package com.bluejungle.destiny.webui.framework.authentication;

import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

import static org.springframework.security.crypto.util.EncodingUtils.concatenate;

/**
 * Pbkdf2PasswordEncoder implementation
 *
 * @author Mohammed Sainal Shah
 */
public class SecurePbkdf2PasswordEncoder extends Pbkdf2PasswordEncoder {

    private int saltLength;
    private final byte[] secret;
    private final int hashWidth;
    private final int iterations;
    private final BytesKeyGenerator saltGenerator;
    private final SecretKeyFactoryAlgorithm algorithm = SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512;

    SecurePbkdf2PasswordEncoder(CharSequence secret, int iterations, int hashWidth, int saltLength) {
        super(secret, iterations, hashWidth);

        // saltLength passed is in bits, convert to bytes
        this.saltLength = saltLength / 8;
        this.secret = Utf8.encode(secret);
        this.iterations = iterations;
        this.hashWidth = hashWidth;
        this.saltGenerator = KeyGenerators.secureRandom(this.saltLength);
        setEncodeHashAsBase64(true);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt =  saltGenerator.generateKey();
        try {
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(),
                    concatenate(salt, this.secret), this.iterations, this.hashWidth);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm.name());
            byte[] encodedPassword = concatenate(salt, skf.generateSecret(spec).getEncoded());

            return encodeToBase64(encodedPassword);
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not create hash", e);
        }
    }


    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = decodeFromBase64(encodedPassword);
        byte[] salt = EncodingUtils.subArray(digested, 0, this.saltGenerator.getKeyLength());
        return matches(digested, encodeWithSalt(rawPassword, salt));
    }

    /**
     * Constant time comparison to prevent against timing attacks.
     */
    private static boolean matches(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < expected.length; i++) {
            result |= expected[i] ^ actual[i];
        }
        return result == 0;
    }

    private byte[] encodeWithSalt(CharSequence rawPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(),
                    concatenate(salt, this.secret), this.iterations, this.hashWidth);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm.name());
            return concatenate(salt, skf.generateSecret(spec).getEncoded());
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not create hash", e);
        }
    }

    private static byte[] decodeFromBase64(String encodedBytes) {
        return Base64.getDecoder().decode(encodedBytes);
    }

    private static String encodeToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
