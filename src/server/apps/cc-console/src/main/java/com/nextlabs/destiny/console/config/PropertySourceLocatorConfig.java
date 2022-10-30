package com.nextlabs.destiny.console.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bluejungle.framework.crypt.ReversibleEncryptor;

/**
 * Property source locator configuration that is loaded during the bootstrap phase.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class PropertySourceLocatorConfig {

    @Bean
    @ConditionalOnProperty(value = "spring.cloud.config.enabled", matchIfMissing = true)
    public ConfigServicePropertySourceLocator configServicePropertySource(ConfigClientProperties properties) {
        if (StringUtils.isNotEmpty(properties.getPassword())
                && properties.getPassword().startsWith(ReversibleTextEncryptor.ENCRYPTED_VALUE_PREFIX)) {
            properties.setPassword(new ReversibleEncryptor().decrypt(properties.getPassword()));
        }
        return new ConfigServicePropertySourceLocator(properties);
    }

}
