package com.nextlabs.destiny.console.dao.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.AgentDao;
import com.nextlabs.destiny.console.model.Agent;

/**
 * @author Sachindra Dasun
 */
@Repository
public class AgentDaoImpl extends GenericDaoImpl<Agent, Long> implements AgentDao {

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public List<Agent> find(List<String> types, String value) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Agent> query = builder.createQuery(Agent.class);
        Root<Agent> agent = query.from(Agent.class);
        query.multiselect(agent.get(Agent.ATTRIBUTE_ID), agent.get(Agent.ATTRIBUTE_HOST), agent.get(Agent.ATTRIBUTE_TYPE));
        List<Predicate> predicates = new ArrayList<>();
        // Return agents of all types when type list is empty.
        if (!CollectionUtils.isEmpty(types)) {
            predicates.add(agent.get(Agent.ATTRIBUTE_TYPE).in(types));
        }
        if (!StringUtils.isEmpty(value)) {
            predicates.add(builder.like(builder.lower(agent.get(Agent.ATTRIBUTE_HOST)), "%" + value.toLowerCase() + "%"));
        }
        predicates.add(builder.equal(agent.get(Agent.ATTRIBUTE_REGISTERED), true));
        query.where(predicates.toArray(new Predicate[]{}));
        query.orderBy(new ImmutableList.Builder<Order>()
                .add(builder.asc(agent.get(Agent.ATTRIBUTE_TYPE)))
                .add(builder.asc(agent.get(Agent.ATTRIBUTE_HOST)))
                .build());
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Agent> findByIds(List<Long> ids) {
        List<Agent> agents = new ArrayList<>();
        // Id list is partitioned to overcome the database specific limit of max allowed values for IN clause.
        for (List<Long> subIds : Lists.partition(ids, 500)) {
            if (!CollectionUtils.isEmpty(subIds)) {
                CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
                CriteriaQuery<Agent> query = builder.createQuery(Agent.class);
                Root<Agent> agent = query.from(Agent.class);
                query.multiselect(agent.get(Agent.ATTRIBUTE_ID), agent.get(Agent.ATTRIBUTE_HOST), agent.get(Agent.ATTRIBUTE_TYPE));
                query.where(agent.get(Agent.ATTRIBUTE_ID).in(subIds));
                List<Agent> subAgents = entityManager.createQuery(query).getResultList();
                if (subAgents != null) {
                    agents.addAll(subAgents);
                }
            }
        }
        return agents;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
