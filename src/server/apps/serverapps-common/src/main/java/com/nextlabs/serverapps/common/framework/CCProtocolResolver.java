package com.nextlabs.serverapps.common.framework;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class CCProtocolResolver implements ProtocolResolver {

    private final ApplicationContext applicationContext;

    public CCProtocolResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // Handle the request for a CC System Property resource, otherwise return null
    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (location.startsWith(SystemPropertyResource.PROPERTY_URL_PREFIX)) {
            String resourceName = StringUtils.removeStart(location, SystemPropertyResource.PROPERTY_URL_PREFIX);
            Environment environment = this.applicationContext.getBean(Environment.class);
            return new SystemPropertyResource(location, environment.getProperty(resourceName));
        }
        return null;
    }
}
