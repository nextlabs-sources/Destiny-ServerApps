package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.cc.installer.annotations.ValidHealthCheckServicePort;
import com.nextlabs.destiny.cc.installer.config.properties.HealthCheckServiceProperties;

/**
 * Validator for Control Center health check service port.
 *
 * @author Sachindra Dasun
 */
@Component
public class HealthCheckServicePortValidator implements ConstraintValidator<ValidHealthCheckServicePort, HealthCheckServiceProperties> {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServicePortValidator.class);

    @Override
    public boolean isValid(HealthCheckServiceProperties healthCheckServiceProperties, ConstraintValidatorContext context) {
        if (!healthCheckServiceProperties.isEnabled()) {
            return true;
        }
        try {
            new ServerSocket(healthCheckServiceProperties.getPort()).close();
            return true;
        } catch (Exception e) {
            logger.error(String.format("Error opening port: %d", healthCheckServiceProperties.getPort()), e);
        }
        return false;
    }

}
