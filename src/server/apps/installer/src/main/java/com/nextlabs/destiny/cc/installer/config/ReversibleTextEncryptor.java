package com.nextlabs.destiny.cc.installer.config;

import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;

/**
 * TextEncryptor implementation using ReversibleEncyptor.
 *
 * @author Sachindra Dasun
 */
public class ReversibleTextEncryptor implements TextEncryptor {

    @Override
    public String encrypt(String text) {
        return EncryptionHelper.encrypt(text);
    }

    @Override
    public String decrypt(String encryptedText) {
        return EncryptionHelper.decrypt(encryptedText);
    }

}
