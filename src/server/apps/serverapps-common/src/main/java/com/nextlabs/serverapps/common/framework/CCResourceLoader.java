package com.nextlabs.serverapps.common.framework;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class CCResourceLoader implements ResourceLoader {

    private final ResourceLoader delegate;
    private final ApplicationContext applicationContext;

    public CCResourceLoader(ApplicationContext applicationContext, ResourceLoader delegate) {
        this.delegate = delegate;
        this.applicationContext = applicationContext;
    }

    @Override
    public Resource getResource(String key) {
        if (key.startsWith(SystemPropertyResource.PROPERTY_URL_PREFIX)) {
            String resourceName = StringUtils.removeStart(key, SystemPropertyResource.PROPERTY_URL_PREFIX);
            Environment environment = this.applicationContext.getBean(Environment.class);
            return new SystemPropertyResource(key, environment.getProperty(resourceName));
        }
        return this.delegate.getResource(key);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.delegate.getClassLoader();
    }
}
