package com.nextlabs.destiny.cc.installer.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.config.properties.CertificateProperties;
import com.nextlabs.destiny.cc.installer.config.properties.KeyPairProperties;
import com.nextlabs.destiny.cc.installer.config.properties.Version;
import com.nextlabs.destiny.cc.installer.dto.SecureStore;
import com.nextlabs.destiny.cc.installer.enums.Hash;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.helpers.DbHelper;
import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.services.CertificateManagementService;
import com.nextlabs.destiny.cc.installer.services.ProgressService;

/**
 * Service implementation for certificate management.
 *
 * @author Sachindra Dasun
 */
@Service
public class CertificateManagementServiceImpl implements CertificateManagementService {

    protected static final String[] DEFAULT_CERTIFICATES = {"enrollment.cer", "keymanagement.cer", "orig_temp_agent.cer",
            "policyAuthor.cer", "temp_agent.cer"};
    private static final String DN_FORMAT = "CN=CompliantEnterprise Server, OU=CompliantEnterprise, O=NextLabs, L=San Mateo, ST=CA, C=US";
    private static final String DN_FORMAT_CC = "CN=%s, OU=CompliantEnterprise, O=NextLabs, L=San Mateo, ST=CA, C=US";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String KEY_ALGORITHM_LEGACY = "DSA";
    private static final int KEY_SIZE = 2048;
    private static final int KEY_SIZE_LEGACY = 1024;
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String SIGNATURE_ALGORITHM_LEGACY = "SHA1withDSA";
    private static final int VALIDITY = 3650;
    private static final Logger logger = LoggerFactory.getLogger(CertificateManagementServiceImpl.class);
    private CcProperties ccProperties;
    private ProgressService progressService;

    private static final String SQL_GET_HASH_ALGORITHM = "SELECT HASH_ALGORITHM FROM SECURE_STORE WHERE NAME = ?";
    private static final String SQL_UPDATE_KEYSTORE_FILE = "UPDATE SECURE_STORE SET STORE_FILE = ?, CHECKSUM = ?, MODIFIED_ON = ? WHERE NAME = ?";

    public CertificateManagementServiceImpl(CcProperties ccProperties, ProgressService progressService) {
        this.ccProperties = ccProperties;
        this.progressService = progressService;
    }

