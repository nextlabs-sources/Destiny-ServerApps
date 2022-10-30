package com.nextlabs.destiny.console.dao.authorization.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.authorization.AccessControlDao;
import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.model.authorization.AccessControl;

/**
 * @author Sachindra Dasun
 */
@Repository
public class AccessControlDaoImpl extends GenericDaoImpl<AccessControl, Long> implements AccessControlDao {

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

}
