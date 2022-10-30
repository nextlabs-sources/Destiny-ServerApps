package com.nextlabs.destiny.console.dto.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.nextlabs.destiny.logmanager.enums.LogLevel;

/**
 * DTO for logger configuration.
 *
 * @author Sachindra Dasun
 */
public class LoggerConfigDTO implements Serializable {

    private static final long serialVersionUID = 4009633590749388884L;
    private String name;
    private String displayName;
    private LogLevel level;
    private List<LoggerAppenderDTO> appenders;

    @JsonIgnore
    private int order;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public List<LoggerAppenderDTO> getAppenders() {
        if (appenders == null) {
            appenders = new ArrayList<>();
        }
        return appenders;
    }

    public void setAppenders(List<LoggerAppenderDTO> appenders) {
        this.appenders = appenders;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
