package com.nextlabs.destiny.console.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

import com.nextlabs.destiny.console.ConsoleApplication;

/**
 * This configuration allows to override remote configurations using a local configuration file.
 *
 * @author Sachindra Dasun
 */
@Configuration
@Order(-1)
public class LocalPropertySourceLocator implements PropertySourceLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalPropertySourceLocator.class);

    @Value("${server.config.path:}")
    private String serverConfigPath;

    @Override
    public PropertySource<?> locate(Environment environment) {
        try {
            if (StringUtils.isNotEmpty(serverConfigPath)) {
                Path configFilePath = Paths.get(serverConfigPath, String.format("%s-local.properties", ConsoleApplication.APPLICATION_NAME));
                if (configFilePath.toFile().exists()) {
                    return new ResourcePropertySource("localConfigurations", configFilePath.toUri().toString());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error in loading local property file", e);
        }
        return null;
    }
}
