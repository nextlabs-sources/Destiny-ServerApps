package com.nextlabs.destiny.cc.installer.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.services.DataMigrationService;
import com.nextlabs.destiny.cc.installer.services.ProgressService;

/**
 * Service implementation for data migration during upgrade.
 *
 * @author Sachindra Dasun
 */
@Service
public class DataMigrationServiceImpl implements DataMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(DataMigrationServiceImpl.class);

    @Autowired
    private CcProperties ccProperties;

    @Autowired
    private ProgressService progressService;

    /**
     * Migrate required data from previous installation to the new installation.
     */
    @Override
    public void migrateData() {
        if (ccProperties.getRunningMode() == RunningMode.UPGRADE
                && !ccProperties.getHome().equals(ccProperties.getPreviousHome())) {
            progressService.setCurrentTask(Task.MIGRATE_DATA);
            migrateEnrollmentData();
        }
    }

    private void migrateEnrollmentData() {
        logger.info("Start migrating enrollment data");
        Path enrollmentFolderPath = Paths.get(ccProperties.getHome(), "tools", "enrollment");
        Path previousEnrollmentFolderPath = Paths.get(ccProperties.getPreviousHome(), "tools", "enrollment");
        File[] enrollmentFiles = previousEnrollmentFolderPath.toFile().listFiles();
        if (enrollmentFiles != null) {
            Arrays.stream(enrollmentFiles).filter(file -> {
                boolean accepted;
                if (file.isDirectory()) {
                    accepted = !"security".equals(file.getName());
                } else {
                    accepted = !enrollmentFolderPath.resolve(file.getName()).toFile().exists() &&
                            (file.getName().endsWith(".conn") || file.getName().endsWith(".def")
                                    || file.getName().endsWith(".ldif"));
                }
                if (accepted) {
                    logger.info("Selected: {}", file.getName());
                }
                return accepted;
            }).forEach(file -> {
                try {
                    if (file.isFile()) {
                        FileUtils.copyFileToDirectory(file, enrollmentFolderPath.toFile());
                    } else if (file.isDirectory()) {
                        FileUtils.copyDirectoryToDirectory(file, enrollmentFolderPath.toFile());
                    }
                } catch (IOException e) {
                    logger.error(String.format("Error in copying file %s", file.getPath()), e);
                }
            });
        }
        logger.info("Completed migrating enrollment data");
    }

}
