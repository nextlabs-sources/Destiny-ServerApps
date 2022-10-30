package com.nextlabs.destiny.configservice.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

import com.nextlabs.destiny.configservice.config.properties.ConfigServiceProperties;

/**
 * This configuration allows to override remote configurations using a local configuration file.
 *
 * @author Sachindra Dasun
 */
@Configuration
@Order(-1)
public class LocalPropertySourceLocator implements PropertySourceLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalPropertySourceLocator.class);

    @Value("${cc.home}")
    private String ccHome;

    @Override
    public PropertySource<?> locate(Environment environment) {
        try {
            Path configFilePath = Paths.get(ccHome, "server", "configuration",
                    String.format("%s-local.properties", ConfigServiceProperties.APPLICATION_NAME));
            if (configFilePath.toFile().exists()) {
                return new ResourcePropertySource("localConfigurations", configFilePath.toUri().toString());
            }
        } catch (IOException e) {
            LOGGER.error("Error in loading local property file", e);
        }
        return null;
    }
}
