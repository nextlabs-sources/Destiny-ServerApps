package com.nextlabs.destiny.cc.installer.config.properties;

import com.nextlabs.destiny.cc.installer.annotations.ValidPassword;
import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;

/**
 * SSL certificate store properties.
 *
 * @author Sachindra Dasun
 */
public class CertificateStoreProperties {

    @ValidPassword(message = "{password.valid}")
    private String password;

    public String getPassword() {
        return EncryptionHelper.decryptIfEncrypted(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
