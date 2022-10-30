package com.nextlabs.destiny.configservice.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.bluejungle.framework.crypt.ReversibleEncryptor;

/**
 * TextEncryptor implementation using ReversibleEncyptor.
 *
 * @author Sachindra Dasun
 */
public class ReversibleTextEncryptor implements TextEncryptor {

    private static final ReversibleEncryptor REVERSIBLE_ENCRYPTOR = new ReversibleEncryptor();
    public static final String CIPHER_VALUE_PREFIX = "{cipher}";

    @Override
    public String encrypt(String text) {
        return REVERSIBLE_ENCRYPTOR.encrypt(text);
    }

    @Override
    public String decrypt(String encryptedText) {
        return REVERSIBLE_ENCRYPTOR.decrypt(encryptedText);
    }

    public static String decryptIfEncrypted(String text) {
        if (StringUtils.isNotEmpty(text) && text.startsWith(CIPHER_VALUE_PREFIX)) {
            return REVERSIBLE_ENCRYPTOR.decrypt(text);
        }
        return text;
    }
}
