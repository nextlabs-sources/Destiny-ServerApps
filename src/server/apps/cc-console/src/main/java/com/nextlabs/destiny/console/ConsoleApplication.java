package com.nextlabs.destiny.console;

import java.nio.file.Paths;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot configuration.
 *
 * @author Sachindra Dasun
 */
@SpringBootApplication
@EnableScheduling
public class ConsoleApplication extends SpringBootServletInitializer {

    public static final String DEFAULT_APPLICATION_NAME = "application";
    public static final String APPLICATION_NAME = "console";

    public static void main(String[] args) {
        System.setProperty("spring.cloud.bootstrap.location", Paths.get(System.getProperty("cc.home"),
                "server", "configuration", "bootstrap.properties").toString());
        new SpringApplicationBuilder(ConsoleApplication.class)
                .build()
                .run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.properties("spring.application.name=" + APPLICATION_NAME);
        return builder.sources(ConsoleApplication.class);
    }

}
