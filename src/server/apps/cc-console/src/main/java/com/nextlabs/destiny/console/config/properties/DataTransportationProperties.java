package com.nextlabs.destiny.console.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "data.transportation")
public class DataTransportationProperties {

    private String keyStoreFile;
    private String trustStoreFile;
    private String mode;
    private boolean allowPlainTextImport;
    private boolean allowPlainTextExport;
    private String sharedKey;

    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    public void setTrustStoreFile(String trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isAllowPlainTextImport() {
        return allowPlainTextImport;
    }

    public void setAllowPlainTextImport(boolean allowPlainTextImport) {
        this.allowPlainTextImport = allowPlainTextImport;
    }

    public boolean isAllowPlainTextExport() {
        return allowPlainTextExport;
    }

    public void setAllowPlainTextExport(boolean allowPlainTextExport) {
        this.allowPlainTextExport = allowPlainTextExport;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }
}
