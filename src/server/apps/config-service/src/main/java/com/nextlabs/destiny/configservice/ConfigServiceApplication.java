package com.nextlabs.destiny.configservice;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import com.nextlabs.cc.common.util.HostnameUtil;
import com.nextlabs.destiny.configservice.config.DataSourceConfiguration;
import com.nextlabs.destiny.configservice.config.properties.ConfigServiceProperties;

/**
 * Configuration service application
 *
 * @author Sachindra Dasun
 */
@SpringBootApplication
@EnableConfigServer
@Import({NativeEnvironmentRepository.class})
@PropertySource(value = "file:${cc.home}/server/configuration/application.properties")
public class ConfigServiceApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.properties(ConfigServiceProperties.get());
        return builder.sources(DataSourceConfiguration.class, ConfigServiceApplication.class);
    }

    public static void main(String[] args) throws IOException {
        setSystemProperties();
        new SpringApplicationBuilder(ConfigServiceApplication.class)
                .properties(ConfigServiceProperties.get())
                .profiles("default", "embedded")
                .build()
                .run(args);
    }

    private static void setSystemProperties() throws IOException {
        String serverHostname = System.getProperty("server.hostname");
        if (StringUtils.isEmpty(serverHostname)) {
            System.setProperty("server.hostname", HostnameUtil.getHostname());
        }
    }

}
