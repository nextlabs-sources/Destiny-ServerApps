/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 28, 2020
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.dictionary.LeafElement;

@Repository
public interface LeafElementsRepository extends JpaRepository<LeafElement, Long> {
}
