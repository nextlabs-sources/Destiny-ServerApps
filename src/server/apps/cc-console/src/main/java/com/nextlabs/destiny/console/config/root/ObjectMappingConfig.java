package com.nextlabs.destiny.console.config.root;

import com.bettercloud.scim2.common.BaseScimResource;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nextlabs.destiny.console.model.scim.ScimJacksonMixin;

/**
 * Object mapping configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class ObjectMappingConfig {

    @Bean
    public Module baseScimResourceMixinModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.setMixInAnnotation(BaseScimResource.class, ScimJacksonMixin.class);
        return simpleModule;
    }

}
