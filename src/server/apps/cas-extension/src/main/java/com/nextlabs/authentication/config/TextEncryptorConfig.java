package com.nextlabs.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.bluejungle.framework.crypt.ReversibleEncryptor;

/**
 * Configure ReversibleEncryptor as the default TextEncryptor.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class TextEncryptorConfig {

    @Bean
    public TextEncryptor textEncryptor() {
        return new ReversibleTextEncryptor();
    }

    @Bean
    public ReversibleEncryptor reversibleEncryptor() {
        return new ReversibleEncryptor();
    }

}
