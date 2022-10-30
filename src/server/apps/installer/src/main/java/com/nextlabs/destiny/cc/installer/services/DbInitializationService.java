package com.nextlabs.destiny.cc.installer.services;

import java.io.IOException;

/**
 * Perform database initialization for Control Center.
 *
 * @author Sachindra Dasun
 */
public interface DbInitializationService {

    void initialize() throws IOException;

}
