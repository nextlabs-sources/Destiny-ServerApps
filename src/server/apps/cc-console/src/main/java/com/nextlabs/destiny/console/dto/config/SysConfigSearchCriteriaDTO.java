package com.nextlabs.destiny.console.dto.config;

import java.io.Serializable;

/**
 * DTO for system configuration search criteria.
 *
 * @author Sachindra Dasun
 */
public class SysConfigSearchCriteriaDTO implements Serializable {

    private static final long serialVersionUID = 5185359662200494337L;
    private String application;
    private String key;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
