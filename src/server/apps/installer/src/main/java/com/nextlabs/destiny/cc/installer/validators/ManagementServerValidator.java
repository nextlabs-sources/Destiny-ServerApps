package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.nextlabs.destiny.cc.installer.annotations.ValidManagementServer;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.config.properties.ManagementServerProperties;

/**
 * Validator for management server details.
 *
 * @author Sachindra Dasun
 */
public class ManagementServerValidator implements ConstraintValidator<ValidManagementServer, CcProperties> {

    @Override
    public boolean isValid(CcProperties ccProperties, ConstraintValidatorContext context) {
        if (ccProperties.isManagementServerInstance()) {
            return true;
        }
        ManagementServerProperties managementServerProperties = ccProperties.getManagementServer();
        return StringUtils.isNotEmpty(managementServerProperties.getHost()) &&
                StringUtils.isNotEmpty(managementServerProperties.getUsername()) &&
                StringUtils.isNotEmpty(managementServerProperties.getPassword()) &&
                managementServerProperties.getWebServicePort() > 0 &&
                managementServerProperties.getConfigServicePort() > 0;
    }

}
