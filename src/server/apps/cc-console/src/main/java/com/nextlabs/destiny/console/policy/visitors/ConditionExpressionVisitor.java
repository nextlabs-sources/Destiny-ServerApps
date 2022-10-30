/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 12, 2016
 *
 */
package com.nextlabs.destiny.console.policy.visitors;

import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.ICompositePredicate;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.framework.expressions.IRelation;

/**
 *
 * Visitor to walk through Advanced condition expression to extract details
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ConditionExpressionVisitor extends DefaultPredicateVisitor {

    
    @Override
    public void visit(ICompositePredicate pred, boolean preorder) {
       
        super.visit(pred, preorder);
    }

    @Override
    public void visit(IPredicateReference pred) {
        super.visit(pred);
    }

    @Override
    public void visit(IRelation pred) {
        super.visit(pred);
    }

    @Override
    public void visit(IPredicate pred) {
        super.visit(pred);
    }

    
}
