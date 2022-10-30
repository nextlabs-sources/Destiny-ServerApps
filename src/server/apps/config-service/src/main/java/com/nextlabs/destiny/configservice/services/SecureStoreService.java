package com.nextlabs.destiny.configservice.services;

import java.io.IOException;

/**
 * Secure store service to retrieve zipped secure store files.
 *
 * @author Chok Shah Neng
 */
public interface SecureStoreService {

    byte[] getStoreZip() throws IOException;

}
