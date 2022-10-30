/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 10, 2016
 *
 */
package com.nextlabs.destiny.console.policy.pql.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.framework.expressions.BooleanOp;
import com.bluejungle.framework.expressions.CompositePredicate;
import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IMultivalue;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateVisitor;
import com.bluejungle.framework.expressions.Multivalue;
import com.bluejungle.framework.expressions.PredicateConstants;
import com.bluejungle.framework.expressions.Relation;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.pf.destiny.formatter.DomainObjectFormatter;
import com.bluejungle.pf.destiny.lifecycle.DevelopmentStatus;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.action.DAction;
import com.bluejungle.pf.domain.destiny.common.IDSpec;
import com.bluejungle.pf.domain.destiny.common.SpecAttribute;
import com.bluejungle.pf.domain.destiny.common.SpecBase;
import com.bluejungle.pf.domain.destiny.common.SpecReference;
import com.bluejungle.pf.domain.destiny.resource.ResourceAttribute;
import com.bluejungle.pf.domain.destiny.subject.SubjectAttribute;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.domain.epicenter.common.SpecType;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentConditionDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.MemberCondition;
import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.Operator;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.policy.visitors.ActionComponentPredicateVisitor;
import com.nextlabs.destiny.console.policy.visitors.Attribute;
import com.nextlabs.destiny.console.policy.visitors.MemberPredicateVisitor;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;
import com.nextlabs.destiny.console.policy.visitors.ResourceComponentPredicateVisitor;
import com.nextlabs.destiny.console.policy.visitors.SubjectComponentPredicateVisitor;

