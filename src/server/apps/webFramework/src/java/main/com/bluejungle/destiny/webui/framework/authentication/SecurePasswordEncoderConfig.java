package com.bluejungle.destiny.webui.framework.authentication;

import com.nextlabs.destiny.configclient.ConfigClient;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides Delegating password encoder beans.
 * Uses PBKDF2 for password matching for passwords with pbkdf2 marker
 * Falls back to legacy MD5 otherwise
 *
 * @author Mohammed Sainal Shah
 */

public class SecurePasswordEncoderConfig {

    private static final CharSequence secret = ConfigClient.get("pbkdf2.encoding.secret.key").toString();
    private static final int ITERATION_COUNT = ConfigClient.get("pbkdf2.encoding.iteration.count").toInt();
    private static final int HASH_WIDTH = ConfigClient.get("pbkdf2.encoding.hash.width").toInt();
    private static final int SALT_LENGTH = ConfigClient.get("pbkdf2.encoding.salt.width").toInt();

    private static final String PBKDF2_ID = "pbkdf2";

    private static DelegatingPasswordEncoder passwordEncoder;

    @SuppressWarnings("deprecation")
    public static PasswordEncoder getDelegatingPasswordEncoder() {

        if (passwordEncoder == null) {
            synchronized (SecurePasswordEncoderConfig.class) {

                Map<String, PasswordEncoder> encoders = new HashMap<>();
                encoders.put(PBKDF2_ID, pbkdf2PasswordEncoder());

                passwordEncoder = new DelegatingPasswordEncoder(
                        PBKDF2_ID, encoders);
                //to support older password hashing implementation
                passwordEncoder.setDefaultPasswordEncoderForMatches(new MD5PasswordEncoder());
            }
        }
        return passwordEncoder;
    }

    private static PasswordEncoder pbkdf2PasswordEncoder() {
        return new SecurePbkdf2PasswordEncoder(secret, ITERATION_COUNT, HASH_WIDTH, SALT_LENGTH);
    }

    public static boolean isPasswordPbkdf2Encoded (String password){
        String pbkdf2Prefix = String.format("{%s}", PBKDF2_ID);
        return password.startsWith(pbkdf2Prefix);
    }
}
