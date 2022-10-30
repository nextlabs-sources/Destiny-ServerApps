package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.destiny.cc.installer.annotations.ValidInstallationPathAccess;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;

/**
 * Validator for Control Center installation path access.
 *
 * @author Sachindra Dasun
 */
public class InstallationPathAccessValidator implements ConstraintValidator<ValidInstallationPathAccess, String> {

    private static final Logger logger = LoggerFactory.getLogger(InstallationPathAccessValidator.class);

    @Override
    public boolean isValid(String installationDir, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(installationDir)) {
            return true;
        }
        Path installationPath = Paths.get(installationDir);
        String testFileName = String.format("cc-install-test-%s.txt", RandomStringUtils.randomAlphabetic(6)
                .toLowerCase());
        Path testFilePath;
        try {
            if (installationPath.getFileName() != null &&
                    StringUtils.equalsIgnoreCase(installationPath.getFileName().toString(), CcProperties.NEXTLABS_FOLDER_NAME)) {
                if (installationPath.toFile().exists()) {
                    testFilePath = installationPath.resolve(testFileName);
                } else {
                    testFilePath = installationPath.getParent().resolve(testFileName);
                }
            } else {
                testFilePath = installationPath.resolve(testFileName);
            }
            if (testFilePath.toFile().createNewFile()) {
                logger.info("Installation path access check file created");
                if (Files.deleteIfExists(testFilePath)) {
                    logger.info("Installation path access check file deleted");
                }
                return true;
            }
        } catch (IOException e) {
            logger.error("Error in validating installation path access", e);
        }
        return false;
    }

}
