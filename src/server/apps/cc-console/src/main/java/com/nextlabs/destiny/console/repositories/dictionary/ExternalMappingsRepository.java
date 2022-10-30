/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.dictionary.FieldMapping;

@Repository
public interface ExternalMappingsRepository extends JpaRepository<FieldMapping, Long> {

}
