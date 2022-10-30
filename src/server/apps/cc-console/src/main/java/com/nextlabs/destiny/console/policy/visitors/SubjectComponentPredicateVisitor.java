/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 1, 2016
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
import com.bluejungle.pf.domain.destiny.subject.LocationReference;

/**
 * Visitor to walk through Subject Component attributes and other details
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SubjectComponentPredicateVisitor extends DefaultPredicateVisitor {

    private static final Logger log = LoggerFactory
            .getLogger(SubjectComponentPredicateVisitor.class);

    private PredicateData predicateData;

    public SubjectComponentPredicateVisitor(PredicateData predicateData) {
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

        if (lhs instanceof SubjectAttribute || rhs instanceof SubjectAttribute) {
            SubjectAttribute subjectAttribute;
            Constant constVal;

            if (lhs instanceof SubjectAttribute && rhs instanceof Constant) {
                subjectAttribute = (SubjectAttribute) lhs;
                constVal = (Constant) rhs;
                attribute = new Attribute(subjectAttribute.getName(), operator.getName(),
                        constVal.getRepresentation());

            } else if (rhs instanceof SubjectAttribute && lhs instanceof Constant) {
                subjectAttribute = (SubjectAttribute) rhs;
                constVal = (Constant) lhs;
                attribute = new Attribute(constVal.getRepresentation(),
                        operator.getName(), subjectAttribute.getName());
            } else if (lhs instanceof SubjectAttribute && rhs instanceof SubjectAttribute) {
                subjectAttribute = (SubjectAttribute) lhs;
                SubjectAttribute attribValue = (SubjectAttribute) rhs;
                String variableValue = attribValue.getObjectSubTypeName() + "."
                        + attribValue.getName();
                attribute = new Attribute(subjectAttribute.getName(), operator.getName(),
                        variableValue);
            } else if (lhs instanceof SubjectAttribute && rhs instanceof ResourceAttribute) {
                subjectAttribute = (SubjectAttribute) lhs;
                ResourceAttribute attribValue = (ResourceAttribute) rhs;
                String variableValue = attribValue.getObjectTypeName() + "."
                        + attribValue.getObjectSubTypeName() + "."
                        + attribValue.getName();
                attribute = new Attribute(subjectAttribute.getName(), operator.getName(),
                        variableValue);
            } else if (lhs instanceof SubjectAttribute && rhs instanceof LocationReference) {
                subjectAttribute = (SubjectAttribute) lhs;
                LocationReference locationRef = (LocationReference)rhs;

                attribute = new Attribute(subjectAttribute.getName(), operator.getName(), locationRef.getRefLocationName());
            } else {
                log.warn("Invalid subject attribute detail, {}",
                        pred);
            }

            if (attribute != null)
                predicateData.addAttribute(attribute);
        }
    }
}
