package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.nextlabs.destiny.cc.installer.annotations.ValidHigherUpgradeVersion;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;

/**
 * Validator to check if the upgrade version is higher than the existing version.
 *
 * @author Sachindra Dasun
 */
public class HigherUpgradeVersionValidator implements ConstraintValidator<ValidHigherUpgradeVersion, CcProperties> {

    @Override
    public boolean isValid(CcProperties ccProperties, ConstraintValidatorContext context) {
        return ccProperties.getRunningMode() != RunningMode.UPGRADE ||
                ccProperties.getVersion().compareTo(ccProperties.getPreviousVersion()) >= 0;
    }

}