/**
 * Component related PQL manipulation helper.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ComponentPQLHelper {

    private static final Logger log = LoggerFactory
            .getLogger(ComponentPQLHelper.class);

    public static final String DOT = ".";
    public static final String RESOURCE_GROUP = "RESOURCE";
    public static final String ACTION_GROUP = "ACTION";
    public static final String SUBJECT_GROUP = "SUBJECT";
    public static final String MEMBER_GROUP = "MEMBER";
    public static final String CONSTANT = "CONSTANT";
    public static final String RESOURCE_TYPE_PREFIX = SpecType.RESOURCE.getName() + DOT;
    public static final String USER_TYPE_PREFIX = SpecType.USER.getName() + DOT;
    public static final String HOST_TYPE_PREFIX = SpecType.HOST.getName() + DOT;
    public static final String APPLICATION_TYPE_PREFIX = SpecType.APPLICATION.getName() + DOT;
    public static final String VARIABLE_VALUE_PREFIX = "${";
    public static final String VARIABLE_VALUE_SUFFIX = "}";
    public static final String MULTI_VALUE_PREFIX = "[";
    public static final String MULTI_VALUE_SUFFIX = "]";
    public static final String USER = "USER";
    public static final String USER_GROUP = "USER_GROUP";
    public static final String HOST = "HOST";
    public static final String HOST_GROUP = "HOST_GROUP";
    public static final String APPLICATION = "APPLICATION";

    private static final String COMPONENT_PQL = "ID 102440 STATUS APPROVED"
            + " COMPONENT \"SUBJECT/Alphabet-Export Control6\" = ((TRUE AND TRUE) AND"
            + " (user.fso.\"identifier\" = user.fso.\"name.ss.ss\" AND user.lk.\"identifier\" = \"5\" AND TRUE))";

    public static void main(String[] a) throws PQLException {
        ComponentDTO component = ComponentPQLHelper.create()
                .fromPQL(COMPONENT_PQL);

        PolicyModel pm = new PolicyModel();
        pm.setShortName("fso");
        pm.setType(PolicyModelType.SUBJECT);
        String pql = ComponentPQLHelper.create().getPQL(100L, "TEST-ATTR", "",
                SpecType.RESOURCE, component, pm);

        System.out.println("PQL:-->" + pql);

    }

    /**
     * Create an instance of {@link ComponentPQLHelper}
     * 
     * @return
     */
    public static ComponentPQLHelper create() {
        return new ComponentPQLHelper();
    }

    public String getPQL(Long id, String title, String description,
            SpecType specType, ComponentDTO componentDTO,
            PolicyModel policyModel) {
        String policyModelType = (policyModel != null)
                ? policyModel.getShortName().toLowerCase() : "dyn";

        PolicyModelType componentGrp = (policyModel != null)
                ? policyModel.getType() : PolicyModelType.RESOURCE;

        return getPQL(id, title, description, specType, componentDTO,
                policyModelType, componentGrp, policyModel);
    }

    public String getPQL(Long id, String title, String description,
            SpecType specType, ComponentDTO componentDTO,
            PolicyModel policyModel, PolicyModelType componentGroup) {
        String policyModelType = (policyModel != null)
                ? policyModel.getShortName().toLowerCase() : "dyn";

        return getPQL(id, title, description, specType, componentDTO,
                policyModelType, componentGroup, policyModel);
    }

    public String getPQL(Long id, String title, String description,
            SpecType specType, ComponentDTO componentDTO,
            String policyModelType, PolicyModelType componentGroup, PolicyModel policyModel) {

        DomainObjectFormatter formatter = new DomainObjectFormatter();

        String modelType = (policyModelType != null)
                ? policyModelType.toLowerCase() : "dyn";

        // Conditions
        List<IPredicate> relationExpressions = getExpressions(componentDTO,
                componentGroup, modelType, policyModel);
        CompositePredicate relationPredicate = new CompositePredicate(
                BooleanOp.AND, relationExpressions);
        
        //Members and sub-components
        IPredicate memberPredicate = getMemberConditionsPredicate(componentDTO.getMemberConditions());

        // Actions
        List<IPredicate> actions = getActions(componentDTO);

        IPredicate predicate;
        if (actions.isEmpty()) {
            predicate = new CompositePredicate(BooleanOp.AND, memberPredicate, relationPredicate);
        } else {
            predicate = actions.size() > 1 ? new CompositePredicate(BooleanOp.OR, actions) : actions.get(0);
            predicate = new CompositePredicate(BooleanOp.AND, memberPredicate, predicate);
        }

        DevelopmentStatus devStatus = PolicyDevelopmentStatus
                .get(componentDTO.getStatus()).getDevValue();

        SpecBase specBase = new SpecBase(null, specType, id, title, description,
                devStatus, predicate, false);

        formatter.formatDef(specBase);
        String pql = formatter.getPQL();

        log.debug("Component PQL has been generated, [ PQL :{} ]", pql);

        return pql;
    }

    private List<IPredicate> getActions(ComponentDTO componentDTO) {
        List<IPredicate> actions = new ArrayList<>();
        for (String actionStr : componentDTO.getActions()) {
            DAction action = DAction.getAction(actionStr);
            actions.add(action);
        }
        return actions;
    }

    /**
     * Extract Predicate data from the given PQL
     * 
     * @param pql
     * @param componentGroup
     * @return {@link PredicateData}
     * @throws PQLException
     */
    public PredicateData getPredicates(IDSpec spec, String pql,
            String componentGroup) throws PQLException {
        PredicateData predicateData = new PredicateData();

        if (RESOURCE_GROUP.equals(componentGroup)) {
            spec.accept(new ResourceComponentPredicateVisitor(predicateData),
                    IPredicateVisitor.POSTORDER);
        } else if (ACTION_GROUP.equals(componentGroup)) {
            spec.accept(new ActionComponentPredicateVisitor(predicateData),
                    IPredicateVisitor.POSTORDER);
        } else if (SUBJECT_GROUP.equals(componentGroup)) {
            spec.accept(new SubjectComponentPredicateVisitor(predicateData),
                    IPredicateVisitor.POSTORDER);
        }
        return predicateData;
    }

    /**
     * Create {@link ComponentDTO} from the given PQL
     * 
     * @param pql
     * @return
     * @throws PQLException
     */
    public ComponentDTO fromPQL(String pql) throws PQLException {
        if (StringUtils.isEmpty(pql)) {
            return null;
        }
        ComponentDTO component = new ComponentDTO();
        DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);        
        IDSpec spec = domBuilder.processSpec();

        String[] splits = spec.getName().split("/", -1);
        int length = splits.length;
        String componentGroup = splits[0];
        String name = (length < 2) ? "" : splits[length - 1];

        DevelopmentStatus status = spec.getStatus();
        component.setId(spec.getId());
        component.setName(name);
        component.setDescription(spec.getDescription());
        component.setStatus(status.getName());
        component.setType(componentGroup);

        if(spec.getPredicate() instanceof CompositePredicate) {
            CompositePredicate specPredicate = ((CompositePredicate) spec.getPredicate());
            if (specPredicate.predicateCount() > 0) {
                List<PredicateData> memberPredicates = getMemberPredicate(specPredicate.predicateAt(0));
                component.getMemberConditions().addAll(populateMemberRefs(memberPredicates));
                specPredicate.removePredicate(0);
            }
        }

        PredicateData predicateData = getPredicates(spec, pql, componentGroup);
        if (!predicateData.getActions().isEmpty()) {
            for (String action : predicateData.getActions()) {
                component.getActions().add(action);
            }
        }

        if (!predicateData.getAttributes().isEmpty()) {
            for (Attribute attribute : predicateData.getAttributes()) {
                ComponentConditionDTO condition = new ComponentConditionDTO(
                        attribute.getLhs(), attribute.getOperator(),
                        attribute.getRhs(), true);
                component.getConditions().add(condition);
            }
        }
        return component;
    }

        
	private List<PredicateData> getMemberPredicate(IPredicate membersPred) {
		LinkedList<PredicateData> predicates = new LinkedList<>();
		if (membersPred != null) {
			membersPred.accept(new MemberPredicateVisitor(predicates), IPredicateVisitor.PREORDER);
		}
		return predicates;
	}

    private List<MemberCondition> populateMemberRefs(List<PredicateData> predicates) {
        List<MemberCondition> members = new LinkedList<>();
        for (PredicateData predicateData : predicates) {
            members.add(populateMember(predicateData));
        }
        return members;
    }

    private MemberCondition populateMember(PredicateData predicateData) {
        MemberCondition memberCondition = new MemberCondition();
        Operator operator = (predicateData.getOperator() == null) ? Operator.IN : predicateData.getOperator();
        memberCondition.setOperator(operator);
        for (Long refId : predicateData.getReferenceIds()) {
            memberCondition.getMembers().add(new MemberDTO(refId, DevEntityType.COMPONENT.name()));
        }
        for (Attribute attribute : predicateData.getAttributes()) {
            MemberDTO member = new MemberDTO();
            member.setId(Long.valueOf(attribute.getRhs()));
            member.setType(MEMBER_GROUP);
            String attributeLHS = attribute.getLhs();
            String attributeLHSType = attributeLHS.split("\\.")[0];
            member.setMemberType(attributeLHS.contains("group") ? (attributeLHSType + "_group").toUpperCase()
                    : attributeLHSType.toUpperCase());
            memberCondition.getMembers().add(member);
        }
        return memberCondition;
    }

    public IPredicate getMemberConditionsPredicate(List<MemberCondition> memberConditions) {

        List<IPredicate> memberConditionPredicates = new ArrayList<>();
        for (MemberCondition memberCondition : memberConditions) {
            List<IPredicate> memberPredicates = getMemberPredicates(memberCondition);
            if (!memberPredicates.isEmpty()) {

                IPredicate memberConditionPredicate = memberPredicates.size() == 1 ?
                        new CompositePredicate(BooleanOp.OR, PredicateConstants.FALSE, memberPredicates.get(0)) :
                        new CompositePredicate(BooleanOp.OR, memberPredicates);
                if (memberCondition.getOperator() == Operator.NOT) {
                    memberConditionPredicate = new CompositePredicate(BooleanOp.NOT, memberConditionPredicate);
                }
                memberConditionPredicates.add(memberConditionPredicate);
            }
        }

        if (!memberConditionPredicates.isEmpty()) {
            return memberConditionPredicates.size() == 1 ? new CompositePredicate(BooleanOp.AND,
                    PredicateConstants.TRUE,
                    memberConditionPredicates.get(0)) :
                    new CompositePredicate(BooleanOp.AND, memberConditionPredicates);
        }

        return new CompositePredicate(BooleanOp.AND, PredicateConstants.TRUE, PredicateConstants.TRUE);
    }

    private List<IPredicate> getMemberPredicates(MemberCondition memberCondition) {
        List<IPredicate> memberPredicates = new ArrayList<>();
        if (!memberCondition.getMembers().isEmpty()) {
            memberPredicates.add(PredicateConstants.FALSE);
        }
        for (MemberDTO member : memberCondition.getMembers()) {
            if (ComponentPQLHelper.MEMBER_GROUP.equals(member.getType())) {
                RelationOp op = RelationOp.EQUALS;
                SpecAttribute lhs = populateMemberLHS(member.getMemberType());
                IExpression rhs = Constant.build(member.getId());
                memberPredicates.add(new Relation(op, lhs, rhs));
            } else {
                memberPredicates.add(new SpecReference(member.getId()));
            }
        }
        if (memberPredicates.size() > 2) {
            memberPredicates.remove(0);
        }
        return memberPredicates;
    }

    private SpecAttribute populateMemberLHS(String memberType) {
        switch (memberType.toUpperCase()) {
            case USER:
                return SubjectAttribute.USER_ID;
            case USER_GROUP:
                return SubjectAttribute.USER_LDAP_GROUP_ID;
            case HOST:
                return SubjectAttribute.HOST_ID;
            case HOST_GROUP:
                return SubjectAttribute.HOST_LDAP_GROUP_ID;
            case APPLICATION:
                return SubjectAttribute.APP_ID;
            default:
                return null;
        }
    }
	
    public List<IPredicate> getExpressions(ComponentDTO componentDTO,
            PolicyModelType componentGroup, String modelType, PolicyModel policyModel) {
        List<IPredicate> relationExpressions = new ArrayList<>();
        relationExpressions.add(PredicateConstants.TRUE);

        for (ComponentConditionDTO condition : componentDTO.getConditions()) {
            RelationOp relationOp = RelationOp
                    .getElement(condition.getOperator());
            SpecAttribute lhs;
            IExpression rhs = null;
            
            // Default to USER, which is previously hard coded value
            SubjectType subjectType = SubjectType.USER;
            
            if(modelType.equalsIgnoreCase(SubjectType.APP.getName())) {
            	subjectType = SubjectType.APP;
            } else if(modelType.equalsIgnoreCase(SubjectType.HOST.getName())) {
            	subjectType = SubjectType.HOST;
            }

            if (SUBJECT_GROUP.equals(componentGroup.name())) {
                lhs = SubjectAttribute.forNameAndType(condition.getAttribute().toLowerCase(), subjectType);
            } else {
                lhs = ResourceAttribute.forNameAndType(condition.getAttribute().toLowerCase(), modelType);
            }

            if (SUBJECT_GROUP.equals(condition.getRhsType())) {
                String subAttr = condition.getRHSValue();
                if (subAttr.startsWith(USER_TYPE_PREFIX)) {
                    subAttr = subAttr.substring(USER_TYPE_PREFIX.length()).toLowerCase();
                    rhs = SubjectAttribute.forNameAndType(subAttr, SubjectType.USER);
                } else if (subAttr.startsWith(HOST_TYPE_PREFIX)) {
                    subAttr = subAttr.substring(HOST_TYPE_PREFIX.length()).toLowerCase();
                    rhs = SubjectAttribute.forNameAndType(subAttr, SubjectType.HOST);
                } else if (subAttr.startsWith(APPLICATION_TYPE_PREFIX)) {
                    subAttr = subAttr.substring(APPLICATION_TYPE_PREFIX.length()).toLowerCase();
                    rhs = SubjectAttribute.forNameAndType(subAttr, SubjectType.APP);
                }
            } else if (RESOURCE_GROUP.equals(condition.getRhsType())) {
                String resAttr = condition.getRHSValue();
                resAttr = resAttr.substring(RESOURCE_TYPE_PREFIX.length());
                String subtype = resAttr.substring(0, resAttr.indexOf(DOT));
                resAttr = resAttr.substring(resAttr.indexOf(DOT) + 1);
                rhs = ResourceAttribute.forNameAndType(resAttr, subtype);
            } else {
                String value = condition.getRHSValue();
                DataType dataType = getDataType(policyModel, condition.getAttribute());

                if(dataType != null) {
                    if (DataType.NUMBER.equals(dataType)) {
                        long numValue = Long.valueOf(value);
                        rhs = Constant.build(numValue);
                    } else if (DataType.MULTIVAL.equals(dataType)) {
                        Object objValue = JSONValue.parse(value);
                        ArrayList<Object> values = new ArrayList<>();
                        if (objValue instanceof JSONArray) {
                            for (Object o : (JSONArray) objValue) {
                                values.add(o);
                            }
                        } else {
                            values.add(value);
                            value = JSONArray.toJSONString(values);
                        }
                        IMultivalue multiValue = Multivalue.create(values);
                        rhs = Constant.build(multiValue, value);
                    } else if (DataType.DATE.equals(dataType)) {
                        long dateLongValue = Long.valueOf(value);
                        Date dateValue = new Date(dateLongValue);
                        rhs = Constant.build(dateValue);
                    } else {
                        rhs = Constant.build(value);
                    }
                } else {
                    if(value!= null
                        && value.startsWith(MULTI_VALUE_PREFIX)
                        && value.endsWith(MULTI_VALUE_SUFFIX)) {
                        Object objValue = JSONValue.parse(value);
                        ArrayList<Object> values = new ArrayList<>();
                        if (objValue instanceof JSONArray) {
                            for (Object o : (JSONArray) objValue) {
                                values.add(o);
                            }
                        } else {
                            values.add(value);
                            value = JSONArray.toJSONString(values);
                        }
                        IMultivalue multiValue = Multivalue.create(values);
                        rhs = Constant.build(multiValue, value);
                    } else {
                        rhs = Constant.build(value);
                    }
                }
            }
            relationExpressions.add(new Relation(relationOp, lhs, rhs));
        }

        if (componentDTO.getConditions().isEmpty()) {
            relationExpressions.add(PredicateConstants.TRUE);
        }
        return relationExpressions;
    }

    /**
     * Find the attribute type by considering both user defined attributes and pre-seeded attributes.
     *
     * @param policyModel   Policy Model
     * @param attributeName Name of the attribute
     * @return the attribute type
     */
    private DataType getDataType(PolicyModel policyModel, String attributeName) {
        if (policyModel != null) {
            for (AttributeConfig attribute : policyModel.getAttributes()) {
                if (attributeName.equalsIgnoreCase(attribute.getShortName())) {
                    return attribute.getDataType();
                }
            }
            for (AttributeConfig attribute : policyModel.getExtraSubjectAttributes()) {
                if (attributeName.equalsIgnoreCase(attribute.getShortName())) {
                    return attribute.getDataType();
                }
            }
        }
        return null;
    }
}
