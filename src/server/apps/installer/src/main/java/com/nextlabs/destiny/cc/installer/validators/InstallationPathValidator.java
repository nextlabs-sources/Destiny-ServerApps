package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.destiny.cc.installer.annotations.ValidInstallationPath;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;

/**
 * Validator for Control Center installation path.
 *
 * @author Sachindra Dasun
 */
public class InstallationPathValidator implements ConstraintValidator<ValidInstallationPath, String> {

    private static final Logger logger = LoggerFactory.getLogger(InstallationPathValidator.class);

    @Override
    public boolean isValid(String installationDir, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(installationDir)) {
            return true;
        }
        try {
            String ccFolderName = Paths.get(ParameterHelper.INIT_CC_HOME).toFile().getCanonicalFile().getName();
            Path installationPath = Paths.get(installationDir);
            if (RunningMode.UPGRADE.name().equals(System.getProperty("nextlabs.cc.running-mode"))
                    && installationPath.equals(Paths.get(System.getProperty("nextlabs.cc.previous-home")))) {
                return false;
            }
            if (installationPath.getFileName() != null &&
                    StringUtils.equalsIgnoreCase(installationPath.getFileName().toString(), CcProperties.NEXTLABS_FOLDER_NAME)) {
                return installationPath.getParent().toFile().exists()
                        && !installationPath.resolve(ccFolderName).toFile().exists();
            } else {
                File installationPathFolder = installationPath.toFile();
                return installationPathFolder.exists()
                        && installationPathFolder.isDirectory()
                        && !installationPath.resolve(CcProperties.NEXTLABS_FOLDER_NAME).resolve(ccFolderName).toFile().exists();
            }
        } catch (IOException e) {
            logger.error("Error in validating installation path", e);
        }
        return false;
    }

}
