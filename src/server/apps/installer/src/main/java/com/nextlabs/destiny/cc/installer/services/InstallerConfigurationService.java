package com.nextlabs.destiny.cc.installer.services;

import java.io.IOException;

/**
 * Configure control center before running installer.
 *
 * @author Sachindra Dasun
 */
public interface InstallerConfigurationService {

    void configureInstaller() throws IOException;

}
