/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 22, 2016
 *
 */
package com.nextlabs.destiny.console.policy.visitors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.pf.domain.destiny.common.SpecReference;
import com.bluejungle.pf.domain.destiny.resource.ResourceAttribute;
import com.bluejungle.pf.domain.destiny.subject.SubjectAttribute;

/**
 * Visitor to walk through Resource Component attributes and other details
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ResourceComponentPredicateVisitor extends DefaultPredicateVisitor {

    private static final Logger log = LoggerFactory
            .getLogger(ResourceComponentPredicateVisitor.class);

    private PredicateData predicateData;

    public ResourceComponentPredicateVisitor(PredicateData predicateData) {
        this.predicateData = predicateData;
    }

    @Override
    public void visit(IPredicateReference pred) {
        SpecReference specReference = (SpecReference) pred;
        Long referenceId = specReference.getReferencedID();
        predicateData.addReference(referenceId);
    }

    @Override
    public void visit(IRelation pred) {
        IExpression lhs = pred.getLHS();
        IExpression rhs = pred.getRHS();
        RelationOp operator = pred.getOp();

        Attribute attribute = null;

        if (lhs instanceof ResourceAttribute
                || rhs instanceof ResourceAttribute) {
            ResourceAttribute resAttr;
            Constant constVal;

            if (lhs instanceof ResourceAttribute && rhs instanceof Constant) {
                resAttr = (ResourceAttribute) lhs;
                constVal = (Constant) rhs;
                attribute = new Attribute(resAttr.getName(), operator.getName(),
                        constVal.getRepresentation());
            } else if (rhs instanceof ResourceAttribute
                    && lhs instanceof Constant) {
                resAttr = (ResourceAttribute) rhs;
                constVal = (Constant) lhs;
                attribute = new Attribute(constVal.getRepresentation(),
                        operator.getName(), resAttr.getName());
            } else if (lhs instanceof ResourceAttribute
                    && rhs instanceof ResourceAttribute) {
                resAttr = (ResourceAttribute) lhs;
                ResourceAttribute attribValue = (ResourceAttribute) rhs;
                String variableValue = attribValue.getObjectTypeName() + "."
                        + attribValue.getObjectSubTypeName() + "."
                        + attribValue.getName();
                attribute = new Attribute(resAttr.getName(), operator.getName(),
                        variableValue);
            } else if (lhs instanceof ResourceAttribute
                    && rhs instanceof SubjectAttribute) {
                resAttr = (ResourceAttribute) lhs;
                SubjectAttribute attribValue = (SubjectAttribute) rhs;
                String variableValue = attribValue.getObjectSubTypeName() + "."
                        + attribValue.getName();
                attribute = new Attribute(resAttr.getName(), operator.getName(),
                        variableValue);
            } else {
                log.warn("Invalid Resource attribute detail, {}",
                        pred);
            }
        }

        if (attribute != null)
            predicateData.addAttribute(attribute);
    }

}
