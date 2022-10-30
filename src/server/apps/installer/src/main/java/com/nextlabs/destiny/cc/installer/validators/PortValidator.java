package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.cc.installer.annotations.ValidPort;
import com.nextlabs.destiny.cc.installer.enums.PortType;

/**
 * Validator for Control Center network ports.
 *
 * @author Sachindra Dasun
 */
@Component
public class PortValidator implements ConstraintValidator<ValidPort, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(PortValidator.class);
    private static int defaultAppServicePort = -1;
    private PortType portType;

    @Override
    public void initialize(ValidPort constraintAnnotation) {
        portType = constraintAnnotation.portType();
    }

    @Override
    public boolean isValid(Integer port, ConstraintValidatorContext context) {
        try {
            if (Boolean.parseBoolean(System.getProperty("nextlabs.cc.is-management-server", "false"))
                    || portType == PortType.WEB_SERVICE || portType == PortType.SERVER_SHUTDOWN
                    || portType == PortType.POLICY_VALIDATOR) {
                if (portType == PortType.APP_SERVICE
                        && Boolean.parseBoolean(System.getProperty("nextlabs.cc.web-installer", "false"))
                        && defaultAppServicePort == port) {
                    return true;
                }
                if (portType == PortType.SERVER_SHUTDOWN
                        && Boolean.parseBoolean(System.getProperty("nextlabs.cc.web-installer", "false"))) {
                    return true;
                }
                new ServerSocket(port).close();
            }
            return true;
        } catch (Exception e) {
            logger.error(String.format("Error opening port: %d", port), e);
        }
        return false;
    }

    @Value("${nextlabs.cc.port.app-service-port}")
    public void setDefaultAppServicePort(int defaultAppServicePort) {
        PortValidator.defaultAppServicePort = defaultAppServicePort;
    }

}
