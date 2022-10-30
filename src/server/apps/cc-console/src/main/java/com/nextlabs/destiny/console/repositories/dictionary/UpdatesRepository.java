/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 29, 2020
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextlabs.destiny.console.model.dictionary.Updates;

public interface UpdatesRepository extends JpaRepository<Updates, Long> {

    Updates findByEnrollmentId(Long userId);

    Updates findByEnrollmentIdAndActiveToGreaterThan(Long enrollmentId, long currentTimeMillis);

}
