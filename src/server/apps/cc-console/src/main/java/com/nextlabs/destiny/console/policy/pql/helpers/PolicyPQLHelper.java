/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 11, 2016
 *
 */
package com.nextlabs.destiny.console.policy.pql.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.domain.agenttype.AgentTypeEnumType;
import com.bluejungle.framework.expressions.BooleanOp;
import com.bluejungle.framework.expressions.CompositePredicate;
import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateVisitor;
import com.bluejungle.framework.expressions.PredicateConstants;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.framework.utils.IPair;
import com.bluejungle.pf.destiny.formatter.DomainObjectFormatter;
import com.bluejungle.pf.destiny.lifecycle.DevelopmentStatus;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.common.SpecReference;
import com.bluejungle.pf.domain.destiny.deployment.AgentAttribute;
import com.bluejungle.pf.domain.destiny.exceptions.PolicyExceptions;
import com.bluejungle.pf.domain.destiny.exceptions.PolicyReference;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.misc.Target;
import com.bluejungle.pf.domain.destiny.obligation.CustomObligation;
import com.bluejungle.pf.domain.destiny.obligation.DObligationManager;
import com.bluejungle.pf.domain.destiny.obligation.LogObligation;
import com.bluejungle.pf.domain.destiny.obligation.NotifyObligation;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.policy.PolicyManager;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyReference;
import com.bluejungle.pf.domain.epicenter.misc.IEffectType;
import com.bluejungle.pf.domain.epicenter.misc.IObligation;
import com.nextlabs.destiny.console.dto.common.AgentDTO;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ObligationDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyEnvironmentConfigDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyScheduleConfigDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.Operator;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.Agent;
import com.nextlabs.destiny.console.policy.visitors.PolicyActionReferenceVisitor;
import com.nextlabs.destiny.console.policy.visitors.PolicyDeploymentTargetVisitor;
import com.nextlabs.destiny.console.policy.visitors.PolicyResourceReferenceVisitor;
import com.nextlabs.destiny.console.policy.visitors.PolicySubjectReferenceVisitor;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;

