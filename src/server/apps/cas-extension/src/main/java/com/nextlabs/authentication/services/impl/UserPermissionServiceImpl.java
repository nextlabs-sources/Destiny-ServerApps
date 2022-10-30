package com.nextlabs.authentication.services.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.services.DelegationPolicyEvaluationService;
import com.nextlabs.authentication.services.UserPermissionService;

/**
 * Return the set of permissions granted for the user.
 *
 * @author Sachindra Dasun
 */
@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private DelegationPolicyEvaluationService delegationPolicyEvaluationService;

    @Autowired
    public void setDelegationPolicyEvaluationService(DelegationPolicyEvaluationService delegationPolicyEvaluationService) {
        this.delegationPolicyEvaluationService = delegationPolicyEvaluationService;
    }

    @Override
    public Set<String> getPermissions(Map<String, Set<String>> attributes) {
        return delegationPolicyEvaluationService.getAllowedActions(attributes);
    }

}
