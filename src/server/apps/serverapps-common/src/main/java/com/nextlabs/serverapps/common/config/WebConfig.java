package com.nextlabs.serverapps.common.config;

import com.nextlabs.serverapps.common.framework.CCProtocolResolver;
import com.nextlabs.serverapps.common.framework.CCProtocolResolverRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public CCProtocolResolverRegistrar getCcProtocolResolver(ApplicationContext context){
        return new CCProtocolResolverRegistrar(new CCProtocolResolver(context));
    }
}
