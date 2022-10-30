/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2020
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import java.util.List;
import java.util.Optional;

import com.nextlabs.destiny.console.enums.EnrollmentType;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.dictionary.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByEnrollmentType(EnrollmentType enrollmentType);

    Optional<Enrollment> findByDomainNameIgnoreCase(String domainName);

    List<Enrollment> findByIsActiveIsTrue(Sort sort);

}
