package com.nextlabs.authentication.services.impl;

import java.util.BitSet;

import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.ITargetResolver;

/**
 * Delegation target resolver implementation.
 *
 * @author Sachindra Dasun
 */
public class DelegationTargetResolver implements ITargetResolver {

    private final IDPolicy[] policies;
    private BitSet applicables;

    public DelegationTargetResolver(IDPolicy parsedPolicy) {
        this.policies = new IDPolicy[]{parsedPolicy};
    }

    @Override
    public IDPolicy[] getPolicies() {
        return policies;
    }

    @Override
    public BitSet getApplicablePolicies(EvaluationRequest request) {
        this.applicables = getApplicables(this.policies, request);
        return (BitSet) applicables.clone();
    }

    public BitSet getApplicables(IDPolicy[] policies, EvaluationRequest request) {
        BitSet applicables = new BitSet(policies.length);
        int i = 0;
        for (IDPolicy policy : policies) {
            applicables.set(i, policy.getTarget().getActionPred().match(request));
            i++;
        }
        return applicables;
    }

    public BitSet getApplicables() {
        return applicables;
    }

}