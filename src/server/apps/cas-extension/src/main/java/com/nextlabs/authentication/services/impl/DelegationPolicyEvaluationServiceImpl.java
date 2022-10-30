package com.nextlabs.authentication.services.impl;

import static com.bluejungle.pf.engine.destiny.EvaluationResult.ALLOW;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.Multivalue;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.action.DAction;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.domain.epicenter.misc.IObligation;
import com.bluejungle.pf.engine.destiny.EvaluationEngine;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.engine.destiny.PolicyEvaluationException;
import com.nextlabs.authentication.enums.DevelopmentEntityType;
import com.nextlabs.authentication.enums.PolicyModelStatus;
import com.nextlabs.authentication.enums.PolicyModelType;
import com.nextlabs.authentication.enums.UserAttributeKey;
import com.nextlabs.authentication.enums.UserCategory;
import com.nextlabs.authentication.models.ActionConfig;
import com.nextlabs.authentication.models.DevelopmentEntity;
import com.nextlabs.authentication.repositories.ActionConfigRepository;
import com.nextlabs.authentication.repositories.DevelopmentEntityRepository;
import com.nextlabs.authentication.services.DelegationPolicyEvaluationService;

/**
 * Delegation policy evaluation service implementation.
 *
 * @author Sachindra Dasun
 */
@Service
public class DelegationPolicyEvaluationServiceImpl implements DelegationPolicyEvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(DelegationPolicyEvaluationServiceImpl.class);

    private ActionConfigRepository actionConfigRepository;

    private DevelopmentEntityRepository developmentEntityRepository;

    /**
     * Returns the allowed actions for a user with given attributes.
     *
     * @param attributes user attributes
     * @return the set of allowed actions
     */
    @Override
    public Set<String> getAllowedActions(Map<String, Set<String>> attributes) {
        List<String> allActions = actionConfigRepository
                .findByPolicyModelStatusAndPolicyModelTypeIs(PolicyModelStatus.ACTIVE, PolicyModelType.DA_RESOURCE).stream()
                .map(ActionConfig::getShortName)
                .collect(Collectors.toList());
        Set<String> allowedActions = new TreeSet<>();
        // Grant all the permissions to administrator users.
        if (attributes.containsKey(UserAttributeKey.NEXTLABS_CC_USER_CATEGORY.getKey()) &&
                attributes.get(UserAttributeKey.NEXTLABS_CC_USER_CATEGORY.getKey()).contains(UserCategory.ADMIN.name())) {
            allowedActions.addAll(allActions);
        } else {
            List<IDPolicy> resolvedRules = resolveRules();
            Map<Long, Collection<IObligation>> allowedObligations = new HashMap<>();
            for (IDPolicy parsedRule : resolvedRules) {
                DelegationTargetResolver resolver = new DelegationTargetResolver(parsedRule);
                AppUserSubject subject = getSubjectWithAttributes(attributes);
                for (String actionName : allActions) {
                    EvaluationRequest evalRequest = new EvaluationRequest();
                    evalRequest.setRequestId(System.nanoTime());
                    evalRequest.setAction(DAction.getAction(actionName));
                    evalRequest.setUser(subject);
                    EvaluationEngine engine = new EvaluationEngine(resolver);
                    evaluateRule(allowedActions, allowedObligations, resolver, engine, actionName, evalRequest);
                }
            }
        }
        return allowedActions;
    }

    private List<IDPolicy> resolveRules() {
        List<DevelopmentEntity> policies = developmentEntityRepository
                .findByHiddenAndStatusInAndType('N', List.of("DR", "AP"), DevelopmentEntityType.DP);
        List<DevelopmentEntity> components = developmentEntityRepository
                .findByHiddenAndStatusInAndType('N', List.of("DR", "AP"), DevelopmentEntityType.DC);
        try {
            return DelegationRuleReferenceResolver.create(policies, components).resolve();
        } catch (PQLException e) {
            throw new RuntimeException("Error in resolving delegation rule references");
        }
    }

    private AppUserSubject getSubjectWithAttributes(Map<String, Set<String>> attributes) {
        String username = attributes.get(UserAttributeKey.USERNAME.getKey()).stream().findFirst().orElseThrow();
        AppUserSubject subject = new AppUserSubject(username, username,
                attributes.containsKey(UserAttributeKey.DISPLAY_NAME.getKey()) ?
                        attributes.get(UserAttributeKey.DISPLAY_NAME.getKey()).stream().findFirst().orElseThrow() :
                        username,
                attributes.get(UserAttributeKey.NEXTLABS_CC_USER_ID.getKey()).stream().findFirst().map(Long::parseLong).orElseThrow(),
                SubjectType.USER, new DynamicAttributes());
        for (Map.Entry<String, Set<String>> entry : attributes.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                IEvalValue value = entry.getValue().size() == 1 ?
                        EvalValue.build(entry.getValue().iterator().next())
                        : EvalValue.build(Multivalue.create(entry.getValue()));
                subject.setAttribute(entry.getKey().toLowerCase(), value);
            }
        }
        return subject;
    }

    private void evaluateRule(Set<String> allowedActions,
                              Map<Long, Collection<IObligation>> allowedObligations,
                              DelegationTargetResolver resolver, EvaluationEngine engine,
                              String actionName, EvaluationRequest evalRequest) {
        try {
            EvaluationResult evalResult = engine.evaluate(evalRequest);
            if (EvaluationResult.ALLOW.equals(evalResult.getEffectName())) {
                allowedActions.add(actionName);
            }
            logger.debug("DA rule evaluation result [ action:{}, effect: {}]", actionName, evalResult.getEffectName());
            if (ALLOW.equals(evalResult.getEffectName())) {
                BitSet applicables = resolver.getApplicables();
                for (int i = applicables.nextSetBit(0); i >= 0; i = applicables.nextSetBit(i + 1)) {
                    IDPolicy policy = resolver.getPolicies()[i];
                    allowedObligations.put(policy.getId(), policy.getObligations(EffectType.ALLOW));
                }
            }
        } catch (PolicyEvaluationException e) {
            logger.info("Policy evaluation resulted in exception " + e);
        }
    }

    @Autowired
    public void setActionConfigRepository(ActionConfigRepository actionConfigRepository) {
        this.actionConfigRepository = actionConfigRepository;
    }

    @Autowired
    public void setDevelopmentEntityRepository(DevelopmentEntityRepository developmentEntityRepository) {
        this.developmentEntityRepository = developmentEntityRepository;
    }

}
