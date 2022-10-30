package com.nextlabs.destiny.cc.installer.config.properties;

import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;

/**
 * Properties available to configure Java.
 *
 * @author Sachindra Dasun
 */
@Validated
public class JavaProperties {

    @NotEmpty
    private String xms;
    @NotEmpty
    private String xmx;

    public String getXms() {
        return xms;
    }

    public void setXms(String xms) {
        this.xms = xms;
    }

    public String getXmx() {
        return xmx;
    }

    public void setXmx(String xmx) {
        this.xmx = xmx;
    }

}
