package com.nextlabs.destiny.console.services.authorization;

import java.util.List;

import com.nextlabs.destiny.console.model.authorization.AccessControl;

/**
 * @author Sachindra Dasun
 */
public interface AccessControlDataService {

    List<AccessControl> getAccessControlList();

}
