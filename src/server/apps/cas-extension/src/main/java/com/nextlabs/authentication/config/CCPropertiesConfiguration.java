package com.nextlabs.authentication.config;

import javax.annotation.PostConstruct;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * Configuration to add custom CAS properties.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class CCPropertiesConfiguration {

    @Autowired
    private ConfigurableEnvironment environment;

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.put("logging.config", "none");
        this.environment.getPropertySources().addFirst(new PropertiesPropertySource("custom-cas-properties",
                properties));
    }

}