    /**
     * Create installer certificate and import keys to keystore and truststore.
     *
     * @throws IOException if error occurred
     */
    public void createInstallerCertificate() throws IOException {
        Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "certificates", "installer-keystore.p12"));
        Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "certificates", "installer-truststore.p12"));
        Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "certificates", "installer.cer"));
        generateKeyPair("installer", String.format(DN_FORMAT_CC, ccProperties.getDnsName()),
                "installer-keystore.p12", ccProperties.getSsl().getInstallerKeystore().getPassword(),
                false);
        exportCertificate("installer", "installer-keystore.p12",
                ccProperties.getSsl().getInstallerKeystore().getPassword(), "installer.cer", false);
        importCertificate("installer", "installer-truststore.p12"
                , ccProperties.getSsl().getInstallerTruststore().getPassword(), "installer.cer");
    }

    /**
     * Import certificate into the given truststore.
     *
     * @param alias               identifier for the entry inside the truststore
     * @param trustStoreFileName  truststore file name
     * @param password            password for the SSL certificate store
     * @param certificateFileName certificate file name
     * @throws IOException if error occurred
     */
    public void importCertificate(String alias, String trustStoreFileName, String password,
                                  String certificateFileName) throws IOException {
        Path trustStoreFilePath = Paths.get(ccProperties.getHome(), "server", "certificates", trustStoreFileName);
        Path certificateFilePath = Paths.get(ccProperties.getHome(), "server", "certificates", certificateFileName);
        if (certificateFilePath.toFile().exists() && !trustStoreFilePath.toFile().exists() ||
                !aliasFound(trustStoreFilePath, alias, password)) {
            List<String> arguments = new ArrayList<>();
            arguments.add("-importcert");
            arguments.add("-v");
            arguments.add("-noprompt");
            arguments.add("-alias");
            arguments.add(alias);
            arguments.add("-file");
            arguments.add(certificateFilePath.toString());
            arguments.add("-keystore");
            arguments.add(trustStoreFilePath.toString());
            arguments.add("-storepass");
            arguments.add(escapeQuotes(password));
            keyTool(arguments);
            logger.debug("Certificate imported: {}, {}, {}", alias, trustStoreFileName, certificateFileName);
        }
    }

    /**
     * Create SSL certificates required for the Control Center.
     *
     * @throws IOException if error occurred
     */
    public void createCertificates() throws IOException, CertificateException, NoSuchAlgorithmException,
            KeyStoreException {
        if (ccProperties.getRunningMode() == RunningMode.UPGRADE &&
                ccProperties.getVersion().equals(ccProperties.getPreviousVersion())) {
            return;
        }
        Path certificatesDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates");
        Files.createDirectories(certificatesDirectoryPath.resolve("cacerts"));
        Files.createDirectories(certificatesDirectoryPath.resolve("override"));
        boolean copyCacertsToOverrideFolder = false;
        if (ccProperties.isManagementServerInstance()) {
            if (ccProperties.getRunningMode() == RunningMode.UPGRADE) {
                logger.debug("Executing upgrade procedure for previous installation");
                if(ccProperties.getPreviousVersion().before(Version.V_2020_04)) {
                    FileUtils.copyDirectory(Paths.get(ccProperties.getPreviousHome(), "server",
                                    "certificates").toFile(), certificatesDirectoryPath.toFile());
                    if (ccProperties.getPreviousVersion().before(Version.V_8_7_0_0)) {
                        logger.debug("Upgrading installation instance prior to 8.7 release");
                        createDigitalSignatureCertificateStores();
                    }
                    logger.debug("Upgrading installation instance prior to 2020.04 release");
                    createControlCenterCertificate("dcc-keystore.jks", "dcc-truststore.jks");
                    migrateCertificates();
                    //copyStores();
                    copyCacertsToOverrideFolder = true;
                }
            } else if (!hasCertificatesInDb() && noCertificatesInOverrideDirectory()) {
                logger.debug("Executing clean installation procedure for current version");
                deleteWebCertificate();
                createDccCertificateStores();
                createLegacyDccCertificate();
                createControlCenterCertificate("dcc-keystore.p12", "dcc-truststore.p12");
                createAgentCertificateStores();
                createLegacyAgentCertificateStores();
                createApplicationCertificateStores();
                createWebCertificateStores();
                createDigitalSignatureCertificateStores();
                createTruststores();
                copyStores();
                copyCacertsToOverrideFolder = true;
            }
            convertJksToPkcs12();
            logger.info("Completed configuring SSL");
        }
        createCacerts(ccProperties, copyCacertsToOverrideFolder ? ccProperties.getSsl().getTruststore().getPassword() :
                        ccProperties.getSsl().getInstallerTruststore().getPassword(),
                copyCacertsToOverrideFolder);
    }

    /**
     * Delete certificate the given truststore.
     *
     * @param alias              identifier for the entry inside the truststore
     * @param trustStoreFileName truststore file name
     * @throws IOException if error occurred
     */
    public void deleteCertificate(String alias, String trustStoreFileName, String password) throws IOException {
        Path storeFilePath = Paths.get(ccProperties.getHome(), "server", "certificates",
                trustStoreFileName);
        if (storeFilePath.toFile().exists()
                && aliasFound(storeFilePath, alias, password)) {
            List<String> arguments = new ArrayList<>();
            arguments.add("-delete");
            arguments.add("-alias");
            arguments.add(alias);
            arguments.add("-keystore");
            arguments.add(storeFilePath.toString());
            arguments.add("-storepass");
            arguments.add(escapeQuotes(password));
            keyTool(arguments);
            logger.info("Certificate deleted: {}, {}", alias, trustStoreFileName);
        }
    }

    private boolean aliasFound(Path storeFilePath, String alias, String password) {
        if (storeFilePath.toFile().exists()) {
            List<String> arguments = new ArrayList<>();
            arguments.add("-list");
            arguments.add("-keystore");
            arguments.add(storeFilePath.toString());
            arguments.add("-storepass");
            arguments.add(escapeQuotes(password));
            arguments.add("-alias");
            arguments.add(alias);
            try {
                keyTool(arguments);
                return true;
            } catch (Exception e) {
                logger.info("Alias {} not found", alias);
            }
        }
        return false;
    }

    private void keyTool(List<String> arguments) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(Paths.get(ccProperties.getHome(), "java", "jre", "bin", "keytool").toString());
        arguments.forEach(argument -> commandLine.addArgument(argument, false));
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } finally {
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output) && !output.contains("The JKS keystore uses a proprietary format.")
                    && !output.contains("keytool error: java.lang.Exception: Alias <")) {
                logger.info(output);
            }
        }
    }

    public boolean createCacerts(CcProperties ccProperties, String password, boolean copyToOverrideFolder) throws IOException {
        Path certificatesDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates");
        Path cacertsFilePath = certificatesDirectoryPath.resolve(CcProperties.CACERTS_FILE);
        createCacertsLink(cacertsFilePath);
        if (cacertsFilePath.toFile().exists()) {
            return false;
        }
        Path cacertsDirectoryPath = certificatesDirectoryPath.resolve("cacerts");
        Files.createDirectories(cacertsDirectoryPath);
        String cacertsPasswordFromDb = getCacertsFileFromDb(cacertsFilePath);
        if (ccProperties.getRunningMode() == RunningMode.UPGRADE) {
            String cacertsFileName = ccProperties.getPreviousVersion().before(Version.V_2020_04) ? "web-truststore.jks"
                    : CcProperties.CACERTS_FILE.toString();
            Path previousCaCertsPath = Paths.get(ccProperties.getPreviousHome(), "server", "certificates",
                    cacertsFileName);
            if (previousCaCertsPath.toFile().exists()) {
                Files.copy(previousCaCertsPath, cacertsFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        for (CertificateProperties certificateProperties : ccProperties.getSsl().getCaCerts()) {
            if (StringUtils.isNotEmpty(certificateProperties.getContent())
                    && StringUtils.isNotEmpty(certificateProperties.getAlias())) {
                Files.write(cacertsDirectoryPath.resolve(String.format("%s.cer", certificateProperties.getAlias())),
                        Base64.getDecoder().decode(certificateProperties.getContent()
                                .replaceAll("[\\r\\n]", "").trim()));
            } else if (StringUtils.isNotEmpty(certificateProperties.getPath())) {
                File certificateFile = Paths.get(certificateProperties.getPath()).toFile();
                if (certificateFile.exists()) {
                    FileUtils.copyFileToDirectory(certificateFile, cacertsDirectoryPath.toFile());
                }
            }
        }
        try (Stream<Path> pathStream = Files.walk(cacertsDirectoryPath)) {
            pathStream.filter(path -> path.toFile().isFile() && !path.toString().endsWith(".p12"))
                    .forEach(path -> {
                        String certificateFileName = path.getFileName().toString();
                        try {
                            importCertificate(FilenameUtils.removeExtension(certificateFileName),
                                    CcProperties.CACERTS_FILE.toString(),
                                    StringUtils.isEmpty(cacertsPasswordFromDb) ? password
                                            : EncryptionHelper.decryptIfEncrypted(cacertsPasswordFromDb),
                                    Paths.get("cacerts", certificateFileName).toString());
                        } catch (IOException e) {
                            logger.error("Error in importing certificate", e);
                        }
                        if ("web.cer".equalsIgnoreCase(certificateFileName) ||
                                "dcc.cer".equalsIgnoreCase(certificateFileName)) {
                            FileUtils.deleteQuietly(path.toFile());
                        }
                    });
        }
        if (ccProperties.getRunningMode() == RunningMode.INSTALLATION) {
            convertKeyStoreType(cacertsFilePath, cacertsFilePath, "PKCS12", "JKS",
                    StringUtils.isEmpty(cacertsPasswordFromDb) ? password
                            : EncryptionHelper.decryptIfEncrypted(cacertsPasswordFromDb));
        }
        if (copyToOverrideFolder && cacertsFilePath.toFile().exists() && ccProperties.isManagementServerInstance()
                && (ccProperties.getRunningMode() == RunningMode.INSTALLATION ||
                (ccProperties.getRunningMode() == RunningMode.UPGRADE &&
                        ccProperties.getPreviousVersion().before(Version.V_2020_04)))) {
            Path overrideDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates", "override");
            Files.deleteIfExists(overrideDirectoryPath.resolve(CcProperties.CACERTS_FILE.toString()));
            FileUtils.copyFileToDirectory(cacertsFilePath.toFile(), overrideDirectoryPath.toFile());
        }
        return true;
    }

    private void migrateCertificates() throws IOException {
        /*
         * For the backward compatibility, the legacy-agent-truststore-kp.p12 is required with the keystore password.
         */
        Path certificatesDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates");
        if (ccProperties.getPreviousVersion().before(Version.V_9_0_0_0)) {
            File agentTruststoreFile = certificatesDirectoryPath.resolve("agent-truststore.jks").toFile();
            if (agentTruststoreFile.exists()) {
                FileUtils.copyFile(agentTruststoreFile,
                        certificatesDirectoryPath.resolve("legacy-agent-truststore-kp.jks").toFile());
            }
        }
        exportCertificate("dcc", "dcc-keystore.jks",
                ccProperties.getSsl().getKeystore().getPassword(), "dcc.cer", false);
        File dccCertificate = certificatesDirectoryPath.resolve("dcc.cer").toFile();
        if (dccCertificate.exists()) {
            FileUtils.copyFileToDirectory(dccCertificate, certificatesDirectoryPath.resolve("cacerts").toFile());
        }
    }

    private boolean noCertificatesInOverrideDirectory() throws IOException {
        Path overrideDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates", "override");
        if (!overrideDirectoryPath.toFile().exists()) {
            return true;
        }
        try (Stream<Path> pathStream = Files.list(overrideDirectoryPath)) {
            return pathStream.count() == 0;
        }
    }

    /**
     * Create web certificate and import keys to keystore and truststore.
     *
     * @throws IOException if error occurred
     */
    public void createWebCertificateStores() throws IOException, CertificateException, NoSuchAlgorithmException,
            KeyStoreException {
        progressService.setCurrentTask(Task.CREATE_WEB_CERTIFICATE);
        Path certificatesDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates");
        KeyPairProperties webKeyPairStore = ccProperties.getSsl().getWebKeyPairStore();
        if (StringUtils.isEmpty(webKeyPairStore.getContent())
                && StringUtils.isNotEmpty(webKeyPairStore.getKeyContent())
                && StringUtils.isNotEmpty(webKeyPairStore.getCertificateContent())) {
            createKeyStoreContent(webKeyPairStore);
        }
        if (StringUtils.isEmpty(webKeyPairStore.getContent())) {
            generateKeyPair("web", String.format(DN_FORMAT_CC,
                    StringUtils.isEmpty(ccProperties.getSsl().getWebCertCn()) ? ccProperties.getDnsName() :
                            ccProperties.getSsl().getWebCertCn()), "web-keystore.p12",
                    ccProperties.getSsl().getKeystore().getPassword(), false);
        } else {
            Path importKeyStorePath = certificatesDirectoryPath.resolve("import-web-keystore");
            Files.write(importKeyStorePath, Base64.getDecoder().decode(webKeyPairStore.getContent()
                    .replaceAll("[\\r\\n]", "").trim()));
            importKeyStore(importKeyStorePath, certificatesDirectoryPath.resolve("web-keystore.p12"),
                    webKeyPairStore.getType(),
                    "PKCS12", webKeyPairStore.getPassword(),
                    ccProperties.getSsl().getKeystore().getPassword(), webKeyPairStore.getKeyAlias(),
                    "web", webKeyPairStore.getKeyPassword());
            Files.deleteIfExists(importKeyStorePath);
        }
        exportCertificate("web", "web-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "web.cer", false);
        importCertificate("web", "web-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "web.cer");
        FileUtils.copyFileToDirectory(certificatesDirectoryPath.resolve("web.cer").toFile(),
                certificatesDirectoryPath.resolve("cacerts").toFile());
    }

    private void createCacertsLink(Path cacertsFilePath) throws IOException {
        Path cacertsLinkPath = Paths.get(ccProperties.getHome(), "java", "jre", "lib", "security", "cacerts");
        Files.deleteIfExists(cacertsLinkPath);
        Files.createSymbolicLink(cacertsLinkPath, cacertsFilePath);
        if (!Paths.get(ParameterHelper.INIT_CC_HOME).equals(Paths.get(ccProperties.getHome()))) {
            Path installerCacertsLinkPath = Paths.get(ParameterHelper.INIT_CC_HOME, "java", "jre",
                    "lib", "security", "cacerts");
            Files.deleteIfExists(installerCacertsLinkPath);
            Files.createSymbolicLink(installerCacertsLinkPath, cacertsFilePath);
        }
    }

    private boolean hasCertificatesInDb() {
        Integer count = null;
        try {
            count = DbHelper.getJdbcTemplate().queryForObject(
                    "SELECT COUNT(*) FROM SECURE_STORE WHERE CHECKSUM <> '00'", Integer.class);
        } catch (Exception e) {
            logger.debug("No certificates found", e);
        }
        return count != null && count > 0;
    }

    private String getCacertsFileFromDb(Path cacertsFilePath) throws IOException {
        if (hasCertificatesInDb()) {
            JdbcTemplate jdbcTemplate = DbHelper.getJdbcTemplate();
            if(jdbcTemplate != null) {
                byte[] cacertsFileContent = DbHelper.getJdbcTemplate().queryForObject(
                        "SELECT STORE_FILE FROM SECURE_STORE WHERE name = 'cacerts.jks'",
                        (rs, rowNum) -> rs.getBytes(1));
                if (cacertsFileContent != null) {
                    Files.write(cacertsFilePath, cacertsFileContent);
                }
                return DbHelper.getJdbcTemplate().queryForObject(
                        "SELECT VALUE FROM SYS_CONFIG WHERE CONFIG_KEY='trust.store.password' AND APPLICATION='application'",
                        String.class);
            }
        }
        return null;
    }

    private void copyStores() throws IOException {
        File overrideDirectory = Paths.get(ccProperties.getHome(), "server", "certificates", "override").toFile();
        FileUtils.cleanDirectory(overrideDirectory);
        FileUtils.copyDirectory(Paths.get(ccProperties.getHome(), "server", "certificates").toFile(),
                overrideDirectory,
                file -> file.getName().endsWith(".p12") && !file.getName().startsWith("installer-"));
    }

    private String getStorePassword(String key) {
        return EncryptionHelper.decryptIfEncrypted(DbHelper
                        .getJdbcTemplate()
                        .queryForObject("SELECT VALUE FROM SYS_CONFIG WHERE CONFIG_KEY=? AND APPLICATION='application'", String.class, key));
    }

    private void convertJksToPkcs12() throws IOException {
        Path certificatesDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates");

        JdbcTemplate jdbcTemplate = DbHelper.getJdbcTemplate();
        try {
            if (jdbcTemplate != null) {
                List<SecureStore> secureStores = jdbcTemplate.query(
                                "SELECT ID, NAME, STORE_FILE FROM SECURE_STORE WHERE NAME != 'cacerts.jks' AND NAME LIKE '%.jks' AND STORE_FILE IS NOT NULL",
                                (rs, rowNum) -> new SecureStore(rs.getString("NAME"),
                                                rs.getBytes("STORE_FILE")));

                for (SecureStore secureStore : secureStores) {
                    File secureStoreFile = certificatesDirectoryPath.resolve(secureStore.getName()).toFile();
                    FileUtils.writeByteArrayToFile(secureStoreFile, secureStore.getStoreFile());
                    logger.debug("Downloaded secure store {} from database", secureStore.getName());
                }
            }

            File[] storeFiles = certificatesDirectoryPath.toFile().listFiles((dir, name) -> name.endsWith(".jks"));
            logger.debug("{} JKS certificate file(s) found", storeFiles.length);

            String keyStorePassword = StringUtils.isNotEmpty(
                            ccProperties.getSsl().getKeystore().getPassword()) ?
                            ccProperties.getSsl().getKeystore().getPassword() : getStorePassword("key.store.password");
            String trustStorePassword = StringUtils.isNotEmpty(
                            ccProperties.getSsl().getTruststore().getPassword()) ?
                            ccProperties.getSsl().getTruststore().getPassword() : getStorePassword("trust.store.password");

            for (File storeFile : storeFiles) {
                if (!storeFile.isFile())
                    continue;

                // JRE only supports cacerts in JKS format
                if (!(storeFile.getName().equals(CcProperties.CACERTS_FILE.toString()) || storeFile.getName().startsWith("installer-"))) {
                    if(storeFile.getName().equals(CcProperties.FPE_KEYSTORE_FILE.toString())) {
                        convertKeyStoreType(storeFile.toPath(),
                                Paths.get(ccProperties.getHome(), "server", "certificates",
                                        "override",
                                        storeFile.getName().replace(".jks", ".p12")),
                                "JCEKS", "PKCS12",
                                (storeFile.getName().startsWith("legacy-") || storeFile.getName().endsWith("-keystore.jks")) ?
                                        keyStorePassword : trustStorePassword);
                    } else {
                        convertKeyStoreType(storeFile.toPath(),
                                Paths.get(ccProperties.getHome(), "server", "certificates",
                                        "override",
                                        storeFile.getName().replace(".jks", ".p12")),
                                "JKS", "PKCS12",
                                (storeFile.getName().startsWith("legacy-") || storeFile.getName().endsWith("-keystore.jks")) ?
                                        keyStorePassword : trustStorePassword);
                    }
                }
                FileUtils.deleteQuietly(storeFile);
            }
        } catch (BadSqlGrammarException sqlerr) {
            // Ignore, this happens during installation on blank database
        }
    }

    private void deleteWebCertificate() throws IOException {
        Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "certificates", "web-keystore.p12"));
        Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "certificates", "web-truststore.p12"));
        Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "certificates", "web.cer"));
    }

    private void createDccCertificateStores() throws IOException {
        progressService.setCurrentTask(Task.CREATE_DCC_CERTIFICATE);
        Path certificatesDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates");
        generateKeyPair("dcc", DN_FORMAT, "dcc-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), false);
        exportCertificate("dcc", "dcc-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "dcc.cer", false);
        importCertificate("dcc", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "dcc.cer");
        FileUtils.copyFileToDirectory(certificatesDirectoryPath.resolve("dcc.cer").toFile(),
                certificatesDirectoryPath.resolve("cacerts").toFile());
    }

    private void createControlCenterCertificate(String dccKeyStoreFileName, String dccTrustStoreFileName)
                    throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        Path certificatesDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates");
        KeyPairProperties ccKeyPairStore = ccProperties.getSsl().getCcKeyPairStore();
        if (StringUtils.isEmpty(ccKeyPairStore.getContent())
                && StringUtils.isNotEmpty(ccKeyPairStore.getKeyContent())
                && StringUtils.isNotEmpty(ccKeyPairStore.getCertificateContent())) {
            createKeyStoreContent(ccKeyPairStore);
        }
        if (StringUtils.isEmpty(ccKeyPairStore.getContent())) {
            generateKeyPair("control_center", String.format(DN_FORMAT_CC, ccProperties.getServiceName()),
                            dccKeyStoreFileName, ccProperties.getSsl().getKeystore().getPassword(), false);
        } else {
            Path importKeyStorePath = certificatesDirectoryPath.resolve("import-cc-keystore");
            Files.write(importKeyStorePath, Base64.getDecoder().decode(ccKeyPairStore.getContent()
                    .replaceAll("[\\r\\n]", "").trim()));
            importKeyStore(importKeyStorePath, certificatesDirectoryPath.resolve(dccKeyStoreFileName),
                    ccKeyPairStore.getType(),
                            dccKeyStoreFileName.endsWith(".jks") ? "JKS" : "PKCS12", ccKeyPairStore.getPassword(),
                    ccProperties.getSsl().getKeystore().getPassword(), ccKeyPairStore.getKeyAlias(),
                    "control_center", ccKeyPairStore.getKeyPassword());
            Files.deleteIfExists(importKeyStorePath);
        }
        exportCertificate("control_center", dccKeyStoreFileName,
                ccProperties.getSsl().getKeystore().getPassword(), "control_center.cer", true);
        importCertificate("control_center", dccTrustStoreFileName,
                ccProperties.getSsl().getTruststore().getPassword(), "control_center.cer");
        FileUtils.copyFileToDirectory(certificatesDirectoryPath.resolve("control_center.cer").toFile(),
                certificatesDirectoryPath.resolve("cacerts").toFile());
    }

    private static void createKeyStoreContent(KeyPairProperties keyPairStore) throws IOException, CertificateException,
            KeyStoreException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(keyPairStore.getPassword())) {
            keyPairStore.setPassword(RandomStringUtils.random(16, true, true));
        }
        String keyContent = keyPairStore.getKeyContent();
        if (!keyContent.startsWith("---")) {
            keyContent = new String(Base64.getDecoder().decode(keyContent));
        }
        try (Reader keyContentReader = new StringReader(keyContent)) {
            try (PEMParser keyPemParser = new PEMParser(keyContentReader)) {
                PrivateKey privateKey = new JcaPEMKeyConverter()
                        .getKeyPair((PEMKeyPair) keyPemParser.readObject())
                        .getPrivate();
                String certificateContent = keyPairStore.getCertificateContent();
                if (!certificateContent.startsWith("---")) {
                    certificateContent = new String(Base64.getDecoder().decode(certificateContent));
                }
                try (Reader certificateContentReader = new StringReader(certificateContent)) {
                    try (PEMParser certificatePemParser = new PEMParser(certificateContentReader)) {
                        Certificate certificate = new JcaX509CertificateConverter()
                                .getCertificate((X509CertificateHolder) certificatePemParser.readObject());
                        KeyStore keyStore = KeyStore.getInstance("PKCS12");
                        keyStore.load(null);
                        keyStore.setKeyEntry(keyPairStore.getKeyAlias(), privateKey,
                                keyPairStore.getPassword().toCharArray(), new Certificate[]{certificate});
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        try (outputStream) {
                            keyStore.store(outputStream, keyPairStore.getPassword().toCharArray());
                        }
                        keyPairStore.setContent(new String(Base64.getEncoder().encode(outputStream.toByteArray())));
                    }
                }
            }
        }
    }

    private void createLegacyDccCertificate() throws IOException {
        progressService.setCurrentTask(Task.CREATE_LEAGACY_DCC_CERTIFICATE);
        generateKeyPair("legacy_dcc", DN_FORMAT, "dcc-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), true);
        exportCertificate("legacy_dcc", "dcc-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "legacy-dcc.cer", false);
    }

    private void createAgentCertificateStores() throws IOException {
        progressService.setCurrentTask(Task.CREATE_AGENT_CERTIFICATE);
        generateKeyPair("agent", DN_FORMAT, "agent-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), false);
        exportCertificate("agent", "agent-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "agent.cer", false);
        importCertificate("agent", "agent-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "agent.cer");
    }

    private void createLegacyAgentCertificateStores() throws IOException {
        progressService.setCurrentTask(Task.CREATE_LEGACY_AGENT_CERTIFICATE);
        generateKeyPair("agent", DN_FORMAT, "legacy-agent-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), true);
        exportCertificate("agent", "legacy-agent-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "legacy-agent.cer", false);
        // Legacy agent truststore needs keystore password to be compatible with older versions
        importCertificate("agent", "legacy-agent-truststore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "legacy-agent.cer");
        importCertificate("agent", "legacy-agent-truststore-kp.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "agent.cer");
    }

    private void createApplicationCertificateStores() throws IOException {
        progressService.setCurrentTask(Task.CREATE_APPLICATION_CERTIFICATE);
        generateKeyPair("app", DN_FORMAT, "application-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), false);
        exportCertificate("app", "application-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "application.cer", false);
        importCertificate("app", "application-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "application.cer");
    }

    private void createDigitalSignatureCertificateStores() throws IOException {
        progressService.setCurrentTask(Task.CREATE_DIGITAL_SIGNATURE_CERTIFICATE);
        generateKeyPair(ccProperties.getDnsName(), String.format(DN_FORMAT_CC, ccProperties.getDnsName()),
                "digital-signature-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), false);
        exportCertificate(ccProperties.getDnsName(), "digital-signature-keystore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "digital-signature.cer", false);
        importCertificate(ccProperties.getDnsName(), "digital-signature-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "digital-signature.cer");
    }

    private void createTruststores() throws IOException {
        progressService.setCurrentTask(Task.CREATE_TRUST_STORES);
        importCertificate("agent", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "agent.cer");
        importCertificate("legacy_agent", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "legacy-agent.cer");
        importCertificate("app", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "application.cer");
        importCertificate("policyauthor", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "policyAuthor.cer");
        importCertificate("enrollment", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "enrollment.cer");
        importCertificate("keymanagement", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "keymanagement.cer");
        importCertificate("temp_agent", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "temp_agent.cer");
        importCertificate("orig_temp_agent", "dcc-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "orig_temp_agent.cer");
        importCertificate("dcc", "agent-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "dcc.cer");

        // Legacy agent truststore needs keystore password to be compatible with older versions
        importCertificate("dcc", "legacy-agent-truststore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "legacy-dcc.cer");
        importCertificate("current_dcc", "legacy-agent-truststore.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "dcc.cer");
        importCertificate("dcc", "legacy-agent-truststore-kp.p12",
                ccProperties.getSsl().getKeystore().getPassword(), "dcc.cer");

        importCertificate("dcc", "application-truststore.p12",
                ccProperties.getSsl().getTruststore().getPassword(), "dcc.cer");
    }

    private void generateKeyPair(String alias, String dName, String keyStoreFileName, String password, boolean legacy)
            throws IOException {
        Path keyStoreFilePath = Paths.get(ccProperties.getHome(), "server", "certificates", keyStoreFileName);
        if (!keyStoreFilePath.toFile().exists() ||
                !aliasFound(keyStoreFilePath, alias, password)) {
            List<String> arguments = new ArrayList<>();
            arguments.add("-genkeypair");
            arguments.add("-alias");
            arguments.add(alias);
            arguments.add("-dname");
            arguments.add(dName);
            arguments.add("-keystore");
            arguments.add(keyStoreFilePath.toString());
            arguments.add("-keypass");
            arguments.add(escapeQuotes(password));
            arguments.add("-storepass");
            arguments.add(escapeQuotes(password));
            arguments.add("-keyalg");
            arguments.add(legacy ? KEY_ALGORITHM_LEGACY : KEY_ALGORITHM);
            arguments.add("-keysize");
            arguments.add(String.valueOf(legacy ? KEY_SIZE_LEGACY : KEY_SIZE));
            arguments.add("-sigalg");
            arguments.add(legacy ? SIGNATURE_ALGORITHM_LEGACY : SIGNATURE_ALGORITHM);
            arguments.add("-validity");
            arguments.add(String.valueOf(VALIDITY));
            keyTool(arguments);
            logger.debug("Key pair generated: {}, {}", alias, keyStoreFileName);
        }
    }

    private void importKeyStore(Path sourceKeyStorePath, Path destKeyStorePath, String srcStoreType, String destStoreType,
                                String srcStorePassword, String destStorePassword, String srcAlias, String destAlias,
                                String srcKeyPassword) throws IOException {
        if (sourceKeyStorePath.toFile().exists()) {
            List<String> arguments = new ArrayList<>();
            arguments.add("-importkeystore");
            arguments.add("-srckeystore");
            arguments.add(sourceKeyStorePath.toString());
            arguments.add("-destkeystore");
            arguments.add(destKeyStorePath.toString());
            arguments.add("-srcstoretype");
            arguments.add(srcStoreType);
            arguments.add("-deststoretype");
            arguments.add(destStoreType);
            if (StringUtils.isNotEmpty(srcStorePassword)) {
                arguments.add("-srcstorepass");
                arguments.add(escapeQuotes(srcStorePassword));
            }
            arguments.add("-deststorepass");
            arguments.add(escapeQuotes(destStorePassword));
            arguments.add("-destkeypass");
            arguments.add(escapeQuotes(destStorePassword));
            if (StringUtils.isNotEmpty(srcAlias)) {
                arguments.add("-srcalias");
                arguments.add(srcAlias);
            }
            if (StringUtils.isNotEmpty(destAlias)) {
                arguments.add("-destalias");
                arguments.add(destAlias);
            }
            if (StringUtils.isNotEmpty(srcKeyPassword)) {
                arguments.add("-srckeypass");
                arguments.add(escapeQuotes(srcKeyPassword));
            }
            keyTool(arguments);
            logger.info("Key store imported from {} to {}", sourceKeyStorePath, destKeyStorePath);
        }
    }

    private void exportCertificate(String alias, String keyStoreFileName, String password, String certificateFileName,
                                   boolean rfc) throws IOException {
        Path keyStoreFilePath = Paths.get(ccProperties.getHome(), "server", "certificates", keyStoreFileName);
        Path certificateFilePath = Paths.get(ccProperties.getHome(), "server", "certificates", certificateFileName);
        if (keyStoreFilePath.toFile().exists() && !certificateFilePath.toFile().exists()) {
            List<String> arguments = new ArrayList<>();
            arguments.add("-exportcert");
            if (rfc) {
                arguments.add("-rfc");
            }
            arguments.add("-alias");
            arguments.add(alias);
            arguments.add("-file");
            arguments.add(certificateFilePath.toString());
            arguments.add("-keystore");
            arguments.add(keyStoreFilePath.toString());
            arguments.add("-storepass");
            arguments.add(escapeQuotes(password));
            keyTool(arguments);
            logger.debug("Certificate exported: {}, {}, {}", alias, keyStoreFileName, certificateFileName);
        }
    }

    private void convertKeyStoreType(Path sourceKeyStorePath, Path destKeyStorePath, String srcStoreType, String destStoreType,
                    String storePassword)
            throws IOException {
        logger.debug("Converting {} store type from {} to {}", sourceKeyStorePath, srcStoreType, destStoreType);

        if (sourceKeyStorePath.toFile().exists()) {
            List<String> arguments = new ArrayList<>();
            arguments.add("-importkeystore");
            arguments.add("-srckeystore");
            arguments.add(sourceKeyStorePath.toString());
            arguments.add("-destkeystore");
            arguments.add(destKeyStorePath.toString());
            arguments.add("-srcstoretype");
            arguments.add(srcStoreType);
            arguments.add("-deststoretype");
            arguments.add(destStoreType);
            arguments.add("-srcstorepass");
            arguments.add(escapeQuotes(storePassword));
            arguments.add("-deststorepass");
            arguments.add(escapeQuotes(storePassword));
            keyTool(arguments);
            logger.debug("Key store converted from {} to {}", sourceKeyStorePath, destKeyStorePath);
        }
    }

    private String escapeQuotes(final String password) {
        if(SystemUtils.IS_OS_WINDOWS) {
            return password.replaceAll("\"", "\\\\\"");
        }

        return password;
    }

    public void uploadKeyStores() throws IOException, NoSuchAlgorithmException {
        File overrideDirectory = Paths.get(ccProperties.getHome(), "server", "certificates", "override").toFile();
        if(!overrideDirectory.exists()) {
            return;
        }
        File[] overrideFiles = overrideDirectory.listFiles(File::isFile);
        if (overrideFiles == null) {
            return;
        }
        logger.info("Uploading SSL key stores to database");
        for (File file : overrideFiles) {
            logger.debug("Uploading store file {} to database", file.getName());
            String hashAlgorithm = DbHelper.getJdbcTemplate().queryForObject(SQL_GET_HASH_ALGORITHM,
                    String.class, file.getName());
            String keystoreFileChecksum = bytesToHex(Hash.valueOf(hashAlgorithm).checksum(file));
            try (FileInputStream keyStoreFileFileInputStream = new FileInputStream(file)) {
                if (hashAlgorithm != null) {
                    DbHelper.getJdbcTemplate().update(connection -> {
                        PreparedStatement updatePreparedStatement = connection.prepareStatement(SQL_UPDATE_KEYSTORE_FILE);
                        updatePreparedStatement.setBinaryStream(1, keyStoreFileFileInputStream, file.length());
                        updatePreparedStatement.setString(2, keystoreFileChecksum);
                        updatePreparedStatement.setDate(3, new Date(System.currentTimeMillis()));
                        updatePreparedStatement.setString(4, file.getName());
                        return updatePreparedStatement;
                    });
                }
            }
            Files.deleteIfExists(file.toPath());
        }
    }

    private String bytesToHex(byte[] hashInBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hashInBytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString().toUpperCase();
    }
}
