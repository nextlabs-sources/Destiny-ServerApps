/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;


@Entity
@Table(name = "DICT_FIELD_MAPPINGS")
public class FieldMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    /** The field for which this mapping is defined. */
    @OneToOne
    @JoinColumn(name = "field", referencedColumnName = "id", nullable = false)
    private Property field;

    /**
     * This field is assessible through field.getParentType(). It is added here to make it possible
     * to create a DB constraint to enforce the uniqueness of external names within a type.
     */
    @ManyToOne
    @JoinColumn(name = "field_type", nullable = false)
    private ElementType fieldType;

    /** The external name defined by this mapping. */
    @Column(name = "external_name")
    private String externalName;

    public FieldMapping() {
        super();
    }

    public FieldMapping(Enrollment enrollment, Property field, ElementType fieldType,
            String externalName) {
        super();
        this.enrollment = enrollment;
        this.field = field;
        this.fieldType = fieldType;
        this.externalName = externalName;
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

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Property getField() {
        return field;
    }

    public void setField(Property field) {
        this.field = field;
    }

    public ElementType getFieldType() {
        return fieldType;
    }

    public void setFieldType(ElementType fieldType) {
        this.fieldType = fieldType;
    }

    public String getExternalName() {
        return externalName;
    }

    public void setExternalName(String externalName) {
        this.externalName = externalName;
    }

}
