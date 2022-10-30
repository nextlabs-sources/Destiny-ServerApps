/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.nextlabs.destiny.console.dto.enrollment.EnrollmentPropertyDTO;
import com.nextlabs.destiny.console.enums.EnrollmentPropertyType;

@Entity
@Table(name = "DICT_ENROLLMENT_PROPERTIES")
public class EnrollmentProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    private String name;

    @Enumerated(EnumType.STRING)
    private EnrollmentPropertyType type;

    @Column(name = "property_value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    public EnrollmentProperty() {
    }

    public EnrollmentProperty(Enrollment enrollment, EnrollmentPropertyDTO enrollmentPropertyDTO) {
        this.enrollment = enrollment;
        name = enrollmentPropertyDTO.getName();
        type = enrollmentPropertyDTO.getType();
        value = enrollmentPropertyDTO.getValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnrollmentPropertyType getType() {
        return type;
    }

    public void setType(EnrollmentPropertyType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
}
