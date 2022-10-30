/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 17, 2020
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.dictionary.ElementType;

@Repository
public interface ElementTypeRepository extends JpaRepository<ElementType, Long> {

    List<ElementType> findByNameInIgnoreCase(List<String> types);

    Optional<ElementType> findByNameIgnoreCase(String name);

}
