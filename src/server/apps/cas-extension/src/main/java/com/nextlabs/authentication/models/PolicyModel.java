package com.nextlabs.authentication.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nextlabs.authentication.enums.PolicyModelStatus;
import com.nextlabs.authentication.enums.PolicyModelType;

/**
 * Entity for policy model.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "POLICY_MODEL")
public class PolicyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private PolicyModelType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PolicyModelStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PolicyModelType getType() {
        return type;
    }

    public void setType(PolicyModelType type) {
        this.type = type;
    }

    public PolicyModelStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyModelStatus status) {
        this.status = status;
    }

}
