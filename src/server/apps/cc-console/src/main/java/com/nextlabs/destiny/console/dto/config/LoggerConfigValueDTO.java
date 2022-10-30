package com.nextlabs.destiny.console.dto.config;

import com.nextlabs.destiny.logmanager.enums.LogLevel;

/**
 * DTO for logger configuration value.
 *
 * @author Sachindra Dasun
 */
public class LoggerConfigValueDTO {
    private String name;
    private LogLevel level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }
}
