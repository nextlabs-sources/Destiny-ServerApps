/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2020
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.json.JSONObject;

import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.enums.converters.EnrollmentTypeConverter;
import com.nextlabs.destiny.console.model.BaseModel;

@Entity
@Table(name = Enrollment.ENROLLMENT_TABLE)
public class Enrollment extends BaseModel {

    public static final String ENROLLMENT_TABLE = "DICT_ENROLLMENTS";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    /** The domain name associated with this enrollment. */
    @Column(name = "DOMAIN_NAME")
    private String domainName;

    /**
     * The type of this enrollment (e.g. Active Directory import, LDIF, etc. Data import application
     * use this field for their purposes.
     */
    @Column(name = "ENROLLMENT_TYPE")
    @Convert(converter = EnrollmentTypeConverter.class)
    private EnrollmentType enrollmentType;

    /**
     * This field defines whether enrollment is recurring if enrollment is recurring, server will
     * automatically pull the data periodically, otherwise, enrollment will be invoked by external
     * command
     */
    private boolean isRecurring;

    private boolean isActive;

    @Column(updatable = false)
    private boolean isSyncing;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true,
            mappedBy = "enrollment")
    private Set<EnrollmentProperty> properties;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true,
            mappedBy = "enrollment")
    private Set<FieldMapping> externalMappings;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true,
            mappedBy = "enrollment")
    private List<Updates> updates;

    public Enrollment() {
        super();
    }

    public void updateFrom(EnrollmentDTO enrollmentDTO) {
        description = enrollmentDTO.getDescription();
        domainName = enrollmentDTO.getName();
        enrollmentType = enrollmentDTO.getType();
        isActive = true;
    }

    public String getAuditString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Enrollment ID", getId());
        jsonObject.put("Domain Name", getDomainName());
        jsonObject.put("Description", getDescription());
        return jsonObject.toString(2);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public EnrollmentType getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(EnrollmentType enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isSyncing() {
        return isSyncing;
    }

    public void setSyncing(boolean syncing) {
        isSyncing = syncing;
    }

    public Set<EnrollmentProperty> getProperties() {
        if (properties == null) {
            properties = new HashSet<>();
        }
        return properties;
    }

    public void setProperties(Set<EnrollmentProperty> properties) {
        this.properties = properties;
        for (EnrollmentProperty p : properties) {
            p.setEnrollment(this);
        }
    }

    public Set<FieldMapping> getExternalMappings() {
        if (externalMappings == null) {
            externalMappings = new HashSet<>();
        }
        return externalMappings;
    }

    public void setExternalMappings(Set<FieldMapping> externalMappings) {
        this.externalMappings = externalMappings;
        for (FieldMapping fm : externalMappings) {
            fm.setEnrollment(this);
        }
    }

    public List<Updates> getUpdates() {
        if (updates == null) {
            updates = new ArrayList<>();
        }
        return updates;
    }

    public void setUpdates(List<Updates> updates) {
        this.updates = updates;
    }

    public String getEnrollmentPropertyValue(String name) {
        return getEnrollmentProperty(name).map(EnrollmentProperty::getValue).orElse("");
    }

    public Optional<EnrollmentProperty> getEnrollmentProperty(String name) {
        return getProperties().stream()
                .filter(property -> name.equalsIgnoreCase(property.getName()))
                .findFirst();
    }

    public Optional<FieldMapping> getFiledMappingByFieldId(Long id) {
        return getExternalMappings().stream()
                .filter(fieldMapping -> id.equals(fieldMapping.getField().getId()))
                .findFirst();
    }

}
