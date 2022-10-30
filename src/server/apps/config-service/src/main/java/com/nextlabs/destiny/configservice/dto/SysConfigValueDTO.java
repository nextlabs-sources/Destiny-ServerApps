package com.nextlabs.destiny.configservice.dto;

import java.io.Serializable;

/**
 * DTO for system configuration value.
 *
 * @author Sachindra Dasun
 */
public class SysConfigValueDTO implements Serializable {

    private static final long serialVersionUID = 1277488138801672283L;
    private String application;
    private String configKey;
    private String value;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
