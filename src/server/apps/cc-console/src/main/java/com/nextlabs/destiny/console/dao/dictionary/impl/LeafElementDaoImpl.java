package com.nextlabs.destiny.console.dao.dictionary.impl;

import com.nextlabs.destiny.console.dao.dictionary.LeafElementDao;
import com.nextlabs.destiny.console.dao.impl.GenericDaoImpl;
import com.nextlabs.destiny.console.model.dictionary.LeafElement;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

/**
 *
 * DAO implementation for LeafElement
 *
 * @author Mohammed Sainal Shah
 * @since 2020.04
 *
 */
@Repository
public class LeafElementDaoImpl extends GenericDaoImpl<LeafElement, Long>
        implements LeafElementDao {

    @PersistenceContext(unitName = MGMT_UNIT)
    private EntityManager entityManager;

    @Override
    public List<LeafElement> filterActive(String fieldName, String fieldValue) {
        String hql = String.format("SELECT leafElement FROM LeafElement leafElement, Element element" +
                " WHERE leafElement.elementId = element.id" +
                " AND element.activeFrom <= :activeFrom" +
                " AND element.activeTo > :activeTo" +
                " AND leafElement.%s = :fieldValue", fieldName);
        TypedQuery<LeafElement> query = entityManager.createQuery(hql, LeafElement.class);
        long asOfTime = Calendar.getInstance().getTimeInMillis();
        query.setParameter("activeFrom", asOfTime);
        query.setParameter("activeTo", asOfTime);
        query.setParameter("fieldValue", fieldValue);
        return query.getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
