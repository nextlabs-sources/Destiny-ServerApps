package com.nextlabs.destiny.cc.installer.config.properties;

import org.springframework.validation.annotation.Validated;

@Validated
public class CertificateProperties {

    private String alias;
    private String content;
    private String path;

    public CertificateProperties() {
    }

    public CertificateProperties(String path) {
        this.path = path;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
