package com.nextlabs.authentication.config;

import com.nextlabs.authentication.enums.Application;
import com.nextlabs.authentication.models.FileResource;
import com.nextlabs.authentication.services.FileResourceService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * To create file resource from database table which solving the issue where configuration
 * files are created in container's volatile storage.
 *
 * @author Chok Shah Neng
 * @since 2020.09
 */
@Component
public class FileResourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceConfiguration.class);

    @Value("${cc.home}")
    private String ccHome;

    @Autowired
    private FileResourceService fileResourceService;

    @PostConstruct
    private void init()
            throws IOException {
        List<FileResource> fileResources = fileResourceService.findByApplication(Application.CAS.getCode());

        if(fileResources != null && !fileResources.isEmpty()) {
            for(FileResource fileResource : fileResources) {
                File toWrite = new File(populateActualPath(fileResource.getPath()));

                try(FileOutputStream fileOutputStream = new FileOutputStream(toWrite)) {
                    fileOutputStream.write(fileResource.getFile(), 0, fileResource.getFile().length);
                    fileOutputStream.flush();
                } catch(IOException err) {
                    LOGGER.error(err.getMessage(), err);
                    throw err;
                }
            }
        }
    }

    private String populateActualPath(String rawValue) {
        if(StringUtils.isNotEmpty(rawValue)) {
            if(rawValue.contains("${cc.home}")) {
                return rawValue.replace("${cc.home}", ccHome);
            }
        }

        return rawValue;
    }
}
