/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 1 Aug 2016
 *
 */
package com.nextlabs.destiny.console.dao.authentication.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.authentication.AuthHandlerTypeDetailDao;
import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.model.authentication.AuthHandlerTypeDetail;

/**
 *
 * Authentication Handlers DAO Implementation
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Repository
public class AuthHandlerTypeDetailDaoImpl extends GenericDaoImpl<AuthHandlerTypeDetail, Long>
		implements AuthHandlerTypeDetailDao {

	private static final Logger log = LoggerFactory.getLogger(AuthHandlerTypeDetailDaoImpl.class);

	@PersistenceContext(unitName = MGMT_UNIT)
	private EntityManager entityManager;

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public List<AuthHandlerTypeDetail> findByType(String type) {

		TypedQuery<AuthHandlerTypeDetail> query = entityManager.
				createNamedQuery(AuthHandlerTypeDetail.FIND_BY_TYPE,
				AuthHandlerTypeDetail.class);
		query.setParameter("type", type);
		List<AuthHandlerTypeDetail> authHandlers = query.getResultList();

		log.debug("Auth Handlers found by given type [ Type:{}, No of records: {}]", 
				type, authHandlers.size());
		return authHandlers;

	}
}
