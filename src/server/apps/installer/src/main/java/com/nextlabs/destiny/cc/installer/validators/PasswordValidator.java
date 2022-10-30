package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.cc.installer.annotations.ValidPassword;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;

/**
 * Validator for Control Center passwords.
 *
 * @author Sachindra Dasun
 */
@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static Pattern pattern;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (RunningMode.INSTALLATION.name().equals(System.getProperty("nextlabs.cc.running-mode")) &&
                Boolean.parseBoolean(System.getProperty("nextlabs.cc.is-management-server", "false"))) {
            return StringUtils.isNotEmpty(password) &&
                    pattern.matcher(EncryptionHelper.decryptIfEncrypted(password)).matches();
        }
        return true;
    }

    @Value("${nextlabs.cc.password.pattern}")
    public void setPasswordPattern(String pattern) {
        PasswordValidator.pattern = Pattern.compile(pattern);
    }

}
