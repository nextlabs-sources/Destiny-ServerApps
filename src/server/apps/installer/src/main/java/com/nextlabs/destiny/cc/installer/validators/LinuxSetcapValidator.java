package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;

import com.nextlabs.destiny.cc.installer.annotations.ValidLinuxSetcap;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;

/**
 * Validator to check if setcap program is found which is used during the Linux installation.
 *
 * @author Sachindra Dasun
 */
public class LinuxSetcapValidator implements ConstraintValidator<ValidLinuxSetcap, CcProperties> {

    @Override
    public boolean isValid(CcProperties ccProperties, ConstraintValidatorContext context) {
        if (!SystemUtils.IS_OS_LINUX) {
            return true;
        }
        return (Paths.get( "/sbin", "setcap").toFile().exists()
                    || Paths.get("/usr", "sbin", "setcap").toFile().exists());
    }

}
