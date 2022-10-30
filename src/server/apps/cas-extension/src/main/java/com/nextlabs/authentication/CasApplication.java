package com.nextlabs.authentication;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.nextlabs.authentication.config.PropertySourceLocatorConfig;

/**
 * Spring Boot configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
@ComponentScan(basePackages = {"com.nextlabs.authentication", "com.nextlabs.serverapps.common.config"},
        excludeFilters =
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = PropertySourceLocatorConfig.class))
@EnableConfigurationProperties({CasConfigurationProperties.class})
@EnableScheduling
public class CasApplication {
}
