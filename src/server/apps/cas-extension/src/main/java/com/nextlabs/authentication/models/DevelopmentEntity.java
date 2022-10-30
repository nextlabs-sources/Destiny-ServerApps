package com.nextlabs.authentication.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.bluejungle.pf.destiny.parser.IHasPQL;
import com.nextlabs.authentication.enums.DevelopmentEntityType;

/**
 * Entity for development entity.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "DEVELOPMENT_ENTITIES")
public class DevelopmentEntity implements IHasPQL {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "HIDDEN")
    private Character hidden;

    @Lob
    @Column(name = "PQL", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String pql;

    @Column(name = "STATUS")
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private DevelopmentEntityType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Character getHidden() {
        return hidden;
    }

    public void setHidden(Character hidden) {
        this.hidden = hidden;
    }

    public String getPql() {
        return pql;
    }

    public void setPql(String pql) {
        this.pql = pql;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DevelopmentEntityType getType() {
        return type;
    }

    public void setType(DevelopmentEntityType type) {
        this.type = type;
    }

}
