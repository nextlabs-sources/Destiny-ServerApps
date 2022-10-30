package com.nextlabs.destiny.cc.installer.helpers;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.OctJwkGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods to generate random keys.
 *
 * @author Mohammed Sainal Shah
 */
import java.security.SecureRandom;

public class KeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(KeyGenerator.class);

    private static final String JSON_WEB_KEY = "k";

    private KeyGenerator() {
    }

    /**
     * Generate json web key of given size .
     *
     * @param size the size
     * @return the key
     */
    public static String generateEncryptedJsonWebKey(int size) {
        return EncryptionHelper.encrypt(OctJwkGenerator.generateJwk(size)
                .toParams(JsonWebKey.OutputControlLevel.INCLUDE_SYMMETRIC)
                .get(JSON_WEB_KEY)
                .toString());
    }
}
