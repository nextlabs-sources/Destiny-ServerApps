package com.nextlabs.destiny.console.services.impl;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.model.FileResource;
import com.nextlabs.destiny.console.repositories.FileResourceRepository;
import com.nextlabs.destiny.console.services.FileResourceService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
public class FileResourceServiceImpl
        implements FileResourceService {

    @Autowired
    private FileResourceRepository fileResourceRepository;

    @Autowired
    private ConfigurationDataLoader configurationDataLoader;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<FileResource> findByApplicationAndModuleAndKey(String application, String module, String key) {
        return fileResourceRepository.findByApplicationAndModuleAndKey(application, module, key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByApplicationAndModule(String application, String module) {
        fileResourceRepository.removeByApplicationAndModule(application, module);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadToDatabase(String application, String module, String key, String filePath, byte[] content) {
        FileResource fileResource;
        Optional<FileResource> fileResourceOptional = fileResourceRepository.findByApplicationAndModuleAndKey(application, module, key);

        if(fileResourceOptional.isPresent()) {
            fileResource = fileResourceOptional.get();
            fileResource.setFile(content);
            fileResource.setModifiedOn(new Date());
        } else {
            fileResource = new FileResource();
            fileResource.setApplication(application);
            fileResource.setModule(module);
            fileResource.setKey(key);
            fileResource.setPath(filePath);
            fileResource.setFile(content);
            fileResource.setModifiedOn(new Date());
            fileResource.setModifiedBy(-1L);
        }

        fileResourceRepository.save(fileResource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exportResource(String folderName, String application, String module, String key)
        throws ConsoleException {
        Optional<FileResource> optionalFileResource = fileResourceRepository.findByApplicationAndModuleAndKey(application, module, key);

        if(optionalFileResource.isPresent()) {
            FileResource fileResource = optionalFileResource.get();
            String fileResourceName = fileResource.getPath().substring(fileResource.getPath().lastIndexOf(File.separator) + 1);

            try {
                FileUtils.writeByteArrayToFile(new File(String.join(File.separator, getExportFolder(folderName).getPath(), fileResourceName)),
                                fileResource.getFile());
            } catch(IOException ioErr) {
                throw new ConsoleException("Error encountered while writing to file,", ioErr);
            }

            return fileResourceName;
        }

        throw new ConsoleException("File resource not found.");
    }

    private File getExportFolder(String folderName) {
        File exportFolder = new File(configurationDataLoader.getPolicyExportsFileLocation().concat(File.separator).concat(folderName));
        if (!exportFolder.exists()) {
            exportFolder.mkdirs();
        }

        return exportFolder;
    }
}
