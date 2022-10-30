/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 12, 2016
 *
 */
package com.nextlabs.destiny.console.policy.visitors;

import java.util.LinkedList;

import com.bluejungle.framework.expressions.BooleanOp;
import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.ICompositePredicate;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.pf.domain.destiny.common.SpecReference;
import com.nextlabs.destiny.console.enums.Operator;

/**
 *
 * Visitor to walk through Policy resource component references
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class PolicyResourceReferenceVisitor extends DefaultPredicateVisitor {

    private LinkedList<PredicateData> predicates;
    private boolean skipNextOR = false;

    public PolicyResourceReferenceVisitor(
            LinkedList<PredicateData> predicates) {
        this.predicates = predicates;
    }

    @Override
    public void visit(ICompositePredicate pred, boolean preorder) {
        if (BooleanOp.OR.equals(pred.getOp()) && !skipNextOR) {
            predicates.add(new PredicateData());
        }

        if (skipNextOR) {
            skipNextOR = false;
        }

        if (BooleanOp.NOT.equals(pred.getOp())) {
            predicates.add(new PredicateData());
            predicates.getLast().setOperator(Operator.NOT);
            skipNextOR = true;
        }
    }

    @Override
    public void visit(IPredicateReference pred) {
        SpecReference specReference = (SpecReference) pred;
        Long referenceId = specReference.getReferencedID();
        predicates.getLast().addReference(referenceId);
    }

    public LinkedList<PredicateData> getPredicates() {
        return predicates;
    }
    

}
