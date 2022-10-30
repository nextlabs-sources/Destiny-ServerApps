package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.file.Paths;

import com.nextlabs.destiny.cc.installer.annotations.ValidEmptyLogQueue;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;

/**
 * Validator to check if logqueue folder is empty before upgrade.
 *
 * @author Sachindra Dasun
 */
public class EmptyLogQueueValidator implements ConstraintValidator<ValidEmptyLogQueue, CcProperties> {

    @Override
    public boolean isValid(CcProperties ccProperties, ConstraintValidatorContext context) {
        if (ccProperties.getRunningMode() == RunningMode.UPGRADE
                && ccProperties.getVersion().compareTo(ccProperties.getPreviousVersion()) != 0) {
            String[] files = Paths.get(ccProperties.getPreviousHome(), "server", "logqueue").toFile().list();
            return files == null || files.length == 0;
        }
        return true;
    }

}
