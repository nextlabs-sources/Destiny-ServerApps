package com.nextlabs.destiny.console.dao.dictionary;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.model.dictionary.LeafElement;

import java.util.List;
/**
 *
 * DAO interface for LeafElement
 *
 * @author Mohammed Sainal Shah
 * @since 2020.04
 *
 */
public interface LeafElementDao extends GenericDao<LeafElement, Long> {

    /**
     * Find LeafElement matching the criteria
     *
     * @param fieldName {@link String}
     * @param value {@link String}
     * @return collection of {@link LeafElement}
     */
    List<LeafElement> filterActive(String fieldName, String fieldValue);
}
