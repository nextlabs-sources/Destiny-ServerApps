/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.dictionary.EnrollmentProperty;

@Repository
public interface EnrollmentPropertyRepository extends JpaRepository<EnrollmentProperty, Long> {

}
