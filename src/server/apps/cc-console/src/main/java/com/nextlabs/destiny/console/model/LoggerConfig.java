package com.nextlabs.destiny.console.model;

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

import com.nextlabs.destiny.logmanager.enums.LoggerConfigType;

/**
 * Entity for logger configuration.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "LOGGER_CONFIG")
public class LoggerConfig {
    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private LoggerConfigType type;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "CONFIG")
    private String config;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoggerConfigType getType() {
        return type;
    }

    public void setType(LoggerConfigType type) {
        this.type = type;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
