package com.nextlabs.destiny.configservice.init;

import java.security.SecureRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.jose4j.base64url.Base64;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.OctJwkGenerator;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.framework.crypt.ReversibleEncryptor;

/**
 * Utility methods to generate random keys.
 *
 * @author Sachindra Dasun
 */
class KeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(KeyGenerator.class);

    private static final String JSON_WEB_KEY = "k";
    private static final ReversibleEncryptor REVERSIBLE_ENCRYPTOR = new ReversibleEncryptor();
    private static final String ENCRYPTED_CONFIG_VALUE_FORMAT = "{cipher}%s";
    private static final SecureRandom secureRandom = new SecureRandom();

    private KeyGenerator() {
    }

    /**
     * Generate json web key of given size .
     *
     * @param size the size
     * @return the key
     */
    static String generateEncryptedJsonWebKey(int size) {
        return String.format(ENCRYPTED_CONFIG_VALUE_FORMAT,
                REVERSIBLE_ENCRYPTOR.encrypt(OctJwkGenerator.generateJwk(size)
                        .toParams(JsonWebKey.OutputControlLevel.INCLUDE_SYMMETRIC)
                        .get(JSON_WEB_KEY)
                        .toString()
                )
        );
    }

    static String generateEncryptedRandomString(int size) {
        return String.format(ENCRYPTED_CONFIG_VALUE_FORMAT,
                REVERSIBLE_ENCRYPTOR.encrypt(RandomStringUtils.randomAlphanumeric(size)));
    }

    static String generateEncryptedBase64RandomString(int size) {
        byte[] random = new byte[size];
        secureRandom.nextBytes(random);
        return String.format(ENCRYPTED_CONFIG_VALUE_FORMAT, REVERSIBLE_ENCRYPTOR.encrypt(Base64.encode(random)));
    }

    // algorithm here is metadata only, used for validation
    static String generateEncryptedRSAJsonWebKey(final int bits, final String keyId, String algorithm) {
        try {
            RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(bits);
            rsaJsonWebKey.setKeyId(keyId);
            rsaJsonWebKey.setAlgorithm(algorithm);
            JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(rsaJsonWebKey);
            return String.format(ENCRYPTED_CONFIG_VALUE_FORMAT,
                    REVERSIBLE_ENCRYPTOR.encrypt(
                            jsonWebKeySet.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE)));
        } catch (JoseException e) {
            logger.error("Error while generating RSA Json Web Key, {}", e);
            return "";
        }
    }
}
