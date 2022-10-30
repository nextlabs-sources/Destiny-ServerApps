package com.nextlabs.destiny.cc.installer.config.properties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;

import com.nextlabs.destiny.cc.installer.annotations.ValidPort;
import com.nextlabs.destiny.cc.installer.enums.PortType;

@Validated
public class PortProperties {

    @Min(value = 1, message = "{activeMqPort.min}")
    @Max(value = 65535, message = "{activeMqPort.max}")
    @ValidPort(portType = PortType.ACTIVE_MQ, message = "{activeMqPort.validPort}")
    private int activeMqPort;
    @Min(value = 1, message = "{appServicePort.min}")
    @Max(value = 65535, message = "{appServicePort.max}")
    @ValidPort(portType = PortType.APP_SERVICE, message = "{appServicePort.validPort}")
    private int appServicePort;
    @Min(value = 1, message = "{configServicePort.min}")
    @Max(value = 65535, message = "{configServicePort.max}")
    @ValidPort(portType = PortType.CONFIG_SERVICE, message = "{configServicePort.validPort}")
    private int configServicePort;
    @Min(value = 1, message = "{dataIndexerHttpPort.min}")
    @Max(value = 65535, message = "{dataIndexerHttpPort.max}")
    @ValidPort(portType = PortType.DATA_INDEXER_HTTP, message = "{dataIndexerHttpPort.validPort}")
    private int dataIndexerHttpPort;
    @Min(value = 1, message = "{policyValidatorPort.min}")
    @Max(value = 65535, message = "{policyValidatorPort.max}")
    @ValidPort(portType = PortType.POLICY_VALIDATOR, message = "{policyValidatorPort.validPort}")
    private int policyValidatorPort;
    @Min(value = 0, message = "{externalPort.min}")
    @Max(value = 65535, message = "{externalPort.max}")
    private int externalPort;
    @Min(value = 1, message = "{serverShutdownPort.min}")
    @Max(value = 65535, message = "{serverShutdownPort.max}")
    @ValidPort(portType = PortType.SERVER_SHUTDOWN, message = "{serverShutdownPort.validPort}")
    private int serverShutdownPort;
    @Min(value = 1, message = "{webServicePort.min}")
    @Max(value = 65535, message = "{webServicePort.max}")
    @ValidPort(portType = PortType.WEB_SERVICE, message = "{webServicePort.validPort}")
    private int webServicePort;

    public int getActiveMqPort() {
        return activeMqPort;
    }

    public void setActiveMqPort(int activeMqPort) {
        this.activeMqPort = activeMqPort;
    }

    public int getAppServicePort() {
        return appServicePort;
    }

    public void setAppServicePort(int appServicePort) {
        this.appServicePort = appServicePort;
    }

    public int getConfigServicePort() {
        return configServicePort;
    }

    public void setConfigServicePort(int configServicePort) {
        this.configServicePort = configServicePort;
    }

    public int getDataIndexerHttpPort() {
        return dataIndexerHttpPort;
    }

    public void setDataIndexerHttpPort(int dataIndexerHttpPort) {
        this.dataIndexerHttpPort = dataIndexerHttpPort;
    }

    public int getExternalPort() {
        return externalPort > 1 ? externalPort : appServicePort;
    }

    public void setExternalPort(int externalPort) {
        this.externalPort = externalPort;
    }

    public int getPolicyValidatorPort() {
        return policyValidatorPort;
    }

    public void setPolicyValidatorPort(int policyValidatorPort) {
        this.policyValidatorPort = policyValidatorPort;
    }

    public int getServerShutdownPort() {
        return serverShutdownPort;
    }

    public void setServerShutdownPort(int serverShutdownPort) {
        this.serverShutdownPort = serverShutdownPort;
    }

    public int getWebServicePort() {
        return webServicePort;
    }

    public void setWebServicePort(int webServicePort) {
        this.webServicePort = webServicePort;
    }

}
