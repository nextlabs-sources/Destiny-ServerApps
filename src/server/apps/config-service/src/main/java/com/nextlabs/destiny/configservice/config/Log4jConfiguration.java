package com.nextlabs.destiny.configservice.config;

import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Log4j configuration required when running in embedded container mode.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class Log4jConfiguration {

    @Bean
    @Profile("embedded")
    public ServletListenerRegistrationBean<Log4jServletContextListener> log4jServletContextListenerServletListenerRegistrationBean() {
        return new ServletListenerRegistrationBean<>(new Log4jServletContextListener());
    }

}
