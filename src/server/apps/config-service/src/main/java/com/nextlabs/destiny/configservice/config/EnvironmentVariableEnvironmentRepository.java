package com.nextlabs.destiny.configservice.config;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Environment repository to provide properties from environment variables prefixed
 * with NEXTLABS_CC_APPLICATION_PROFILE.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class EnvironmentVariableEnvironmentRepository implements EnvironmentRepository, Ordered {

    private static final String ENVIRONMENT_VARIABLE_PROPERTY_FORMAT = "NEXTLABS_CC_%s_%s_";

    @Override
    public Environment findOne(String application, String profile, String label) {
        Environment environment = new Environment(application, profile);
        environment.add(new PropertySource("environmentVariablePropertySource",
                getEnvironmentProperties(application, profile)));
        return environment;
    }

    public static Map<String, String> getEnvironmentProperties(String application, String profile) {
        String applicationPropertyPrefix = String.format(ENVIRONMENT_VARIABLE_PROPERTY_FORMAT, application, profile).toLowerCase();
        String commonPropertyPrefix = String.format(ENVIRONMENT_VARIABLE_PROPERTY_FORMAT, "application", profile).toLowerCase();
        return System.getenv()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().startsWith(applicationPropertyPrefix) ||
                        entry.getKey().toLowerCase().startsWith(commonPropertyPrefix)
                )
                .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase()
                                .replaceAll(String.format("%s|%s", applicationPropertyPrefix, commonPropertyPrefix), "")
                                .replace("_", ".")
                        , Map.Entry::getValue));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
