package com.nextlabs.authentication.config;

import com.nextlabs.serverapps.common.framework.SystemPropertyResource;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.nextlabs.serverapps.common.properties.CasOidcProperties;

@Component
public class CustomBeanPostProcessor implements BeanPostProcessor, Ordered {

    private static final String OIDC_SIGNING_JWKS = "cc-oidc-config.signingJwks";
    private final CasOidcProperties casOidcProperties;

    @Autowired
    public CustomBeanPostProcessor(CasOidcProperties casOidcProperties){
        this.casOidcProperties = casOidcProperties;
    }

    private static final Logger logger = LoggerFactory.getLogger(CustomBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName){
        logger.debug("Calling bean post processor before init for bean:: {} class name: {}",
                beanName, bean.getClass());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName){
        if (bean instanceof CasConfigurationProperties) {
            logger.debug("Calling bean post processor after init for bean:: {} class name: {}",
                    beanName, bean.getClass());
            CasConfigurationProperties casProperties = (CasConfigurationProperties) bean;
            casProperties.getAuthn().getOidc().getJwks().setJwksFile(SystemPropertyResource.PROPERTY_URL_PREFIX + OIDC_SIGNING_JWKS);
            casProperties.getAuthn().getOidc().setIssuer(casOidcProperties.getOidcIssuer());
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
