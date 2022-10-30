package com.nextlabs.destiny.console.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties for Service URLs.
 *
 * @author Sachindra Dasun
 */
@Component
@ConfigurationProperties(prefix = "service.url")
public class ServiceUrlProperties {

    private String dem;
    private String dps;

    public String getDem() {
        return dem;
    }

    public void setDem(String dem) {
        this.dem = dem;
    }

    public String getDps() {
        return dps;
    }

    public void setDps(String dps) {
        this.dps = dps;
    }

}
