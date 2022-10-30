package com.nextlabs.destiny.cc.installer.services;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Future;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;

/**
 * Handle Control Center installation.
 *
 * @author Sachindra Dasun
 */
public interface InstallService {

    void createCcStartFiles() throws IOException;

    Future<Boolean> install(@Valid CcProperties ccProperties) throws Exception;

    void grantExecutePermission(Path filePath) throws IOException;

}
