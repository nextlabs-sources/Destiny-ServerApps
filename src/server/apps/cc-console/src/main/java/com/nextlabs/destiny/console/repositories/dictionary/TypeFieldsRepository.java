/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 28, 2020
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.dictionary.TypeField;

@Repository
public interface TypeFieldsRepository extends JpaRepository<TypeField, Long> {

    List<TypeField> findByParentTypeIdAndDeletedAndNameIgnoreCase(Long parentTypeId, char deleted,
            String name);

    List<TypeField> findByDeletedIs(char deleted);

}
