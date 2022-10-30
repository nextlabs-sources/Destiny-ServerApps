package com.nextlabs.destiny.cc.installer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.TextEncryptor;

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

}
