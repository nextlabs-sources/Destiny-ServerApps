package com.nextlabs.destiny.console.policy.visitors;

import java.util.Deque;

import com.bluejungle.framework.expressions.BooleanOp;
import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.ICompositePredicate;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.pf.domain.destiny.common.SpecReference;
import com.bluejungle.pf.domain.destiny.subject.SubjectAttribute;
import com.nextlabs.destiny.console.enums.Operator;

public class MemberPredicateVisitor extends DefaultPredicateVisitor {

    private final Deque<PredicateData> predicates;
    private boolean skipNextOR = false;

    public MemberPredicateVisitor(Deque<PredicateData> predicates) {
        this.predicates = predicates;
    }

    @Override
    public void visit(ICompositePredicate pred, boolean preorder) {
        if (BooleanOp.OR.equals(pred.getOp()) && !skipNextOR) {
            predicates.add(new PredicateData());
        }
        skipNextOR = false;
        if (BooleanOp.NOT.equals(pred.getOp())) {
            predicates.add(new PredicateData());
            predicates.getLast().setOperator(Operator.NOT);
            skipNextOR = true;
        }
    }

    @Override
    public void visit(IPredicateReference pred) {
        SpecReference specReference = (SpecReference) pred;
        if (predicates.isEmpty()) {
            predicates.add(new PredicateData());
        }
        predicates.getLast().addReference(specReference.getReferencedID());
    }

    @Override
    public void visit(IRelation pred) {
        if (pred.getLHS() instanceof SubjectAttribute && pred.getRHS() instanceof Constant) {
            SubjectAttribute lhs = (SubjectAttribute) pred.getLHS();
            Constant rhs = (Constant) pred.getRHS();
            String attrLhs = String.format("%s.%s", lhs.getObjectSubTypeName(), lhs.getName());
            Attribute attribute = new Attribute(attrLhs, pred.getOp().getName(), rhs.getRepresentation());
            predicates.getLast().addAttribute(attribute);
        }
    }
}
