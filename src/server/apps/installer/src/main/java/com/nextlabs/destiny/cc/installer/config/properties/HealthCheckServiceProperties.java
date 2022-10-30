package com.nextlabs.destiny.cc.installer.config.properties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.nextlabs.destiny.cc.installer.annotations.ValidHealthCheckServicePort;
import com.nextlabs.destiny.cc.installer.enums.Protocol;

/**
 * Server health check service properties.
 *
 * @author Sachindra Dasun
 */
@ValidHealthCheckServicePort(message = "{healthCheckServicePort.validPort}")
public class HealthCheckServiceProperties {

    private boolean enabled;
    @Min(value = 0, message = "{healthCheckServicePort.min}")
    @Max(value = 65535, message = "{healthCheckServicePort.max}")
    private int port;
    private Protocol protocol;
    private String contextPath;
    private String fileName;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
