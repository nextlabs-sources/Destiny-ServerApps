/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.services.policy;

import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import org.springframework.data.domain.Page;

/**
 *
 * Policy Search Service interface
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
public interface XacmlPolicySearchService {

    /**
     * Find all the xacml policies
     * 
     * @throws ConsoleException
     */
    Page<XacmlPolicyLite> findAllXacmlPolicies();
    /**
     * Re-Index all the policies
     *
     * @throws ConsoleException
     */
    void reIndexAllXacmlPolicies() throws ConsoleException;

    /**
     * @param entity
     * @throws ConsoleException 
     */
    void reIndexXacmlPolicy(PolicyDevelopmentEntity entity) throws ConsoleException;

}
