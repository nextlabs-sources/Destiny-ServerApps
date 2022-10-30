package com.nextlabs.destiny.cc.installer.config;

import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;
import com.nextlabs.destiny.cc.installer.helpers.KeyGenerator;
import com.nextlabs.serverapps.common.authentication.SecurePbkdf2PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Control Center installer password encode configuration.
 *
 * @author Mohammed Sainal Shah
 */
@Configuration
public class SecurePasswordEncoderConfig {

    private static final int ITERATION_COUNT = 200000;
    private static final int HASH_WIDTH = 512;
    private static final int SALT_WIDTH = 512;
    private static final String PBKDF2_ID = "pbkdf2";

    @Bean
    public String passwordEncodeSecret(){
        return KeyGenerator.generateEncryptedJsonWebKey(512);
    }

    @Bean
    public SecurePbkdf2PasswordEncoder securePbkdf2PasswordEncoder() {
        return new SecurePbkdf2PasswordEncoder(EncryptionHelper.decrypt(passwordEncodeSecret()), ITERATION_COUNT, HASH_WIDTH, SALT_WIDTH);
    }

    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PBKDF2_ID, securePbkdf2PasswordEncoder());
        return new DelegatingPasswordEncoder(PBKDF2_ID, encoders);
    }
}
