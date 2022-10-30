package com.nextlabs.destiny.cc.installer.services;

import java.io.IOException;

/**
 * Perform operating system service management.
 *
 * @author Sachindra Dasun
 */
public interface ServiceManagementService {

    void stopCCServices() throws IOException;

    void deleteCCServices() throws IOException;

    void createCCServices() throws IOException;

}