/**
 * Policy related PQL manipulation helper.
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class PolicyPQLHelper {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyPQLHelper.class);

    public static final String RESOURCE_GROUP = "RESOURCE";
    public static final String ACTION_GROUP = "ACTION";
    public static final String SUBJECT_GROUP = "SUBJECT";

    private static final String POLICY_MODEL_ID = "policy_model_id";

    static String pql = "ID 55 STATUS APPROVED "
            + "POLICY \"TEST_POLICIES/TEST_POLICY_1\""
            + "    ATTRIBUTE DOCUMENT_POLICY"
            + "    FOR (TRUE AND (FALSE OR ID 72))"
            + "    ON (TRUE AND (FALSE OR ID 85))"
            + "    TO (TRUE AND (FALSE OR ID 73))"
            + "    BY (((FALSE OR ID 64) AND NOT ((FALSE OR ID 68 OR ID 65 OR ID 67)) AND (FALSE OR ID 142 OR ID 147)) AND (TRUE AND TRUE) AND (TRUE AND (FALSE OR ID 56)))"
            // + " WHERE (TRUE AND (TRUE AND (user.citizenship = \"US\" AND
            // user.locale = \"US\")))"
            + "    WHERE (TRUE AND (TRUE AND (ENVIRONMENT.REMOTE_ACCESS = 1 AND ENVIRONMENT.TIME_SINCE_LAST_HEARTBEAT > 6000 "
            + "           AND CURRENT_TIME.identity >= \"Mar 29, 2016 11:02:00 AM\" AND CURRENT_TIME.time >= \"11:02:00 AM\" AND"
            + "           CURRENT_TIME.time <= \"11:02:00 AM\" AND (CURRENT_TIME.weekday = \"monday\" OR CURRENT_TIME.weekday = \"tuesday\""
            + "           OR CURRENT_TIME.weekday = \"wednesday\" OR CURRENT_TIME.weekday = \"thursday\" OR CURRENT_TIME.weekday = \"friday\") "
            + "           AND CURRENT_TIME.identity <= \"Apr 29, 2016 11:02:48 AM\") AND resource.fso.name = \"test\" AND call_function(\"foo\", \"bar\", 1, 2, 3) = \"no\" AND resource.fso.name = \"test2\")) "
            + "    DO deny" + "    BY DEFAULT DO allow"
            + "    ON allow DO \"Strip Attachments - File Server Adapter\"(\"File Server\", \"SERVER_1\", \"Location\", \"Bottom\", \"Text\", \"The attachments [filename] to this message have been removed for security purpose and made available at the following location:[link].\", \"Link Format\", \"Long\")"
            + "    ON deny DO log";

    static String pql2 = "ID 399 STATUS APPROVED "
            + " POLICY \"TEST_POLICIES/TEST_POLICY_3\""
            // + " ATTRIBUTE DOCUMENT_POLICY"
            // + " ATTRIBUTE POLICY_EXCEPTION"
            + "    FOR (TRUE AND (FALSE OR ID 72))"
            + "    ON (TRUE AND (FALSE OR ID 85))"
            + "    TO (TRUE AND (FALSE OR ID 73))"
            + "    BY ((TRUE AND (FALSE OR ID 142)) AND (TRUE AND TRUE) AND (TRUE AND (FALSE OR ID 56)))"
            + "    SUBPOLICY deny_overrides  \"TEST_POLICIES/TEST_POLICY_3/SUB_POLICY_3\" "
            + "    DO allow"
            + "    ON allow DO \"Policy Accessible Tags\"(\"filters\", \"---~tagsFilters:~- operator: 'IN'~  tags:~  - version: 0~    lastUpdatedDate: null~    createdDate: null~    createdBy: null~    lastUpdatedBy: null~    id: 1152~    key: 'access'~    label: 'access'~    type: null~    status: null~\", \"policy_model_id\", \"5583\")";
    // + " ON deny DO \"test_obj\" (\"filters\",
    // \"{\"tagsFilters\":[{\"operator\":\"IN\",\"tags\":[\"ITAR-1\",\"ITAR-2\",\"ITAR-3\"]},{\"operator\":\"NOT\",\"tags\":[\"ABD\",\"ABD-2\"]}]}\")";
    // + " ON deny DO log, notify \"amila.silva@nextlabs.com\" \"Copy Move
    // Approval - Allow Policy121\"";

    static String pql3 = "POLICY \"TEST_0806/Policy_P0\" "
            + "   ATTRIBUTE DOCUMENT_POLICY"
            + "     TAG \"test-1\"=\"${test-1}\""
            + "     TAG \"oil&Gas\"=\"oil&Gas\"" + "     TAG head=\"head\""
            + "    FOR (TRUE AND (FALSE OR ID 106))"
            + "    ON (TRUE AND (FALSE OR ID 105))" + "    TO (TRUE AND TRUE)"
            + "    BY ((TRUE AND (FALSE OR ID 108)) AND (TRUE AND TRUE) AND (TRUE AND TRUE))"
            + "    DO allow" + "    BY DEFAULT DO allow" + "    ON allow DO log"
            + "    ON deny DO log";

    public static void main(String... a) throws PQLException, ConsoleException {

        PolicyDTO dto = create().fromPQL(pql3);
        dto.getTags().add(
                new TagDTO("hello", "asasdsa", TagType.POLICY_TAG.toString()));
        dto.getTags().add(
                new TagDTO("hello2", "asasdsa", TagType.POLICY_TAG.toString()));
        dto.getTags().add(new TagDTO("helloasdsa!@#", "!@#$$asasdsa",
                TagType.POLICY_TAG.toString()));

        System.out.println(ToStringBuilder.reflectionToString(dto,
                ToStringStyle.MULTI_LINE_STYLE));

        String pll = create().getPQL(123L, "asdsa", "asdsadd", dto);
        System.out.println("::::::: POLL new :" + pll);
        // ObligationDTO objDTO = new ObligationDTO();
        // objDTO.setPolicyModelId(124L);
        // objDTO.setName("Allow 1");
        // objDTO.getParams().put("name", "123");
        // objDTO.getParams().put("name-1", "2343");
        // objDTO.getParams().put("name-2", "456");
        //
        // ObligationDTO objDTO2 = new ObligationDTO();
        // objDTO2.setPolicyModelId(1344L);
        // objDTO2.setName("Allow 2");
        // objDTO2.getParams().put("name 2", "123");
        // objDTO2.getParams().put("name-3", "2343");
        // objDTO2.getParams().put("name-4", "456");
        //
        // dto.getAllowObligations().add(objDTO);
        // dto.getAllowObligations().add(objDTO2);
        //
        // String aks = create().getPQL(1234L, "AMI", "asdsa", dto);
        // PolicyDTO dto2 = create().fromPQL(aks);

        // System.out.println("::::->" + aks);

        // "allowObligations":[{"policyModelId":"138","name":"obligation
        // 2","params":{}},{"policyModelId":"138","name":"obligation
        // 1","params":{"parameter 2":"3","parameter
        // 1":"1"}}],"denyObligations":[{"policyModelId":"138","name":"obligation
        // 1","params":{"parameter 2":"3","parameter 1":"2"}}

    }

    /**
     * Create an instance of {@link PolicyPQLHelper}
     * 
     * @return
     */
    public static PolicyPQLHelper create() {
        return new PolicyPQLHelper();
    }

    public String getPQL(Long id, String title, String description,
            PolicyDTO policyDTO) throws ConsoleException {
        try {
            DomainObjectFormatter formatter = new DomainObjectFormatter();

            CompositePredicate actionPredicate = getPredicate(policyDTO.getCategory(),
                    policyDTO.getActionComponents());
            CompositePredicate fromResourcePredicate = getPredicate(policyDTO.getCategory(),
                    policyDTO.getFromResourceComponents());
            CompositePredicate toResourcePredicate = getPredicate(policyDTO.getCategory(),
                    policyDTO.getToResourceComponents(), policyDTO.isHasToResourceComponents());
            CompositePredicate subjectPredicate = getPredicate(policyDTO.getCategory(),
                    policyDTO.getSubjectComponents());
            CompositePredicate toSubjectPredicate = getPredicate(policyDTO.getCategory(),
                    policyDTO.getToSubjectComponents(), policyDTO.isHasToSubjectComponents());

            DevelopmentStatus devStatus = PolicyDevelopmentStatus
                    .get(policyDTO.getStatus()).getDevValue();

            PolicyManager pm = new PolicyManager();
            IDPolicy policy = pm.newPolicy(id, title);
            policy.setDescription(description);
            policy.setStatus(devStatus);
            policy.setMainEffect(
                    EffectType.getElement(policyDTO.getEffectType()));

            if (EffectType.DENY.getName()
                    .equals(policy.getMainEffect().getName())) {
                policy.setOtherwiseEffect(EffectType.ALLOW);
            }

            addPolicyTagsToPQL(policyDTO, policy);

            policy.setTarget(new Target());
            policy.getTarget().setActionPred(actionPredicate);
            policy.getTarget().setFromResourcePred(fromResourcePredicate);
            policy.getTarget().setToResourcePred(policyDTO.isHasToResourceComponents() ? toResourcePredicate : null);
            policy.getTarget().setSubjectPred(subjectPredicate);
            policy.getTarget().setToSubjectPred(policyDTO.isHasToSubjectComponents() ? toSubjectPredicate : null);

            if (policyDTO.isManualDeploy()) {
                policy.setDeploymentTarget(getDeploymentTargetPredicate(policyDTO.getDeploymentTargets()));
            }

            policy.setConditions(ConditionPredicateHelper.buildConditionPredicate(policyDTO));

            // obligations
            getObligations(policy, EffectType.ALLOW,
                    policyDTO.getAllowObligations());
            getObligations(policy, EffectType.DENY,
                    policyDTO.getDenyObligations());

            // set attributes
            addAttributes(policyDTO, policy);

            // Policy exceptions nor sub policies
            setPolicyExceptions(policyDTO, policy);

            formatter.formatDef(policy);
            String pql = formatter.getPQL();

            log.debug("Policy PQL has been generated, [ PQL :{} ]", pql);

            return pql;
        } catch (Exception e) {
            throw new ConsoleException("Error encountered in generate PQL", e);
        }
    }

    private void addPolicyTagsToPQL(PolicyDTO policyDTO, IDPolicy policy) {
        for (TagDTO tag : policyDTO.getTags()) {
            if (tag.getKey() != null && tag.getLabel() != null) {
                policy.addTag(tag.getKey(), tag.getLabel());
            }
        }
    }

    private void addAttributes(PolicyDTO policyDTO, IDPolicy policy) {
        for (String attribute : policyDTO.getAttributes()) {
            policy.setAttribute(attribute, true);
        }
    }

    private void setPolicyExceptions(PolicyDTO policyDTO, IDPolicy policy) {
        if (!policyDTO.getSubPolicyRefs().isEmpty()) {
            policy.setPolicyExceptions(new PolicyExceptions());
        }
        for (String refId : policyDTO.getSubPolicyRefs()) {
            policy.getPolicyExceptions().addPolicy(new PolicyReference(refId.replace("\"", "\\\"")));
        }
    }

    private void getObligations(IDPolicy policy, IEffectType effectType,
            List<ObligationDTO> obligations) {
        for (ObligationDTO obligation : obligations) {

            if (LogObligation.OBLIGATION_NAME
                    .equalsIgnoreCase(obligation.getName())) {
                LogObligation logObligation = new DObligationManager()
                        .createLogObligation();
                policy.addObligation(logObligation, effectType);

            } else if (NotifyObligation.OBLIGATION_NAME
                    .equalsIgnoreCase(obligation.getName())) {
                String toAddress = obligation.getParams().get("to");
                String message = obligation.getParams().get("message");

                NotifyObligation notifyObligation = new DObligationManager()
                        .createNotifyObligation(toAddress, message);
                policy.addObligation(notifyObligation, effectType);

            } else {
                // rest of the custom obligations
                List<String> args = new LinkedList<>();
                Iterator<String> itr = obligation.getParams().keySet()
                        .iterator();
                while (itr.hasNext()) {
                    String key = itr.next();
                    String value = obligation.getParams().get(key);
                    value = (value == null) ? "" : value;
                    args.add(key);
                    args.add(value);
                }

                // add policy model details as another arg
                if (obligation.getPolicyModelId() != null
                        && obligation.getPolicyModelId() >= 1) {
                    args.add(POLICY_MODEL_ID);
                    args.add(String.valueOf(obligation.getPolicyModelId()));
                }

                CustomObligation customObligation = new CustomObligation(
                        obligation.getName(), args);
                policy.addObligation(customObligation, effectType);
            }
        }
    }

    private CompositePredicate getPredicate(DevEntityType category, List<PolicyComponent> components) {
        return getPredicate(category, components, false);
    }

    private CompositePredicate getPredicate(DevEntityType category, List<PolicyComponent> components, boolean to) {
        CompositePredicate predicate = new CompositePredicate(BooleanOp.AND,
                PredicateConstants.TRUE);
        boolean hasPredicates = populatePredicates(components, predicate);

        if (!hasPredicates) {
            if (to) {
                CompositePredicate toPredicate = new CompositePredicate(BooleanOp.OR, PredicateConstants.FALSE);
                toPredicate.addPredicate(PredicateConstants.TRUE);
                predicate.addPredicate(toPredicate);
            } else {
            	if(DevEntityType.DELEGATION_POLICY == category) {
            		predicate.addPredicate(PredicateConstants.FALSE);
            	} else {
            	    predicate.addPredicate(PredicateConstants.TRUE);
            	}
            }
        }
        return predicate;
    }

    /**
     * Create a composite predicate based on the provided list of deployment targets (agents).
     *
     * @param agents the list of deployment targets (agents)
     * @return the composite predicate composed using the deployment targets (agents)
     */
    private CompositePredicate getDeploymentTargetPredicate(List<AgentDTO> agents) {
        // PQL expect the deployment target types in the below order even when the policy does contain any deployment
        // targets of the type. Hence values are added to a LinkedHashMap and then populated to preserve the
        // order.
        Map<String, List<AgentDTO>> deploymentTargetsMap = new LinkedHashMap<>();
        deploymentTargetsMap.put(AgentTypeEnumType.FILE_SERVER.toString(), new ArrayList<>());
        deploymentTargetsMap.put(AgentTypeEnumType.DESKTOP.toString(), new ArrayList<>());
        deploymentTargetsMap.put(AgentTypeEnumType.PORTAL.toString(), new ArrayList<>());

        agents.forEach(agent -> deploymentTargetsMap.get(agent.getType()).add(agent));

        CompositePredicate predicate = new CompositePredicate(BooleanOp.OR);
        deploymentTargetsMap.forEach((type, deploymentTargets) -> {
            CompositePredicate deploymentTargetPredicate = new CompositePredicate(
                    BooleanOp.AND
            );
            if (deploymentTargets.isEmpty()) {
                // Add always false predicate when there are no deployment targets of the type.
                deploymentTargetPredicate.addPredicate(new CompositePredicate(
                        BooleanOp.OR,
                        PredicateConstants.FALSE,
                        PredicateConstants.FALSE)
                );
            } else {
                CompositePredicate targetsOfTypePredicate = new CompositePredicate(BooleanOp.OR, PredicateConstants.FALSE);
                deploymentTargets.forEach(agent -> targetsOfTypePredicate.addPredicate(
                        AgentAttribute.ID.buildRelation(RelationOp.EQUALS, Constant.build(agent.getId()))));
                deploymentTargetPredicate.addPredicate(targetsOfTypePredicate);
            }
            // The type should be always added as the second predicate to maintain the compatibility.
            deploymentTargetPredicate.addPredicate(AgentAttribute.TYPE.buildRelation(RelationOp.EQUALS, Constant.build(type)));
            predicate.addPredicate(deploymentTargetPredicate);
        });
        return predicate;
    }

    private boolean populatePredicates(List<PolicyComponent> components,
            CompositePredicate predicate) {
        boolean hasPredicates = false;
        for (PolicyComponent component : components) {
            if (component.getComponents().isEmpty()) {
                continue;
            }
            List<IPredicate> refs = getReference(component);
            CompositePredicate refPredicate = new CompositePredicate(
                    BooleanOp.OR, refs);

            if (component.getOperator() == Operator.NOT) {
                refPredicate = new CompositePredicate(BooleanOp.NOT,
                        refPredicate);
            }
            predicate.addPredicate(refPredicate);
            hasPredicates = true;
        }
        return hasPredicates;
    }

    private List<IPredicate> getReference(PolicyComponent component) {
        List<IPredicate> refs = new ArrayList<>();
        refs.add(PredicateConstants.FALSE);
        for (ComponentDTO cmp : component.getComponents()) {
            refs.add(new SpecReference(cmp.getId()));
        }
        return refs;
    }

    /**
     * Create {@link PolicyDTO} from the given PQL
     *
     * @param pql
     * @return {@link PolicyDTO}
     * @throws PQLException
     */
    public PolicyDTO fromPQL(String pql) throws PQLException {

        if (StringUtils.isEmpty(pql)) {
            return null;
        }

        PolicyDTO dto = new PolicyDTO();
        DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);
        IDPolicy policy = domBuilder.processPolicy();

        if (policy == null) {
            return null;
        }

        String fullName = policy.getName();
        String[] splits = fullName.split("/", -1);
        String name = splits[splits.length - 1];

        dto.setId(policy.getId());
        dto.setName(name);
        dto.setFullName(policy.getName());
        dto.setDescription(policy.getDescription());
        dto.setEffectType(policy.getMainEffect().getName());
        dto.setStatus(policy.getStatus().getName());

        addPolicyPQLTags(dto, policy);

        PredicateData actionPred = getActionPredicates(policy);
        List<PredicateData> fromResourcePreds = getFromResourcePredicates(
                policy);
        List<PredicateData> toResourcePreds = getToResourcePredicates(policy);
        List<PredicateData> subjectPreds = getSubjectPredicates(policy);
        List<PredicateData> toSubjectPreds = getToSubjectPredicates(policy);

        if (!actionPred.getReferenceIds().isEmpty()) {
            PolicyComponent actionComponent = populateComponent(actionPred);
            dto.getActionComponents().add(actionComponent);
        }

        if (!fromResourcePreds.isEmpty()) {
            List<PolicyComponent> components = populateComponentRefs(
                    fromResourcePreds);
            dto.getFromResourceComponents().addAll(components);
        }

        if (toResourcePreds != null) {
            dto.setHasToResourceComponents(true);
            if (!toResourcePreds.isEmpty()) {
                List<PolicyComponent> components = populateComponentRefs(
                        toResourcePreds);
                dto.getToResourceComponents().addAll(components);
            }
        }

        if (!subjectPreds.isEmpty()) {
            List<PolicyComponent> components = populateComponentRefs(
                    subjectPreds);
            dto.getSubjectComponents().addAll(components);
        }

        if (toSubjectPreds != null) {
            dto.setHasToSubjectComponents(true);
            if (!toSubjectPreds.isEmpty()) {
                List<PolicyComponent> components = populateComponentRefs(
                        toSubjectPreds);
                dto.getToSubjectComponents().addAll(components);
            }
        }

        if (policy.getConditions() != null) {
            IPredicate predicate = policy.getConditions();
            // populate environment config
            PolicyEnvironmentConfigDTO envConfig = ConditionPredicateHelper.getEnvConfig(predicate);
            dto.setEnvironmentConfig(envConfig);

            // populate schedule config
            PolicyScheduleConfigDTO scheduleConfig = ConditionPredicateHelper.getScheduleConfig(predicate);
            dto.setScheduleConfig(scheduleConfig);

            // populate expression
            String conditionPQL = ConditionPredicateHelper.getFreeTypeExpressionPQL(predicate);
            dto.setExpression(conditionPQL);
        }

        List<ObligationDTO> allowObligationDTOs = populateObligations(policy,
                EffectType.ALLOW);
        List<ObligationDTO> denyObligationDTOs = populateObligations(policy,
                EffectType.DENY);

        dto.setAllowObligations(allowObligationDTOs);
        dto.setDenyObligations(denyObligationDTOs);
        dto.getAttributes().addAll(policy.getAttributes());

        IPredicate deploymentTargetPredicate = policy.getDeploymentTarget();
        if (deploymentTargetPredicate != null) {
            LinkedList<Agent> agents = new LinkedList<>();
            deploymentTargetPredicate.accept(new PolicyDeploymentTargetVisitor(agents), IPredicateVisitor.PREORDER);
            dto.setManualDeploy(true);
            dto.setDeploymentTargets(agents.stream().map(AgentDTO::getDTO).collect(Collectors.toList()));
        }

        populateSubPolicies(dto, policy);
        return dto;
    }

    private void addPolicyPQLTags(PolicyDTO dto, IDPolicy policy) {
        for (IPair<String, String> tag : policy.getTags()) {
            dto.getTags().add(new TagDTO(tag.first(), tag.second(),
                    TagType.POLICY_TAG.name()));
        }
    }

    private void populateSubPolicies(PolicyDTO dto, IDPolicy policy) {
        if (policy.getPolicyExceptions() != null) {
            for (IPolicyReference reference : policy.getPolicyExceptions()
                    .getPolicies()) {
                if (reference != null) {
                    dto.getSubPolicyRefs().add(reference.getReferencedName());
                }
            }
        }
    }

    private List<ObligationDTO> populateObligations(IDPolicy policy,
            IEffectType effectType) {
        List<ObligationDTO> obligationDTOs = new LinkedList<>();
        IObligation[] obligations = policy.getObligationArray(effectType);

        for (IObligation obligation : obligations) {
            if (obligation instanceof CustomObligation) {
                CustomObligation cusObg = (CustomObligation) obligation;
                ObligationDTO objDto = new ObligationDTO();
                objDto.setName(cusObg.getCustomObligationName());
                List<? extends Object> args = cusObg.getCustomObligationArgs();
                if (!args.isEmpty()) {
                    Iterator<? extends Object> itr = args.iterator();
                    while (itr.hasNext()) {
                        String key = String.valueOf(itr.next());
                        String value = itr.hasNext()
                                ? String.valueOf(itr.next()) : "";
                        objDto.getParams().put(key, value);
                    }

                    String policyModelId = objDto.getParams()
                            .get(POLICY_MODEL_ID);
                    if (policyModelId != null) {
                        objDto.setPolicyModelId(Long.valueOf(policyModelId));
                        objDto.getParams().remove(POLICY_MODEL_ID);
                    }
                }
                obligationDTOs.add(objDto);

            } else if (obligation instanceof LogObligation) {
                LogObligation logObg = (LogObligation) obligation;
                ObligationDTO objDto = new ObligationDTO();
                objDto.setName(logObg.getType());
                obligationDTOs.add(objDto);
            } else if (obligation instanceof NotifyObligation) {
                NotifyObligation notify = (NotifyObligation) obligation;
                ObligationDTO objDto = new ObligationDTO();
                objDto.setName(notify.getType());
                objDto.getParams().put("to", notify.getEmailAddresses());
                objDto.getParams().put("message", notify.getBody());
                obligationDTOs.add(objDto);
            }
        }

        return obligationDTOs;
    }

    private List<PolicyComponent> populateComponentRefs(
            List<PredicateData> predicates) {
        List<PolicyComponent> components = new LinkedList<>();
        for (PredicateData predicateData : predicates) {
            PolicyComponent component = populateComponent(predicateData);
            components.add(component);
        }
        return components;
    }

    private PolicyComponent populateComponent(PredicateData predicateData) {
        PolicyComponent component = new PolicyComponent();
        Operator operator = (predicateData.getOperator() == null) ? Operator.IN
                : predicateData.getOperator();
        component.setOperator(operator);
        for (Long refId : predicateData.getReferenceIds()) {
            component.getComponents().add(new ComponentDTO(refId));
        }
        return component;
    }

    private List<PredicateData> getToSubjectPredicates(IDPolicy policy) {
        LinkedList<PredicateData> predicates = null;
        IPredicate toSubjectPred = policy.getTarget().getToSubjectPred();
        if (toSubjectPred != null) {
            predicates = new LinkedList<>();
            toSubjectPred.accept(new PolicySubjectReferenceVisitor(predicates),
                    IPredicateVisitor.PREORDER);
        }
        return removeEmpty(predicates);
    }

    private List<PredicateData> getSubjectPredicates(IDPolicy policy) {
        LinkedList<PredicateData> predicates = new LinkedList<>();
        IPredicate subjectPred = policy.getTarget().getSubjectPred();
        if (subjectPred != null) {
            PredicateData subjectPredicateData = new PredicateData();
            subjectPred.accept(new PolicySubjectReferenceVisitor(predicates),
                    IPredicateVisitor.PREORDER);
            predicates.add(subjectPredicateData);
        }
        return removeEmpty(predicates);
    }

    private List<PredicateData> getToResourcePredicates(IDPolicy policy) {
        LinkedList<PredicateData> predicates = null;
        IPredicate toResourcePred = policy.getTarget().getToResourcePred();
        if (toResourcePred != null) {
            predicates = new LinkedList<>();
            toResourcePred.accept(
                    new PolicyResourceReferenceVisitor(predicates),
                    IPredicateVisitor.PREORDER);
        }
        return removeEmpty(predicates);
    }

    private List<PredicateData> getFromResourcePredicates(IDPolicy policy) {
        LinkedList<PredicateData> predicates = new LinkedList<>();
        IPredicate fromResourcePred = policy.getTarget().getFromResourcePred();
        if (fromResourcePred != null) {
            fromResourcePred.accept(
                    new PolicyResourceReferenceVisitor(predicates),
                    IPredicateVisitor.PREORDER);
        }
        return removeEmpty(predicates);
    }

    private PredicateData getActionPredicates(IDPolicy policy) {
        PredicateData actionPredicateData = new PredicateData();
        IPredicate actionPred = policy.getTarget().getActionPred();
        if (actionPred != null) {
            actionPred.accept(
                    new PolicyActionReferenceVisitor(actionPredicateData),
                    IPredicateVisitor.PREORDER);
        }
        return actionPredicateData;
    }

    private LinkedList<PredicateData> removeEmpty(
            LinkedList<PredicateData> predicateData) {
        if (predicateData != null) {
            predicateData.removeIf(predicate -> predicate.getReferenceIds().isEmpty());
        }
        return predicateData;
    }

}
