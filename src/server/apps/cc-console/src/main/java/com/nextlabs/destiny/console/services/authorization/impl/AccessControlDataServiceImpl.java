package com.nextlabs.destiny.console.services.authorization.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.dao.authorization.AccessControlDao;
import com.nextlabs.destiny.console.model.authorization.AccessControl;
import com.nextlabs.destiny.console.services.authorization.AccessControlDataService;

/**
 * @author Sachindra Dasun
 */
@Service
public class AccessControlDataServiceImpl implements AccessControlDataService {

    @Autowired
    private AccessControlDao accessControlDao;

    @Override
    public List<AccessControl> getAccessControlList() {
        return accessControlDao.findAll();
    }

}
