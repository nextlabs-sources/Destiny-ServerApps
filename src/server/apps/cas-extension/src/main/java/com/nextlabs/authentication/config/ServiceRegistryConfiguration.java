package com.nextlabs.authentication.config;

import java.util.List;

import org.apereo.cas.services.ChainingServiceRegistry;
import org.apereo.cas.services.DefaultChainingServiceRegistry;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServiceRegistry;
import org.apereo.cas.services.ServiceRegistryExecutionPlan;
import org.apereo.cas.services.ServiceRegistryExecutionPlanConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for services allowed by CAS.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class ServiceRegistryConfiguration implements ServiceRegistryExecutionPlanConfigurer {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Bean
    @RefreshScope
    public ChainingServiceRegistry serviceRegistry(RegisteredServices registeredServices) {
        return new DefaultChainingServiceRegistry(applicationContext) {
            @Override
            public RegisteredService save(RegisteredService registeredService) {
                return registeredService;
            }

            @Override
            public boolean delete(RegisteredService registeredService) {
                return false;
            }

            @Override
            public List<RegisteredService> load() {
                return registeredServices.getServices();
            }

            @Override
            public RegisteredService findServiceById(long id) {
                return load().stream().filter(registeredService -> registeredService.getId() == id)
                        .findFirst().orElse(null);
            }

            @Override
            public String getName() {
                return "cc-service-registry";
            }
        };
    }

    @Override
    public void configureServiceRegistry(final ServiceRegistryExecutionPlan plan) {
        plan.registerServiceRegistry(serviceRegistry);
    }

}
