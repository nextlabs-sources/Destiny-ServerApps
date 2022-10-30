package com.nextlabs.destiny.console.services;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.FileResource;

import java.util.Optional;

public interface FileResourceService {

    Optional<FileResource> findByApplicationAndModuleAndKey(String application, String module, String key);

    void removeByApplicationAndModule(String application, String module);

    void uploadToDatabase(String application, String module, String key, String filePath, byte[] content);

    String exportResource(String folderName, String application, String module, String key) throws ConsoleException;

}
