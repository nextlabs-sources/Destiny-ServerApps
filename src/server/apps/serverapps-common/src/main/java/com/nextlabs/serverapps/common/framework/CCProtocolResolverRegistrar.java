package com.nextlabs.serverapps.common.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

public class CCProtocolResolverRegistrar implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CCProtocolResolverRegistrar.class);

    private CCProtocolResolver ccProtocolResolver;

    public CCProtocolResolverRegistrar(CCProtocolResolver ccProtocolResolver) {
        this.ccProtocolResolver = ccProtocolResolver;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            final ConfigurableApplicationContext configurableApplicationContext
                    = (ConfigurableApplicationContext) applicationContext;

            logger.info(
                    "Adding instance of {} to the set of protocol resolvers",
                    this.ccProtocolResolver.getClass().getCanonicalName()
            );
            configurableApplicationContext.addProtocolResolver(this.ccProtocolResolver);
        }
    }
}
