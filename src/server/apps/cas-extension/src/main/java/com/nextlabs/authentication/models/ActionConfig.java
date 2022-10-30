package com.nextlabs.authentication.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity for action config.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "PM_ACTION_CONFIG")
public class ActionConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PLCY_MODEL_ID", insertable = false, updatable = false)
    private PolicyModel policyModel;
    @Column(name = "SHORT_NAME")
    private String shortName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public PolicyModel getPolicyModel() {
        return policyModel;
    }

    public void setPolicyModel(PolicyModel policyModel) {
        this.policyModel = policyModel;
    }

}
