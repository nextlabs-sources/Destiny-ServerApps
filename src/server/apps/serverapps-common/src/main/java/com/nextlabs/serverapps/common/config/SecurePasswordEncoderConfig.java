package com.nextlabs.serverapps.common.config;

import com.nextlabs.serverapps.common.authentication.MD5PasswordEncoder;
import com.nextlabs.serverapps.common.authentication.SecurePbkdf2PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@Configuration
public class SecurePasswordEncoderConfig {

    @Value("${pbkdf2.encoding.secret.key}")
    private CharSequence secret;

    @Value("#{new Integer('${pbkdf2.encoding.iteration.count}')}")
    private int iterationCount;

    @Value("#{new Integer('${pbkdf2.encoding.hash.width}')}")
    private int hashWidth;

    @Value("#{new Integer('${pbkdf2.encoding.salt.width}')}")
    private int saltWidth;

    private static final String PBKDF2_ID = "pbkdf2";

    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PBKDF2_ID, new SecurePbkdf2PasswordEncoder(secret, iterationCount, hashWidth, saltWidth));

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(
                PBKDF2_ID, encoders);

        //to support older password hashing implementation
        passwordEncoder.setDefaultPasswordEncoderForMatches(new MD5PasswordEncoder());

        return passwordEncoder;
    }

    public static boolean isPasswordPbkdf2Encoded (String password){
        String pbkdf2Prefix = String.format("{%s}", PBKDF2_ID);
        return password.startsWith(pbkdf2Prefix);
    }
}
