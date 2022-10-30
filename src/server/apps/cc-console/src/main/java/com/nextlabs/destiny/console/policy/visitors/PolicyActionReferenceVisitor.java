/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 12, 2016
 *
 */
package com.nextlabs.destiny.console.policy.visitors;

import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.pf.domain.destiny.common.SpecReference;

/**
 *
 * Visitor to walk through Policy action component references
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class PolicyActionReferenceVisitor extends DefaultPredicateVisitor {

    private PredicateData predicateData;

    public PolicyActionReferenceVisitor(PredicateData predicateData) {
        this.predicateData = predicateData;
    }

    @Override
    public void visit(IPredicateReference pred) {
        SpecReference specReference = (SpecReference) pred;
        Long referenceId = specReference.getReferencedID();
        predicateData.addReference(referenceId);
    }

}
