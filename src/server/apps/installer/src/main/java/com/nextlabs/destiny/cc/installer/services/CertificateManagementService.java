package com.nextlabs.destiny.cc.installer.services;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;

/**
 * Manage SSL certificates used
 *
 * @author Sachindra Dasun
 */
public interface CertificateManagementService {

    void createInstallerCertificate() throws IOException;

    void importCertificate(String alias, String trustStoreFileName, String password,
                           String certificateFileName) throws IOException;

    void createCertificates() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException;

    void deleteCertificate(String alias, String trustStoreFileName, String password) throws IOException;

    boolean createCacerts(CcProperties ccProperties, String password, boolean copyToOverrideFolder) throws IOException;

    void uploadKeyStores() throws IOException, NoSuchAlgorithmException;

}
