package com.nextlabs.destiny.cc.installer.config.properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.validation.annotation.Validated;

import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;

@Validated
public class ManagementServerProperties {

    private int configServicePort;
    private String host;
    private String password;
    private String username;
    private int webServicePort;

    public ManagementServerProperties() {
        password = RandomStringUtils.random(32, true, true);
    }

    public int getConfigServicePort() {
        return configServicePort;
    }

    public void setConfigServicePort(int configServicePort) {
        this.configServicePort = configServicePort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = EncryptionHelper.decryptIfEncrypted(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getWebServicePort() {
        return webServicePort;
    }

    public void setWebServicePort(int webServicePort) {
        this.webServicePort = webServicePort;
    }

}
