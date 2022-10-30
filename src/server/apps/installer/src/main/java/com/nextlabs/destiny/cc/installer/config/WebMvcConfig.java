package com.nextlabs.destiny.cc.installer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class WebMvcConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurerForIndexHtml() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("forward:/index.html");
            }
        };
    }

}
