package com.nextlabs.destiny.console.services.impl;

import com.google.common.io.BaseEncoding;
import com.nextlabs.destiny.console.config.properties.KeyStoreProperties;
import com.nextlabs.destiny.console.config.properties.TrustStoreProperties;
import com.nextlabs.destiny.console.dto.config.SysConfigValueDTO;
import com.nextlabs.destiny.console.dto.store.CertificateDTO;
import com.nextlabs.destiny.console.dto.store.SecureStoreDTO;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.EntryType;
import com.nextlabs.destiny.console.enums.Hash;
import com.nextlabs.destiny.console.enums.SecureStoreFile;
import com.nextlabs.destiny.console.enums.SecureStoreType;
import com.nextlabs.destiny.console.enums.SysConfigEntry;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.model.SecureStore;
import com.nextlabs.destiny.console.repositories.SecureStoreRepository;
import com.nextlabs.destiny.console.services.EntityAuditLogService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.SecureStoreService;
import com.nextlabs.destiny.console.services.SysConfigService;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementation of secure store service.
 * This service uses combination of KeyManager, TrustManager and keytool to perform maintenance
 * of secure stores.
 *
 * @author Chok Shah Neng
 * @since 9.5
 */
@Service
public class SecureStoreServiceImpl
        implements SecureStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureStoreServiceImpl.class);
    private static final String CACERT_FILE_NAME = "cacerts";
    private static final String EXPORT_FOLDER = "SecureStore";
    private static final String ZIPPED_STORE_FILE = "stores.zip";
    private static final Object SYNCHRONIZATION_LOCK = new Object();

    // Collection of key/trust store files
    private static final SecureStoreFile[] KEY_STORES = new SecureStoreFile[]{
                    SecureStoreFile.AGENT_KEY_STORE,
                    SecureStoreFile.APPLICATION_KEY_STORE,
                    SecureStoreFile.DCC_KEY_STORE,
                    SecureStoreFile.DIGITAL_SIGNATURE_KEY_STORE,
                    SecureStoreFile.LEGACY_AGENT_KEY_STORE,
                    SecureStoreFile.SAML2_KEY_STORE,
                    SecureStoreFile.WEB_KEY_STORE
    };
    private static final SecureStoreFile[] TRUST_STORES = new SecureStoreFile[]{
                    SecureStoreFile.AGENT_TRUST_STORE,
                    SecureStoreFile.APPLICATION_TRUST_STORE,
                    SecureStoreFile.CACERTS_TRUST_STORE,
                    SecureStoreFile.DCC_TRUST_STORE,
                    SecureStoreFile.DIGITAL_SIGNATURE_TRUST_STORE,
                    SecureStoreFile.LEGACY_AGENT_TRUST_STORE,
                    SecureStoreFile.LEGACY_AGENT_TRUST_STORE_KP,
                    SecureStoreFile.WEB_TRUST_STORE
    };
    // Collection of files using key/trust password
    private static final SecureStoreFile[] KEY_PASSWORD_STORES = new SecureStoreFile[] {
                    SecureStoreFile.AGENT_KEY_STORE,
                    SecureStoreFile.APPLICATION_KEY_STORE,
                    SecureStoreFile.DCC_KEY_STORE,
                    SecureStoreFile.DIGITAL_SIGNATURE_KEY_STORE,
                    SecureStoreFile.FPE_KEY_STORE,
                    SecureStoreFile.LEGACY_AGENT_KEY_STORE,
                    SecureStoreFile.LEGACY_AGENT_TRUST_STORE,
                    SecureStoreFile.LEGACY_AGENT_TRUST_STORE_KP,
                    SecureStoreFile.SAML2_KEY_STORE,
                    SecureStoreFile.WEB_KEY_STORE
    };
    private static final SecureStoreFile[] TRUST_PASSWORD_STORES = new SecureStoreFile[] {
                    SecureStoreFile.AGENT_TRUST_STORE,
                    SecureStoreFile.APPLICATION_TRUST_STORE,
                    SecureStoreFile.CACERTS_TRUST_STORE,
                    SecureStoreFile.DCC_TRUST_STORE,
                    SecureStoreFile.DIGITAL_SIGNATURE_TRUST_STORE,
                    SecureStoreFile.WEB_TRUST_STORE
    };

    @Value("${cc.home}")
    private String controlCenterHome;

    @Autowired
    private KeyStoreProperties keyStoreProperties;

    @Autowired
    private TrustStoreProperties trustStoreProperties;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private EntityAuditLogService entityAuditLogService;

    @Autowired
    private MessageBundleService msgBundleService;

    @Autowired
    private SecureStoreRepository secureStoreRepository;

    @Autowired
    private ConfigurationDataLoader configurationDataLoader;

    @Override
    public List<CertificateDTO> list(String storeType) {
        File certificateFolder = getCertificateDirectory();

        if(certificateFolder.exists()) {
            synchronized(SYNCHRONIZATION_LOCK) {
                if(SecureStoreType.KEYSTORE.name().equalsIgnoreCase(storeType)) {
                    return readEntries(certificateFolder, true, KEY_STORES);
                } else {
                    return readEntries(certificateFolder, true, TRUST_STORES);
                }
            }
        }

        return new ArrayList<>();
    }

    @Override
    public List<CertificateDTO> getEntriesByStoreName(String storeName) {
        File certificateFolder = getCertificateDirectory();

        List<CertificateDTO> entries = new ArrayList<>();
        if(certificateFolder.exists()) {
            synchronized(SYNCHRONIZATION_LOCK) {
                return readEntries(certificateFolder, true, false, SecureStoreFile.getStoreFileByName(storeName));
            }
        } else {
            LOGGER.error("Certificate directory is not exist.");
        }

        return entries;
    }

    @Override
    public CertificateDTO getDetails(String storeName, String alias)
                throws ConsoleException {
        File certificateFolder = getCertificateDirectory();

        if(certificateFolder.exists()) {
            synchronized(SYNCHRONIZATION_LOCK) {
                StoreAccess storeAccess = getAccessByStoreName(storeName);
                String secureStoreFileName = storeName.concat(".")
                                .concat(storeName.equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());

                CertificateDTO certificateDTO = readEntry(new File(certificateFolder, secureStoreFileName), storeAccess, alias);
                certificateDTO.setStoreName(storeName);

                entityAuditLogService.addEntityAuditLog(AuditAction.RETRIEVE,
                                AuditableEntity.SECURE_STORE.getCode(), -1L,
                                certificateDTO.toAuditString(),
                                null);

                return certificateDTO;
            }
        } else {
            LOGGER.error("Certificate directory is not exist.");
        }

        return new CertificateDTO();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateKey(CertificateDTO certificateDTO)
                    throws ConsoleException {
        File certificateDirectory = getCertificateDirectory();

        if(certificateDirectory.exists() && certificateDirectory.canWrite()) {
            synchronized(SYNCHRONIZATION_LOCK) {
                StoreAccess storeAccess = getAccessByStoreName(certificateDTO.getStoreName());
                String secureStoreFileName = certificateDTO.getStoreName().concat(".")
                                .concat(certificateDTO.getStoreName().equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());
                File secureStore = new File(certificateDirectory, secureStoreFileName);

                if(isAliasExist(secureStore, storeAccess, certificateDTO.getAlias())) {
                    throw new NotUniqueException(msgBundleService.getText("server.error.not.unique.code"),
                                    msgBundleService.getText("server.error.alias.not.unique",
                                                    certificateDTO.getAlias()));
                }

                try {
                    List<String> arguments = new ArrayList<>();
                    arguments.add(EntryType.KEY_PAIR.name().equalsIgnoreCase(certificateDTO.getType()) ? "-genkeypair" : "-genseckey");
                    arguments.add("-keystore");
                    arguments.add(secureStore.getPath());
                    arguments.add("-storepass");
                    arguments.add(escapeQuotes(storeAccess.getPassword()));
                    arguments.add("-keypass");
                    arguments.add(escapeQuotes(storeAccess.getPassword()));
                    arguments.add("-alias");
                    arguments.add(certificateDTO.getAlias());
                    arguments.add("-keyalg");
                    arguments.add(certificateDTO.getKeyAlgorithmName());
                    arguments.add("-keysize");
                    arguments.add(Integer.toString(certificateDTO.getKeySize()));

                    if(EntryType.KEY_PAIR.name().equalsIgnoreCase(certificateDTO.getType())) {
                        arguments.add("-dName");
                        arguments.add(certificateDTO.getSubjectDN());
                        arguments.add("-validity");
                        arguments.add(Integer.toString(certificateDTO.getValidity()));
                        arguments.add("-startdate");
                        arguments.add(calculateStartDate(certificateDTO.getValidFrom()));

                        if (StringUtils.isNotBlank(certificateDTO.getSignatureAlgorithmName())) {
                            arguments.add("-sigalg");
                            arguments.add(certificateDTO.getSignatureAlgorithmName());
                        }

                        if(certificateDTO.getNamedExtensions() != null
                                && !certificateDTO.getNamedExtensions().isEmpty()) {
                            for(Map.Entry<String, String> namedExtension : certificateDTO.getNamedExtensions().entrySet()) {
                                arguments.add("-ext");
                                arguments.add(namedExtension.getKey().concat("=").concat(namedExtension.getValue()));
                            }
                        }
                    }

                    executeKeytool(arguments);
                    uploadToDatabase(secureStore);

                    CertificateDTO keyCertificate = readEntry(secureStore, storeAccess, certificateDTO.getAlias());
                    keyCertificate.setStoreName(certificateDTO.getStoreName());

                    entityAuditLogService.addEntityAuditLog(AuditAction.CREATE,
                                    AuditableEntity.SECURE_STORE.getCode(), -1L,
                                    null,
                                    certificateDTO.toAuditString());

                    return true;
                } catch(IOException | NoSuchAlgorithmException err) {
                    restoreFromDatabase(certificateDirectory);
                    throw new ConsoleException(msgBundleService.getText("keytool.execution.error.code"),
                                    err.getMessage(), err.getMessage(), err);
                }
            }
        } else {
            throw new ConsoleException("Certificate directory is not exist or writeable.");
        }
    }

    @Override
    public String generateCsr(CertificateDTO certificateDTO)
                    throws ConsoleException {
        File certificateDirectory = getCertificateDirectory();
        String csrFileName = certificateDTO.getAlias().concat(".csr");

        if (certificateDirectory.exists() && certificateDirectory.canRead()) {
            File exportFolder = getExportFolder();
            synchronized(SYNCHRONIZATION_LOCK) {
                StoreAccess storeAccess = getAccessByStoreName(certificateDTO.getStoreName());
                String secureStoreFileName = certificateDTO.getStoreName().concat(".")
                                .concat(certificateDTO.getStoreName().equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());
                File storeFile = new File(certificateDirectory, secureStoreFileName);
                certificateDTO = readEntry(storeFile, storeAccess, certificateDTO.getAlias());
                try {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("-certreq");
                    arguments.add("-noprompt");
                    arguments.add("-keystore");
                    arguments.add(storeFile.getPath());
                    arguments.add("-storepass");
                    arguments.add(escapeQuotes(storeAccess.getPassword()));
                    arguments.add("-alias");
                    arguments.add(certificateDTO.getAlias());
                    arguments.add("-file");
                    arguments.add((new File(exportFolder, csrFileName)).getPath());

                    if(certificateDTO.getNamedExtensions() != null
                            && !certificateDTO.getNamedExtensions().isEmpty()) {
                        for(Map.Entry<String, String> namedExtension : certificateDTO.getNamedExtensions().entrySet()) {
                            arguments.add("-ext");
                            arguments.add(namedExtension.getKey().concat("=").concat(namedExtension.getValue()));
                        }
                    }

                    executeKeytool(arguments);
                } catch (IOException err) {
                    throw new ConsoleException(msgBundleService.getText("keytool.execution.error.code"),
                                    err.getMessage(), err.getMessage(), err);
                }
            }
        } else {
            throw new ConsoleException("Certificate directory is not exist or readable.");
        }

        return csrFileName;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importCertificate(String storeName, String alias, String certificateFileName,
                    boolean verifyTrustChain, boolean aliasShouldExist, boolean replaceIfAliasExist, byte[] content)
                    throws ConsoleException {
        File certificateDirectory = getCertificateDirectory();

        if (certificateDirectory.exists() && certificateDirectory.canWrite()) {
            synchronized(SYNCHRONIZATION_LOCK) {
                StoreAccess storeAccess = getAccessByStoreName(storeName);
                String secureStoreFileName = storeName.concat(".")
                                .concat(storeName.equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());
                File certificateToImport = new File(certificateDirectory, certificateFileName);
                File secureStore = new File(certificateDirectory, secureStoreFileName);

                if(aliasShouldExist != isAliasExist(secureStore, storeAccess, alias)) {
                    if(aliasShouldExist) {
                        throw new NoDataFoundException(msgBundleService.getText("server.error.alias.not.found.code"),
                                        msgBundleService.getText("server.error.alias.not.found",
                                                        alias));
                    } else if(!replaceIfAliasExist) {
                        throw new NotUniqueException(msgBundleService.getText("server.error.not.unique.code"),
                                        msgBundleService.getText("server.error.alias.not.unique",
                                                        alias));
                    } else {
                        // remove alias if it exists
                        removeEntry(storeName, alias);
                    }
                }

                try {
                    FileUtils.writeByteArrayToFile(certificateToImport, content);

                    List<String> arguments = new ArrayList<>();
                    arguments.add("-importcert");
                    arguments.add("-noprompt");
                    arguments.add("-keystore");
                    arguments.add(secureStore.getPath());
                    arguments.add("-storepass");
                    arguments.add(escapeQuotes(storeAccess.getPassword()));
                    arguments.add("-alias");
                    arguments.add(alias);
                    arguments.add("-file");
                    arguments.add(certificateToImport.getPath());

                    if(verifyTrustChain) {
                        arguments.add("-trustcacerts");
                    }

                    executeKeytool(arguments);
                    uploadToDatabase(secureStore);

                    CertificateDTO certificateDTO = readEntry(secureStore, storeAccess, alias);
                    certificateDTO.setStoreName(storeName);

                    entityAuditLogService.addEntityAuditLog(AuditAction.CREATE,
                                    AuditableEntity.SECURE_STORE.getCode(), -1L,
                                    null,
                                    certificateDTO.toAuditString());

                    return true;
                } catch(IOException | NoSuchAlgorithmException err) {
                    restoreFromDatabase(certificateDirectory);
                    throw new ConsoleException(
                                    msgBundleService.getText("keytool.execution.error.code"),
                                    err.getMessage(), err.getMessage(), err);
                } finally {
                    FileUtils.deleteQuietly(certificateToImport);
                }
            }
        } else {
            throw new ConsoleException("Certificate directory is not exist or readable.");
        }
    }

    @Override
    public String exportCertificate(String storeName, String alias)
                    throws ConsoleException {
        File certificateDirectory = getCertificateDirectory();
        String certificateFileName = alias.concat(".cer");

        if (certificateDirectory.exists() && certificateDirectory.canRead()) {
            File exportFolder = getExportFolder();

            synchronized(SYNCHRONIZATION_LOCK) {
                StoreAccess storeAccess = getAccessByStoreName(storeName);
                String secureStoreFileName = storeName.concat(".")
                                .concat(storeName.equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());

                try {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("-exportcert");
                    arguments.add("-noprompt");
                    arguments.add("-keystore");
                    arguments.add(new File(certificateDirectory, secureStoreFileName).getPath());
                    arguments.add("-storepass");
                    arguments.add(escapeQuotes(storeAccess.getPassword()));
                    arguments.add("-alias");
                    arguments.add(alias);
                    arguments.add("-file");
                    arguments.add((new File(exportFolder, certificateFileName)).getPath());

                    executeKeytool(arguments);
                } catch(IOException err) {
                    throw new ConsoleException(msgBundleService.getText("keytool.execution.error.code"),
                                    err.getMessage(), err.getMessage(), err);
                }
            }
        } else {
            throw new ConsoleException("Certificate directory is not exist or readable.");
        }

        return certificateFileName;
    }

    /**
     * Remove entry from store
     * @param storeName Secure store name contains the certificate
     * @param alias Alias of entry to remove
     * @return <tt>true</tt> if this store changed as a result of the call
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeEntry(String storeName, String alias)
            throws ConsoleException {
        File certificateDirectory = getCertificateDirectory();

        if (certificateDirectory.exists() && certificateDirectory.canWrite()) {
            synchronized(SYNCHRONIZATION_LOCK) {
                StoreAccess storeAccess = getAccessByStoreName(storeName);
                String secureStoreFileName = storeName.concat(".")
                                .concat(storeName.equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());
                File secureStore = new File(certificateDirectory, secureStoreFileName);

                try {
                    CertificateDTO certificateDTO = readEntry(secureStore, storeAccess, alias);
                    certificateDTO.setStoreName(storeName);

                    entityAuditLogService.addEntityAuditLog(AuditAction.DELETE,
                                    AuditableEntity.SECURE_STORE.getCode(), -1L,
                                    certificateDTO.toAuditString(),
                                    null);

                    removeAlias(secureStore.getPath(), storeAccess.getPassword(), alias);
                    uploadToDatabase(secureStore);

                    return true;
                } catch(IOException | NoSuchAlgorithmException err) {
                    restoreFromDatabase(certificateDirectory);
                    throw new ConsoleException(msgBundleService.getText("keytool.execution.error.code"),
                                    err.getMessage(), err.getMessage(), err);
                }
            }
        } else {
            throw new ConsoleException("Certificate directory is not exist or writeable.");
        }
    }

    /**
     * Remove entries from store
     * @param entries Secure store name and aliases that contains the certificate
     * @return <tt>true</tt> if this store changed as a result of the call
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeEntries(Map<String, Set<String>> entries)
            throws ConsoleException {
        File certificateDirectory = getCertificateDirectory();

        if (certificateDirectory.exists() && certificateDirectory.canWrite()) {
            synchronized(SYNCHRONIZATION_LOCK) {
                try {
                    StoreAccess storeAccess;
                    for(Map.Entry<String, Set<String>> entry : entries.entrySet()) {
                        storeAccess = getAccessByStoreName(entry.getKey());
                        String secureStoreFileName = entry.getKey().concat(".")
                                        .concat(entry.getKey().equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());
                        File secureStore = new File(certificateDirectory, secureStoreFileName);
                        for(String alias : entry.getValue()) {
                            CertificateDTO certificateDTO = readEntry(secureStore, storeAccess, alias);
                            certificateDTO.setStoreName(entry.getKey());

                            entityAuditLogService.addEntityAuditLog(AuditAction.DELETE,
                                            AuditableEntity.SECURE_STORE.getCode(), -1L,
                                            certificateDTO.toAuditString(),
                                            null);

                            removeAlias(secureStore.getPath(), storeAccess.getPassword(), alias);
                        }

                        uploadToDatabase(secureStore);
                    }

                    return true;
                } catch (IOException | NoSuchAlgorithmException err) {
                    restoreFromDatabase(certificateDirectory);
                    throw new ConsoleException(msgBundleService.getText("keytool.execution.error.code"),
                                    err.getMessage(), err.getMessage(), err);
                }
            }
        } else {
            throw new ConsoleException("Certificate directory is not exist or writeable.");
        }
    }

    /**
     * Change password of store
     * @param storeType KEYSTORE or TRUSTSTORE
     * @param password  New store password
     * @return <tt>true</tt> if this store password changed as a result of the call
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(String storeType, String password)
            throws ConsoleException {
        File certificateDirectory = getCertificateDirectory();

        if (certificateDirectory.exists() && certificateDirectory.canWrite()) {
            synchronized(SYNCHRONIZATION_LOCK) {
                Set<String> storeFileSet = new HashSet<>();

                if(SecureStoreType.KEYSTORE.name().equalsIgnoreCase(storeType)) {
                    for(SecureStoreFile secureStoreFile : KEY_PASSWORD_STORES) {
                        StoreAccess storeAccess = getAccessByStoreName(secureStoreFile.getStoreName());
                        storeFileSet.add(secureStoreFile.getStoreName().concat(".").concat(storeAccess.getFileExtension()));
                    }
                } else if(SecureStoreType.TRUSTSTORE.name().equalsIgnoreCase(storeType)){
                    for(SecureStoreFile secureStoreFile : TRUST_PASSWORD_STORES) {
                        StoreAccess storeAccess = getAccessByStoreName(secureStoreFile.getStoreName());
                        storeFileSet.add(secureStoreFile.getStoreName().concat(".")
                                        .concat(secureStoreFile.getStoreName().equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension()));
                    }
                }

                try {
                    File[] secureStores = certificateDirectory
                                    .listFiles(file -> storeFileSet.contains(file.getName()));

                    changePassword(getAccessByStoreType(storeType), secureStores, password);
                    uploadToDatabase(secureStores);

                    SecureStoreDTO secureStoreDTO = new SecureStoreDTO();
                    secureStoreDTO.setStoreType(storeType);
                    entityAuditLogService.addEntityAuditLog(AuditAction.CHANGE_PASSWORD,
                                    AuditableEntity.SECURE_STORE.getCode(), -1L,
                                    secureStoreDTO.toAuditString(),
                                    secureStoreDTO.toAuditString());
                    return true;
                } catch(KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException err) {
                    restoreFromDatabase(certificateDirectory);
                    throw new ConsoleException(msgBundleService.getText("keytool.execution.error.code"),
                                    err.getMessage(), err.getMessage(), err);
                }
            }
        } else {
            throw new ConsoleException("Certificate directory is not exist or writeable.");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exportAll()
                    throws ConsoleException {
        synchronized(SYNCHRONIZATION_LOCK) {
            File zipFile = new File(getExportFolder(), ZIPPED_STORE_FILE);

            try(ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
                for(SecureStore secureStore : secureStoreRepository.findAll()) {
                    if(secureStore.getStoreFile() != null) {
                        ZipEntry zipEntry = new ZipEntry(secureStore.getName());
                        zipOutputStream.putNextEntry(zipEntry);
                        zipOutputStream.write(secureStore.getStoreFile());
                        zipOutputStream.flush();
                    }
                }
            } catch (IOException err) {
                throw new ConsoleException("Error encountered while writing to file,", err);
            }

            return ZIPPED_STORE_FILE;
        }
    }

    /**
     * Replace secure store file directly into certificates folder
     * Only replace if the filename matches file exist in certificates folder
     * @param storeFiles Collection of store files to replace
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceStoreFile(List<MultipartFile> storeFiles)
                    throws ConsoleException {
        synchronized(SYNCHRONIZATION_LOCK) {
            File certificateDirectory = getCertificateDirectory();

            try {
                for (MultipartFile file : storeFiles) {
                    File targetStoreFile = new File(certificateDirectory, file.getOriginalFilename());

                    if (targetStoreFile.exists()) {
                        SecureStoreDTO snapshot = getSecureStoreDTO(certificateDirectory, file.getOriginalFilename());

                        Files.write(targetStoreFile.toPath(), file.getBytes());
                        uploadToDatabase(targetStoreFile);

                        SecureStoreDTO secureStoreDTO = getSecureStoreDTO(certificateDirectory, file.getOriginalFilename());
                        
                        entityAuditLogService.addEntityAuditLog(AuditAction.UPDATE,
                                        AuditableEntity.SECURE_STORE.getCode(), -1L,
                                        snapshot.toAuditString(),
                                        secureStoreDTO.toAuditString());
                    } else {
                        LOGGER.warn("Skipping not exists file {}", file.getOriginalFilename());
                    }
                }
            } catch(NoSuchAlgorithmException| IOException e) {
                restoreFromDatabase(certificateDirectory);
                throw new ConsoleException(e.getMessage(), e);
            }
        }
    }

    @Override
    public KeyStore getKeyStore(SecureStoreFile secureStoreFile) {
        StoreAccess storeAccess = getAccessByStoreName(secureStoreFile.getStoreName());
        File secureStore = new File(getCertificateDirectory(), secureStoreFile.getStoreName().concat(".")
                .concat(secureStoreFile.getStoreName().equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension()));
        synchronized(SYNCHRONIZATION_LOCK) {
            if (secureStore.exists()) {
                try (InputStream inputStream = new FileInputStream(secureStore)) {
                    KeyStore keyStore = KeyStore.getInstance(storeAccess.getFileFormat());
                    keyStore.load(inputStream, storeAccess.getPassword().toCharArray());
                    return keyStore;
                } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException err) {
                    LOGGER.error(err.getMessage(), err);
                }
            }
        }
        return null;
    }

    /**
     * List entries of provided store file
     * @param fileName Secure store file name
     * @param filePassword Password to access provided input file
     * @param content Secure store file content
     * @return List of key pair entries in provided secure store file
     * @throws ConsoleException
     */
    @Override
    public List<CertificateDTO> listKey(String fileName, String filePassword, byte[] content)
                    throws ConsoleException {
        List<CertificateDTO> entries = new ArrayList<>();
        File uploadedStoreFile = new File(getCertificateDirectory(), fileName);

        synchronized(SYNCHRONIZATION_LOCK) {
            try {
                FileUtils.writeByteArrayToFile(uploadedStoreFile, content);
                try(InputStream inputStream = new FileInputStream(uploadedStoreFile)) {
                    KeyStore keyStore = KeyStore.getInstance("PKCS12");
                    keyStore.load(inputStream, filePassword.toCharArray());

                    for(Enumeration<String> aliases = keyStore.aliases(); aliases.hasMoreElements(); ) {
                        String alias = aliases.nextElement();

                        Key key = keyStore.getKey(alias, filePassword.toCharArray());
                        if(key instanceof PrivateKey
                            && keyStore.getCertificate(alias) != null) {
                            CertificateDTO certificateDTO = readCertificate(keyStore.getCertificate(alias), false);
                            certificateDTO.setAlias(alias);
                            entries.add(certificateDTO);
                        }
                    }
                } catch(KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException err) {
                    throw new ConsoleException(
                                    msgBundleService.getText("keytool.execution.error.code"),
                                    err.getMessage(), err.getMessage(), err);
                }
            } catch(IOException err) {
                throw new ConsoleException(
                                msgBundleService.getText("keytool.execution.error.code"),
                                err.getMessage(), err.getMessage(), err);
            } finally {
                FileUtils.deleteQuietly(uploadedStoreFile);
            }
        }

        return entries;
    }

    /**
     * Import key pair from PKCS 12 file into key store
     * @param fileName Uploaded PKCS 12 file name
     * @param filePassword Uploaded PKCS 12 file password
     * @param entries Collection of entries to import
     * @param overwrite Flag to indicate overwrite if destination key store contains same alias
     * @param content PKCS 12 file content
     * @param targetStore Destination key store
     * @return
     * @throws ConsoleException
     * @throws NotUniqueException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importKey(String fileName, String filePassword, Map<String, String> entries, boolean overwrite,
                    byte[] content, String targetStore)
        throws ConsoleException {
        StoreAccess storeAccess = getAccessByStoreName(targetStore);
        String secureStoreFileName = targetStore.concat(".")
                        .concat(targetStore.equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());

        File certificateDirectory = getCertificateDirectory();
        File uploadedStoreFile = new File(certificateDirectory, fileName);
        File secureStore = new File(certificateDirectory, secureStoreFileName);

        synchronized (SYNCHRONIZATION_LOCK) {
            try {
                FileUtils.writeByteArrayToFile(uploadedStoreFile, content);

                for (Map.Entry<String, String> aliasEntry : entries.entrySet()) {
                    if (isAliasExist(secureStore, storeAccess, aliasEntry.getValue())) {
                        if (!overwrite) {
                            throw new NotUniqueException(msgBundleService.getText("server.error.not.unique.code"),
                                    msgBundleService.getText("server.error.alias.not.unique", aliasEntry.getValue()));
                        }

                        CertificateDTO certificateDTO = readEntry(secureStore, storeAccess, aliasEntry.getValue());
                        certificateDTO.setStoreName(targetStore);

                        entityAuditLogService.addEntityAuditLog(AuditAction.DELETE,
                                        AuditableEntity.SECURE_STORE.getCode(), -1L,
                                        certificateDTO.toAuditString(),
                                        null);

                        removeAlias(secureStore.getPath(), storeAccess.getPassword(), aliasEntry.getValue());
                    }

                    importKeyPair(uploadedStoreFile.getPath(), filePassword, aliasEntry.getKey(),
                            secureStore.getPath(), storeAccess.getPassword(), aliasEntry.getValue());

                    CertificateDTO keyEntry = readEntry(secureStore, storeAccess, aliasEntry.getValue());
                    keyEntry.setStoreName(targetStore);

                    entityAuditLogService.addEntityAuditLog(AuditAction.CREATE,
                                    AuditableEntity.SECURE_STORE.getCode(), -1L,
                                    null, keyEntry.toAuditString());
                }

                uploadToDatabase(secureStore);
                return true;
            } catch(IOException | NoSuchAlgorithmException err) {
                restoreFromDatabase(certificateDirectory);
                throw new ConsoleException(
                        msgBundleService.getText("keytool.execution.error.code"),
                        err.getMessage(), err.getMessage(), err);
            } finally {
                FileUtils.deleteQuietly(uploadedStoreFile);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importKey(byte[] certificateContent, byte[] privateKeyContent, boolean overwrite, String targetStore,
                             String alias)
            throws ConsoleException {
        StoreAccess storeAccess = getAccessByStoreName(targetStore);
        String secureStoreFileName = targetStore.concat(".").concat(storeAccess.getFileExtension());
        File certificateDirectory = getCertificateDirectory();
        File secureStore = new File(certificateDirectory, secureStoreFileName);
        File tempPkcs12StoreFile = new File(certificateDirectory, "temp-keypair.p12");

        synchronized (SYNCHRONIZATION_LOCK) {
            try {
                if (isAliasExist(secureStore, storeAccess, alias)) {
                    if (!overwrite) {
                        throw new NotUniqueException(msgBundleService.getText("server.error.not.unique.code"),
                                msgBundleService.getText("server.error.alias.not.unique", alias));
                    }

                    CertificateDTO certificateDTO = readEntry(secureStore, storeAccess, alias);
                    certificateDTO.setStoreName(targetStore);

                    entityAuditLogService.addEntityAuditLog(AuditAction.DELETE,
                            AuditableEntity.SECURE_STORE.getCode(), -1L,
                            certificateDTO.toAuditString(),
                            null);

                    removeAlias(secureStore.getPath(), storeAccess.getPassword(), alias);
                }

                try (Reader privateKeyReader = new StringReader(new String(privateKeyContent))) {
                    try (PEMParser privateKeyParser = new PEMParser(privateKeyReader)) {
                        Object keyObject = privateKeyParser.readObject();
                        PrivateKey privateKey;

                        if (keyObject instanceof PEMKeyPair) {
                            privateKey = new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) keyObject).getPrivate();
                        } else if(keyObject instanceof PrivateKeyInfo) {
                            privateKey = new JcaPEMKeyConverter().getPrivateKey(PrivateKeyInfo.getInstance(keyObject));
                        } else {
                            LOGGER.error("Unsupported private key type: " + keyObject.getClass().getName());
                            ConsoleException err = new ConsoleException();
                            err.setStatusCode(msgBundleService.getText("unsupported.private.key.class.code"));
                            err.setStatusMsg(msgBundleService.getText("unsupported.private.key.class.message",
                                    keyObject.getClass().getName()));
                            throw err;
                        }
                        
                        try (Reader certificateReader = new StringReader(new String(certificateContent))) {
                            try (PEMParser certificateParser = new PEMParser(certificateReader)) {
                                KeyStore keyStore = KeyStore.getInstance(storeAccess.getFileFormat());
                                keyStore.load(null);
                                keyStore.setKeyEntry(alias, privateKey, storeAccess.getPassword().toCharArray(),
                                        new Certificate[]{new JcaX509CertificateConverter()
                                                .getCertificate((X509CertificateHolder) certificateParser.readObject())});
                                try (FileOutputStream fileOutputStream = new FileOutputStream(tempPkcs12StoreFile)) {
                                    keyStore.store(fileOutputStream, storeAccess.getPassword().toCharArray());
                                }
                            }
                        }
                    }
                }

                importKeyPair(tempPkcs12StoreFile.getPath(), storeAccess.getPassword(), alias,
                        secureStore.getPath(), storeAccess.getPassword(), alias);

                CertificateDTO keyEntry = readEntry(secureStore, storeAccess, alias);
                keyEntry.setStoreName(targetStore);

                entityAuditLogService.addEntityAuditLog(AuditAction.CREATE, AuditableEntity.SECURE_STORE.getCode(),
                        -1L, null, keyEntry.toAuditString());
                uploadToDatabase(secureStore);
                return true;
            } catch(IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException err) {
                restoreFromDatabase(certificateDirectory);
                throw new ConsoleException(
                        msgBundleService.getText("keytool.execution.error.code"),
                        err.getMessage(), err.getMessage(), err);
            } finally {
                FileUtils.deleteQuietly(tempPkcs12StoreFile);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStoresToDatabase(SecureStoreFile... secureStoreFiles)
                    throws ConsoleException {
        File certificateDirectory = getCertificateDirectory();
        synchronized (SYNCHRONIZATION_LOCK) {
            try {
                for(SecureStoreFile secureStoreFile : secureStoreFiles) {
                    StoreAccess storeAccess = getAccessByStoreName(secureStoreFile.getStoreName());
                    String secureStoreFileName = secureStoreFile.getStoreName().concat(".")
                                    .concat(secureStoreFile.getStoreName().equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension());
                    File secureStore = new File(certificateDirectory, secureStoreFileName);
                    uploadToDatabase(secureStore);
                }
            } catch(IOException | NoSuchAlgorithmException err) {
                restoreFromDatabase(certificateDirectory);
                throw new ConsoleException(
                                msgBundleService.getText("keytool.execution.error.code"),
                                err.getMessage(), err.getMessage(), err);
            }
        }
    }

    private SecureStoreDTO getSecureStoreDTO(File certificateDirectory, String fileName) {
        SecureStoreDTO secureStoreDTO = new SecureStoreDTO();
        SecureStoreFile storeToRead = null;
        for(SecureStoreFile secureStoreFile : SecureStoreFile.values()) {
            if(fileName.startsWith(secureStoreFile.getStoreName())) {
                storeToRead = secureStoreFile;
                break;
            }
        }

        if(storeToRead != null) {
            secureStoreDTO.setStoreName(storeToRead.getStoreName());
            secureStoreDTO.setStoreType(storeToRead.getStoreType().name());

            secureStoreDTO.setCertificateDTO(readEntries(certificateDirectory, true, true, storeToRead));
        }

        return secureStoreDTO;
    }

    private List<CertificateDTO> readEntries(File certificateDirectory, boolean uiManageable, SecureStoreFile... stores) {
        return readEntries(certificateDirectory, false, uiManageable, stores);
    }

    private List<CertificateDTO> readEntries(File certificateDirectory, boolean deepRead, boolean uiManageable, SecureStoreFile... stores) {
        List<CertificateDTO> entries = new ArrayList<>();

        if(stores != null) {
            for (SecureStoreFile secureStoreFile : stores) {
                if (uiManageable && !secureStoreFile.getUIManageable()) {
                    continue;
                }

                StoreAccess storeAccess = getAccessByStoreName(secureStoreFile.getStoreName());
                File secureStore = new File(certificateDirectory, secureStoreFile.getStoreName().concat(".")
                        .concat(secureStoreFile.getStoreName().equals(CACERT_FILE_NAME) ? "jks" : storeAccess.getFileExtension()));
                if (secureStore.exists()) {
                    try (InputStream inputStream = new FileInputStream(secureStore)) {
                        KeyStore keyStore = KeyStore.getInstance(storeAccess.getFileFormat());
                        keyStore.load(inputStream, storeAccess.getPassword().toCharArray());

                        for (Enumeration<String> aliases = keyStore.aliases(); aliases.hasMoreElements(); ) {
                            String alias = aliases.nextElement();

                            CertificateDTO certificateDTO;
                            if (keyStore.entryInstanceOf(alias, KeyStore.SecretKeyEntry.class)) {
                                certificateDTO = readSecretKey(keyStore.getKey(alias, storeAccess.getPassword().toCharArray()));
                            } else {
                                certificateDTO = readCertificate(keyStore.getCertificate(alias), deepRead);
                                if (keyStore.isCertificateEntry(alias)) {
                                    certificateDTO.setType(EntryType.CERTIFICATE.name());
                                } else {
                                    certificateDTO.setType(EntryType.KEY_PAIR.name());
                                }
                            }
                            certificateDTO.setStoreName(secureStoreFile.getStoreName());
                            certificateDTO.setAlias(alias);
                            entries.add(certificateDTO);
                        }
                    } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException |
                             UnrecoverableKeyException err) {
                        LOGGER.error(err.getMessage(), err);
                    }
                }
            }
        }

        return entries;
    }

    private CertificateDTO readEntry(File store, StoreAccess storeAccess, String alias) {
        if(store.exists()) {
            try(InputStream inputStream = new FileInputStream(store)) {
                KeyStore keyStore = KeyStore.getInstance(storeAccess.getFileFormat());
                keyStore.load(inputStream, storeAccess.getPassword().toCharArray());

                if(!keyStore.containsAlias(alias)) {
                    throw new NoDataFoundException(msgBundleService.getText("server.error.alias.not.found.code"),
                            msgBundleService.getText("server.error.alias.not.found",
                                    alias));
                }

                CertificateDTO certificateDTO;
                if(keyStore.entryInstanceOf(alias, KeyStore.SecretKeyEntry.class)) {
                    certificateDTO = readSecretKey(keyStore.getKey(alias, storeAccess.getPassword().toCharArray()));
                } else {
                    certificateDTO = readCertificate(keyStore.getCertificate(alias), true);
                    certificateDTO.setAlias(alias);
                    if(keyStore.isCertificateEntry(alias)) {
                        certificateDTO.setType(EntryType.CERTIFICATE.name());
                    } else {
                        certificateDTO.setType(EntryType.KEY_PAIR.name());
                    }
                }

                return certificateDTO;
            } catch(KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException |
                    UnrecoverableKeyException err) {
                LOGGER.error(err.getMessage(), err);
            }
        }

        return null;
    }

    private CertificateDTO readSecretKey(Key key)
            throws NoSuchAlgorithmException {
        CertificateDTO certificateDTO = new CertificateDTO();
        SecretKey secretKey = (SecretKey)key;
        certificateDTO.setKeyAlgorithmName(secretKey.getAlgorithm());
        certificateDTO.setType(EntryType.SECRET_KEY.name());
        certificateDTO.setEncodedKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        certificateDTO.setKeySize(getKeySize(secretKey));

        return certificateDTO;
    }

    private CertificateDTO readCertificate(Certificate certificate, boolean deepRead)
                    throws CertificateEncodingException, NoSuchAlgorithmException, CertificateParsingException {
        CertificateDTO certificateDTO = new CertificateDTO();

        if(certificate != null) {
            X509Certificate x509Certificate = (X509Certificate) certificate;
            certificateDTO.setSignatureAlgorithmName(x509Certificate.getSigAlgName());
            certificateDTO.setValidFrom(x509Certificate.getNotBefore().getTime());
            certificateDTO.setValidUntil(x509Certificate.getNotAfter().getTime());

            PublicKey publicKey = certificate.getPublicKey();
            certificateDTO.setKeyAlgorithmName(publicKey.getAlgorithm());
            certificateDTO.setKeySize(getKeySize(publicKey));

            if(deepRead) {
                certificateDTO.setVersion(x509Certificate.getVersion());
                certificateDTO.setIssuer(x509Certificate.getIssuerDN().getName());
                certificateDTO.setSubjectDN(x509Certificate.getSubjectDN().getName());
                certificateDTO.setSerialNumber(x509Certificate.getSerialNumber());
                certificateDTO.setType(certificate.getType());
                List<String> subjectAltNames = getSubjectAltNames(x509Certificate);
                if(!subjectAltNames.isEmpty()) {
                    certificateDTO.getNamedExtensions().put("SubjectAlternativeName", String.join(",", subjectAltNames));
                }

                certificateDTO.setThumbprints(getThumbprints(x509Certificate.getEncoded(), Hash.MD5.getName(),
                                Hash.SHA1.getName(), Hash.SHA224.getName(), Hash.SHA256.getName(), Hash.SHA384.getName(), Hash.SHA512.getName()));
            }
        }

        return certificateDTO;
    }

    private List<String> getSubjectAltNames(X509Certificate certificate) {
        List<String> result = new ArrayList<>();
        try {
            Collection<?> subjectAltNames = certificate.getSubjectAlternativeNames();
            if (subjectAltNames == null) {
                return Collections.emptyList();
            }
            for (Object subjectAltName : subjectAltNames) {
                List<?> entry = (List<?>) subjectAltName;
                if (entry == null || entry.size() < 2) {
                    continue;
                }
                Integer altNameType = (Integer) entry.get(0);
                if (altNameType == null) {
                    continue;
                }

                String altType = getSubjectAltType(altNameType);
                if(altNameType == 1 || altNameType == 2 || altNameType == 6
                    || altNameType == 7 || altNameType == 8) {
                    String altName = (String) entry.get(1);
                    if (altName != null) {
                        result.add(altType + ":" + altName);
                    }
                }
            }
            return result;
        } catch (CertificateParsingException e) {
            return Collections.emptyList();
        }
    }

    private String getSubjectAltType(Integer altNameType) {
        switch(altNameType) {
            case 1: return "email";
            case 2: return "dns";
            case 6: return "uri";
            case 7: return "ip";
            case 8: return "oid";
            default: return "others";
        }
    }

    private int getKeySize(Key key) {
        if(key != null) {
            if(key instanceof RSAPublicKey) {
                return ((RSAPublicKey)key).getModulus().bitLength();
            } else if(key instanceof RSAPrivateKey) {
                return ((RSAPrivateKey)key).getModulus().bitLength();
            } else if(key instanceof DSAPublicKey) {
                return ((DSAPublicKey)key).getParams().getP().bitLength();
            } else if(key instanceof DSAPrivateKey) {
                return ((DSAPrivateKey)key).getParams().getP().bitLength();
            } else if(key instanceof ECPublicKey) {
                ECPublicKey ecPublicKey = ((ECPublicKey)key);
                return Math.max(ecPublicKey.getW().getAffineX().bitLength(), ecPublicKey.getW().getAffineY().bitLength());
            } else if (key instanceof ECPrivateKey) {
                return ((ECPrivateKey)key).getS().bitLength();
            } else if (key instanceof SecretKey) {
                return key.getEncoded().length * Byte.SIZE;
            }
        }

        return -1;
    }

    private Map<String, String> getThumbprints(byte[] encodedThumbprint, String... algorithms)
                    throws NoSuchAlgorithmException {
        Map<String, String> thumbprints = new LinkedHashMap<>();

        for(String algorithm : algorithms) {
            StringBuilder thumbprint = new StringBuilder();
            String rawValue = BaseEncoding.base16().encode(MessageDigest.getInstance(algorithm).digest(encodedThumbprint));

            for (int i = 0; i < rawValue.length(); i++) {
                if (i > 0 && i % 2 == 0) {
                    thumbprint.append(':');
                }

                thumbprint.append(rawValue.charAt(i));
            }

            thumbprints.put(algorithm, thumbprint.toString());
        }

        return thumbprints;
    }

    private void changePassword(StoreAccess storeAccess, File[] secureStores, String password)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        for(File secureStore : secureStores) {
            SecureStoreFile secureStoreFile = SecureStoreFile.getStoreFileByName(secureStore.getName().substring(0, secureStore.getName().lastIndexOf(".")));
            try (InputStream inputStream = new FileInputStream(secureStore)) {
                KeyStore keyStore = KeyStore.getInstance(secureStoreFile.getStoreFormat());
                keyStore.load(inputStream, storeAccess.getPassword().toCharArray());

                List<String> arguments = new ArrayList<>();
                arguments.add("-storepasswd");
                arguments.add("-keystore");
                arguments.add(secureStore.getPath());
                arguments.add("-storepass");
                arguments.add(escapeQuotes(storeAccess.getPassword()));
                arguments.add("-new");
                arguments.add(escapeQuotes(password));

                executeKeytool(arguments);
            }
        }

        SysConfigValueDTO sysConfigValueDTO = new SysConfigValueDTO();
        sysConfigValueDTO.setApplication(storeAccess.getPasswordConfigEntry().getScope().getCode());
        sysConfigValueDTO.setConfigKey(storeAccess.getPasswordConfigEntry().getKey());
        sysConfigValueDTO.setValue(password);

        List<SysConfigValueDTO> sysConfigValueDTOS = (new ArrayList<>());
        sysConfigValueDTOS.add(sysConfigValueDTO);
        sysConfigService.sendConfigRefreshRequest(sysConfigService.updateValue(sysConfigValueDTOS, false));
    }

    private void importKeyPair(String srcKeystore, String srcPassword, String srcAlias,
                               String destKeystore, String destPassword, String destAlias)
            throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add("-importkeystore");
        arguments.add("-noprompt");
        arguments.add("-srckeystore");
        arguments.add(srcKeystore);
        arguments.add("-srcstoretype");
        arguments.add("PKCS12");
        arguments.add("-srcstorepass");
        arguments.add(escapeQuotes(srcPassword));
        arguments.add("-srckeypass");
        arguments.add(escapeQuotes(srcPassword));
        arguments.add("-srcalias");
        arguments.add(srcAlias);
        arguments.add("-destkeystore");
        arguments.add(destKeystore);
        arguments.add("-deststorepass");
        arguments.add(escapeQuotes(destPassword));
        arguments.add("-destkeypass");
        arguments.add(escapeQuotes(destPassword));
        arguments.add("-deststoretype");
        arguments.add(keyStoreProperties.getType());
        arguments.add("-destalias");
        arguments.add(destAlias);

        executeKeytool(arguments);
    }

    private StoreAccess getAccessByStoreType(String storeType) {
        StoreAccess storeAccess = new StoreAccess();

        if (SecureStoreType.KEYSTORE.name().equalsIgnoreCase(storeType)) {
            storeAccess.setFileExtension(keyStoreProperties.getType().equalsIgnoreCase("JKS") ? "jks" : "p12");
            storeAccess.setPassword(keyStoreProperties.getPassword());
            storeAccess.setPasswordConfigEntry(SysConfigEntry.KEY_STORE_PASSWORD);
        } else if (SecureStoreType.TRUSTSTORE.name().equalsIgnoreCase(storeType)) {
            storeAccess.setFileExtension(trustStoreProperties.getType().equalsIgnoreCase("JKS") ? "jks" : "p12");
            storeAccess.setPassword(trustStoreProperties.getPassword());
            storeAccess.setPasswordConfigEntry(SysConfigEntry.TRUST_STORE_PASSWORD);
        }

        return storeAccess;
    }

    private StoreAccess getAccessByStoreName(String storeName) {
        StoreAccess storeAccess = new StoreAccess();
        SecureStoreFile storeFile = SecureStoreFile.getStoreFileByName(storeName);
        storeAccess.setFileFormat(storeFile.getStoreFormat());

        if(SecureStoreType.KEYSTORE.equals(storeFile.getPasswordType())) {
            storeAccess.setFileExtension(keyStoreProperties.getType().equalsIgnoreCase("JKS") ? "jks" : "p12");
            storeAccess.setPassword(keyStoreProperties.getPassword());
            storeAccess.setPasswordConfigEntry(SysConfigEntry.KEY_STORE_PASSWORD);
        } else if(SecureStoreType.TRUSTSTORE.equals(storeFile.getPasswordType())) {
            storeAccess.setFileExtension(trustStoreProperties.getType().equalsIgnoreCase("JKS") ? "jks" : "p12");
            storeAccess.setPassword(trustStoreProperties.getPassword());
            storeAccess.setPasswordConfigEntry(SysConfigEntry.TRUST_STORE_PASSWORD);
        }

        return storeAccess;
    }

    private boolean isAliasExist(File store, StoreAccess storeAccess, String alias) {
        if(store.exists()) {
            try(InputStream inputStream = new FileInputStream(store)) {
                KeyStore keyStore = KeyStore.getInstance(storeAccess.getFileFormat());
                keyStore.load(inputStream, storeAccess.getPassword().toCharArray());

                return keyStore.containsAlias(alias);
            } catch(KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException err) {
                LOGGER.error(err.getMessage(), err);
            }
        }

        return false;
    }

    private void removeAlias(String storePath, String storePassword, String alias)
                    throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add("-delete");
        arguments.add("-noprompt");
        arguments.add("-keystore");
        arguments.add(storePath);
        arguments.add("-storepass");
        arguments.add(escapeQuotes(storePassword));
        arguments.add("-alias");
        arguments.add(alias);

        executeKeytool(arguments);
    }

    /**
     * Upload secure store to database
     * @param secureStores Collection of secure store file name to be updated
     * @throws IOException File not found
     * @throws NoSuchAlgorithmException Hashing algorithm not found
     */
    private void uploadToDatabase(File... secureStores)
                    throws IOException, NoSuchAlgorithmException {
        for(File secureStore : secureStores) {
            Optional<SecureStore> secureStoreOptional = secureStoreRepository.findByName(secureStore.getName());

            if(secureStoreOptional.isPresent()) {
                SecureStore databaseRecord = secureStoreOptional.get();
                String secureStoreFileChecksum = bytesToHex(getChecksum(secureStore, databaseRecord.getHashAlgorithm()));
                if(!databaseRecord.getChecksum().equals(secureStoreFileChecksum)) {
                    try(InputStream fileInputStream = new FileInputStream(secureStore)) {
                        databaseRecord.setStoreFile(IOUtils.toByteArray(fileInputStream));
                    }
                    databaseRecord.setChecksum(secureStoreFileChecksum);
                    databaseRecord.setModifiedOn(new Date());

                    secureStoreRepository.save(databaseRecord);
                }
            } else {
                SecureStore newEntry = new SecureStore();
                newEntry.setName(secureStore.getName());
                String secureStoreFileChecksum = bytesToHex(getChecksum(secureStore, "SHA1"));
                try (InputStream fileInputStream = new FileInputStream(secureStore)) {
                    newEntry.setStoreFile(IOUtils.toByteArray(fileInputStream));
                }
                newEntry.setHashAlgorithm("SHA1");
                newEntry.setChecksum(secureStoreFileChecksum);
                newEntry.setModifiedOn(new Date());

                secureStoreRepository.save(newEntry);
            }
        }
    }

    private void restoreFromDatabase(File certificateDirectory) {
        List<SecureStore> secureStores = secureStoreRepository.findAll();

        for(SecureStore secureStore : secureStores) {
            if(secureStore.getStoreFile() != null) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(
                                new File(certificateDirectory, secureStore.getName()))) {
                    fileOutputStream.write(secureStore.getStoreFile());
                    fileOutputStream.flush();
                } catch (IOException err) {
                    LOGGER.error(err.getMessage(), err);
                }
            }
        }
    }

    private void executeKeytool(List<String> arguments)
                    throws IOException {
        ByteArrayOutputStream executionOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(
                        Paths.get(controlCenterHome, "java", "jre", "bin", "keytool").toString());
        arguments.forEach(argument -> commandLine.addArgument(argument, false));
        if(LOGGER.isTraceEnabled())
            LOGGER.trace("Running command: {}", Arrays.toString(commandLine.toStrings()));

        ExecuteWatchdog watchdog = new ExecuteWatchdog(15000);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setWatchdog(watchdog);
        defaultExecutor.setStreamHandler(new PumpStreamHandler(executionOutputStream, errorOutputStream));
        int exitCode = -1;

        try {
            exitCode = defaultExecutor.execute(commandLine);
        } catch (IOException e) {
            throw new IOException(executionOutputStream.toString(), e);
        } finally {
            String exitErrorMessage = errorOutputStream.toString();

            if (exitCode != 0
                && StringUtils.isNotEmpty(exitErrorMessage)) {
                if(exitErrorMessage.indexOf("\nWarning:") == -1) {
                    LOGGER.error(exitErrorMessage);
                } else {
                    LOGGER.warn(exitErrorMessage);
                }
            }
        }
    }

    /**
     * Resolve certificate file object
     * @return Directory of certificates stored
     */
    private File getCertificateDirectory() {
        return new File(String.join(File.separator, controlCenterHome, "server", "certificates"));
    }

    private File getExportFolder() {
        File exportFolder = new File(configurationDataLoader.getPolicyExportsFileLocation().concat(File.separator).concat(EXPORT_FOLDER));
        if (!exportFolder.exists()) {
            exportFolder.mkdirs();
        }

        return exportFolder;
    }

    /**
     * Calculate hash value of input file
     * @param fileName Name of the file
     * @param hashAlgorithm Hashing algorithm name to be used
     * @return hash value in bytes
     * @throws IOException Input file is not found
     * @throws NoSuchAlgorithmException Invalid hashing algorithm name
     */
    private byte[] getChecksum(File fileName, String hashAlgorithm)
                    throws IOException, NoSuchAlgorithmException {
        return Hash.valueOf(hashAlgorithm).checksum(fileName);
    }

    /**
     * Convert bytes into hexadecimal expression.
     * @param hashInBytes hash value
     * @return hash in hexadecimal expression
     */
    private String bytesToHex(byte[] hashInBytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for(byte b : hashInBytes) {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString().toUpperCase();
    }

    /**
     * Convert start date to ([+-]nnn(ymdHMS])+ format.
     * This is to avoid Java bug in yyyy/MM/dd HH:mm:ss format.
     * @param datetime
     *          The value of start date
     * @return Adjustment comparison string of provided start date against current time
     */
    private String calculateStartDate(long datetime) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(datetime);

        StringBuilder startDateValue = new StringBuilder();

        int yearDifference = target.get(Calendar.YEAR) - now.get(Calendar.YEAR);
        int monthDifference = target.get(Calendar.MONTH) - now.get(Calendar.MONTH);
        int dayDifference = target.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH);
        int hourDifference = target.get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY);
        int minuteDifference = target.get(Calendar.MINUTE) - now.get(Calendar.MINUTE);
        int secondDifference = target.get(Calendar.SECOND) - now.get(Calendar.SECOND);

        if(yearDifference != 0)
            startDateValue.append(yearDifference > 0 ? '+' : '-').append(Math.abs(yearDifference)).append('y');

        if(monthDifference != 0)
            startDateValue.append(monthDifference > 0 ? '+' : '-').append(Math.abs(monthDifference)).append('m');

        if(dayDifference != 0)
            startDateValue.append(dayDifference > 0 ? '+' : '-').append(Math.abs(dayDifference)).append('d');

        if(hourDifference != 0)
            startDateValue.append(hourDifference > 0 ? '+' : '-').append(Math.abs(hourDifference)).append('H');

        if(minuteDifference != 0)
            startDateValue.append(minuteDifference > 0 ? '+' : '-').append(Math.abs(minuteDifference)).append('M');

        if(secondDifference != 0)
            startDateValue.append(minuteDifference > 0 ? '+' : '-').append(Math.abs(secondDifference)).append('S');

        return startDateValue.length() > 0 ? startDateValue.toString() : "+0S";
    }

    private String escapeQuotes(final String password) {
        if(SystemUtils.IS_OS_WINDOWS) {
            return password.replaceAll("\"", "\\\\\"");
        }

        return password;
    }
}

/**
 * Helper class to assist in passing store information around
 */
class StoreAccess {

    private String password;
    private String fileFormat;
    private String fileExtension;
    private SysConfigEntry passwordConfigEntry;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileExtension() {
        return fileExtension.toLowerCase();
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public SysConfigEntry getPasswordConfigEntry() {
        return passwordConfigEntry;
    }

    public void setPasswordConfigEntry(SysConfigEntry passwordConfigEntry) {
        this.passwordConfigEntry = passwordConfigEntry;
    }
}
