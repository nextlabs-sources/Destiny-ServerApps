package com.nextlabs.authentication.config;

import java.util.Properties;

import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * Property source locator configuration that is loaded during the bootstrap phase.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class PropertySourceLocatorConfig {

    private static final String AUTH_PROPERTY = "spring.cloud.config.password";

    @Bean
    public ConfigServicePropertySourceLocator configServicePropertySource(ConfigClientProperties properties,
                                                                          ConfigurableEnvironment configurableEnvironment) {
        MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources();
        Properties decryptedProperties = new Properties();
        decryptedProperties.put(AUTH_PROPERTY,
                ReversibleTextEncryptor.decryptIfEncrypted(configurableEnvironment.getProperty(AUTH_PROPERTY)));
        mutablePropertySources.addFirst(new PropertiesPropertySource("bootstrap-decrypted", decryptedProperties));
        properties.setPassword(ReversibleTextEncryptor.decryptIfEncrypted(properties.getPassword()));
        return new ConfigServicePropertySourceLocator(properties);
    }

}
