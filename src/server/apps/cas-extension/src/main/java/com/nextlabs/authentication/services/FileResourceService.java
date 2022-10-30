package com.nextlabs.authentication.services;

import com.nextlabs.authentication.models.FileResource;

import java.util.List;

/**
 * File resource handling service.
 *
 * @author Chok Shah Neng
 * @since 2020.09
 */
public interface FileResourceService {

    List<FileResource> findByApplication(String application);

}
