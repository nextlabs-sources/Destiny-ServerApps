package com.nextlabs.destiny.console.policy.visitors;

import java.util.Deque;

import org.apache.commons.lang3.StringUtils;

import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.ICompositePredicate;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.framework.expressions.Relation;
import com.bluejungle.framework.expressions.ValueType;
import com.bluejungle.pf.domain.destiny.deployment.AgentAttribute;
import com.nextlabs.destiny.console.model.Agent;

/**
 * @author Sachindra Dasun
 */
public class PolicyDeploymentTargetVisitor extends DefaultPredicateVisitor {
    private Deque<Agent> agents;
    private String type;

    public PolicyDeploymentTargetVisitor(Deque<Agent> agents) {
        this.agents = agents;
    }

    @Override
    public void visit(ICompositePredicate pred, boolean preorder) {
        if (pred.predicateCount() == 2) {
            IPredicate predicate = pred.predicateAt(1);
            if (predicate instanceof Relation) {
                Relation relationPredicate = (Relation) predicate;
                if (AgentAttribute.TYPE.equals(relationPredicate.getLHS())) {
                    IEvalValue evalValue = relationPredicate.getRHS().evaluate(null);
                    if (ValueType.STRING.equals(evalValue.getType())) {
                        type = evalValue.getValue().toString();
                    }
                }
            }
        }
    }

    @Override
    public void visit(IRelation pred) {
        if (AgentAttribute.ID.equals(pred.getLHS()) && StringUtils.isNotEmpty(type)) {
            IEvalValue evalValue = pred.getRHS().evaluate(null);
            if (ValueType.LONG.equals(evalValue.getType())) {
                Object rhsValue = evalValue.getValue();
                if (rhsValue instanceof Long) {
                    Agent agent = new Agent((Long) rhsValue, type);
                    this.agents.add(agent);
                }
            }
        }
    }
}
