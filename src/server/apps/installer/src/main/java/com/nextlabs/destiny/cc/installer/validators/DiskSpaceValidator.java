package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.nextlabs.destiny.cc.installer.enums.OperatingSystem;
import org.apache.commons.lang3.StringUtils;

import com.nextlabs.destiny.cc.installer.annotations.ValidDiskSpace;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator to check if disk has enough space to perform installation.
 *
 * @author Sachindra Dasun
 */
public class DiskSpaceValidator implements ConstraintValidator<ValidDiskSpace, CcProperties> {

    private static final long MIN_REQUIRED_SPACE = 4L * 1024 * 1024 * 1024;

    private static final Logger log = LoggerFactory.getLogger(DiskSpaceValidator.class);

    @Override
    public boolean isValid(CcProperties ccProperties, ConstraintValidatorContext context) {
        long freeSpaceInBytes;
        Path installPath, rootPath;
        if (ccProperties.getRunningMode() == RunningMode.UPGRADE && ccProperties.isUpgradeExisting()) {
            installPath = Paths.get(ccProperties.getPreviousHome());
        } else if (StringUtils.isNotEmpty(ccProperties.getInstallationPath())) {
            installPath = Paths.get(ccProperties.getInstallationPath());
        } else {
            installPath = Paths.get(ccProperties.getHome());
        }

        if (ccProperties.getOperatingSystem().equals(OperatingSystem.WINDOWS)) {
            rootPath = installPath.getRoot();
        } else {
            rootPath = installPath.toAbsolutePath();
            try {
                FileStore fs = Files.getFileStore(installPath);
                while (rootPath.getParent() != null && fs.equals(Files.getFileStore(rootPath.getParent()))) {
                    rootPath = rootPath.getParent();
                }
            } catch (IOException | SecurityException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        }
        freeSpaceInBytes = rootPath.toFile().getFreeSpace();

        if (freeSpaceInBytes <= MIN_REQUIRED_SPACE) {
            log.info("Available free space on {} {} MB is not enough. The installation requires free space more than {} MB.",
                    rootPath.toFile().getAbsolutePath(),
                    freeSpaceInBytes / 1024 / 1024,
                    MIN_REQUIRED_SPACE / 1024 / 1024);
            return false;
        } else {
            return true;
        }
    }

}
