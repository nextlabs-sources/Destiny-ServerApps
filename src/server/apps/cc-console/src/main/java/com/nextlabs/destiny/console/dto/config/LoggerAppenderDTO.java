package com.nextlabs.destiny.console.dto.config;

import java.io.Serializable;

/**
 * DTO for logger appender details.
 *
 * @author Sachindra Dasun
 */
public class LoggerAppenderDTO implements Serializable {

    private static final long serialVersionUID = 9163406240402479968L;

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
