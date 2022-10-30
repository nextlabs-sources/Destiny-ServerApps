/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 14, 2020
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.dictionary.Property;

/**
 * Dict Type Fields Repository
 * 
 * @author Sneha Tilak
 * @since 9.5
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByNameIgnoreCaseAndParentTypeId(String name, Long parentId);

    Property findTopByParentTypeIdAndMappingContainingIgnoreCaseOrderByMappingDesc(
            Long parentTypeId, String mappingBase);

    List<Property> findByParentTypeIdAndDeleted(Long parentId, char deleted);

    Optional<Property> findByParentTypeIdAndNameIgnoreCaseAndDeletedAndTypeEqualsIgnoreCase(Long id, String name,
                                                                                            char c, String type);

    List<Property> findByDeleted(char deleted, Sort sort);

    Page<Property> findByParentTypeIdInAndTypeInAndLabelContainingIgnoreCaseAndDeleted(List<Long> entityTypeIds,
                                                                                       List<String> dataTypes,
                                                                                       String text, char deleted,
                                                                                       Pageable pageable);

}
