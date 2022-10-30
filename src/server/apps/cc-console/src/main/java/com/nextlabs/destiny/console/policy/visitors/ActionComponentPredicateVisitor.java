/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 1, 2016
 *
 */
package com.nextlabs.destiny.console.policy.visitors;

import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.pf.domain.destiny.action.DAction;
import com.bluejungle.pf.domain.destiny.common.SpecReference;

/**
 *
 * Visitor to walk through Action type attributes and other details
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ActionComponentPredicateVisitor extends DefaultPredicateVisitor {

    private PredicateData predicateData;

    public ActionComponentPredicateVisitor(PredicateData predicateData) {
        this.predicateData = predicateData;
    }

    @Override
    public void visit(IPredicateReference pred) {
        SpecReference specReference = (SpecReference) pred;
        Long referenceId = specReference.getReferencedID();
        predicateData.addReference(referenceId);
    }

    @Override
    public void visit(IPredicate pred) {
        if (pred instanceof DAction) {
            DAction action = (DAction) pred;
            predicateData.addAction(action.getName());
        }
    }

}
