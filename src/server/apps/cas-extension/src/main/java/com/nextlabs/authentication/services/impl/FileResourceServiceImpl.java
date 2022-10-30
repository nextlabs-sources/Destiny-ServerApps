package com.nextlabs.authentication.services.impl;

import com.nextlabs.authentication.models.FileResource;
import com.nextlabs.authentication.repositories.FileResourceRepository;
import com.nextlabs.authentication.services.FileResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * File resource handling service.
 *
 * @author Chok Shah Neng
 * @since 2020.09
 */
@Service
public class FileResourceServiceImpl
        implements FileResourceService {

    @Autowired
    private FileResourceRepository fileResourceRepository;

    @Override
    public List<FileResource> findByApplication(String application) {
        return fileResourceRepository.findByApplication(application);
    }
}
