package com.nextlabs.destiny.console.config;

import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.bluejungle.framework.crypt.ReversibleEncryptor;

/**
 * TextEncryptor implementation using ReversibleEncyptor.
 *
 * @author Sachindra Dasun
 */
public class ReversibleTextEncryptor implements TextEncryptor {

    public static final String CIPHER_VALUE_FORMAT = "{cipher}%s";
    public static final String ENCRYPTED_VALUE_PREFIX = "{cipher}";

    private ReversibleEncryptor reversibleEncryptor = new ReversibleEncryptor();

    @Override
    public String encrypt(String text) {
        return String.format(CIPHER_VALUE_FORMAT, reversibleEncryptor.encrypt(text));
    }

    @Override
    public String decrypt(String encryptedText) {
        return reversibleEncryptor.decrypt(encryptedText);
    }
}
