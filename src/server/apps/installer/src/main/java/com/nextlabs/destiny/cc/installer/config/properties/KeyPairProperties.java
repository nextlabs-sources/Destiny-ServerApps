package com.nextlabs.destiny.cc.installer.config.properties;

import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;

/**
 * SSL certificate properties.
 *
 * @author Sachindra Dasun
 */
public class KeyPairProperties {


    private String content;
    private String keyAlias;
    private String keyPassword;
    private String password;
    private String type;
    private String keyContent;
    private String certificateContent;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyPassword() {
        return EncryptionHelper.decryptIfEncrypted(keyPassword);
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getPassword() {
        return EncryptionHelper.decryptIfEncrypted(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyContent() {
        return keyContent;
    }

    public void setKeyContent(String keyContent) {
        this.keyContent = keyContent;
    }

    public String getCertificateContent() {
        return certificateContent;
    }

    public void setCertificateContent(String certificateContent) {
        this.certificateContent = certificateContent;
    }

}
