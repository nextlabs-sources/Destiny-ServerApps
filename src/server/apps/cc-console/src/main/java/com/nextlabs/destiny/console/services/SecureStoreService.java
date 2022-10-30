package com.nextlabs.destiny.console.services;

import com.nextlabs.destiny.console.dto.store.CertificateDTO;
import com.nextlabs.destiny.console.enums.SecureStoreFile;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import org.springframework.web.multipart.MultipartFile;

import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SecureStoreService {

    List<CertificateDTO> list(String storeType);

    List<CertificateDTO> getEntriesByStoreName(String storeName);

    CertificateDTO getDetails(String storeName, String alias) throws ConsoleException;

    boolean generateKey(CertificateDTO certificateDTO) throws ConsoleException;

    KeyStore getKeyStore(SecureStoreFile secureStoreFile);

    List<CertificateDTO> listKey(String fileName, String filePassword,
                                 byte[] content) throws ConsoleException;

    boolean importKey(String fileName, String filePassword, Map<String, String> entries, boolean overwrite,
                    byte[] content, String targetStore) throws ConsoleException;

    boolean importKey(byte[] certificate, byte[] privateKey, boolean overwrite, String targetStore, String alias)
        throws ConsoleException;

    String generateCsr(CertificateDTO certificateDTO) throws ConsoleException;

    boolean importCertificate(String storeName, String alias, String certificateFileName,
                    boolean verifyTrustChain, boolean aliasShouldExist, boolean replaceIfAliasExist, byte[] certificate)
        throws ConsoleException;

    String exportCertificate(String storeName, String alias) throws ConsoleException;

    boolean removeEntry(String storeName, String alias) throws ConsoleException;

    boolean removeEntries(Map<String, Set<String>> entries) throws ConsoleException;

    boolean changePassword(String storeType, String password) throws ConsoleException;

    String exportAll() throws ConsoleException;

    void replaceStoreFile(List<MultipartFile> storeFiles) throws ConsoleException;

    void updateStoresToDatabase(SecureStoreFile... secureStoreFiles) throws ConsoleException;

}
