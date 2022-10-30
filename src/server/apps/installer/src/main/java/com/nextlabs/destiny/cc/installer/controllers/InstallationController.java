package com.nextlabs.destiny.cc.installer.controllers;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import org.apache.juli.ClassLoaderLogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.config.properties.CertificateProperties;
import com.nextlabs.destiny.cc.installer.config.properties.DbProperties;
import com.nextlabs.destiny.cc.installer.config.properties.PortProperties;
import com.nextlabs.destiny.cc.installer.dto.ResponseMessage;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.helpers.DbHelper;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.models.ProgressMessage;
import com.nextlabs.destiny.cc.installer.services.CertificateManagementService;
import com.nextlabs.destiny.cc.installer.services.InstallService;
import com.nextlabs.destiny.cc.installer.services.ProgressService;
import com.nextlabs.destiny.cc.installer.validators.CertificateValidator;
import com.nextlabs.destiny.cc.installer.validators.DbConnectionValidator;
import com.nextlabs.destiny.cc.installer.validators.InstallationPathValidator;
import com.nextlabs.destiny.cc.installer.validators.LicenseValidator;

/**
 * API for performing Control Center installation.
 *
 * @author Sachindra Dasun
 */
@RestController
@RequestMapping("installation")
@Validated
public class InstallationController {

    private static final Marker INSTALLER_CONSOLE_MARKER = MarkerFactory.getMarker("INSTALLER_CONSOLE");
    private static final Logger logger = LoggerFactory.getLogger(InstallationController.class);

    @Autowired
    private CcProperties ccProperties;
    @Autowired
    private CertificateManagementService certificateManagementService;
    @Autowired
    private InstallService installService;
    @Autowired
    private ProgressService progressService;

    @GetMapping("properties")
    public ResponseEntity<CcProperties> getProperties() {
        return new ResponseEntity<>(ccProperties, HttpStatus.OK);
    }

    @PutMapping(value = "properties")
    public ResponseEntity<String> updateProperties(@RequestBody CcProperties installationProperties) {
        ccProperties.updateFrom(installationProperties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "validateDbConnection")
    public ResponseEntity<ResponseMessage> validateDbConnection(@RequestBody DbProperties validateDbProperties) {
        DbProperties currentDbProperties = ccProperties.getDb();
        ccProperties.setDb(validateDbProperties);
        if (new DbConnectionValidator().isValid(ccProperties, null)) {
            return new ResponseEntity<>(new ResponseMessage("Valid database connection"), HttpStatus.OK);
        }
        ccProperties.setDb(currentDbProperties);
        return new ResponseEntity<>(new ResponseMessage("Invalid database connection"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "validateInstallationPath")
    public ResponseEntity<ResponseMessage> validateInstallationPath(@RequestBody String installationPath) {
        if (!new InstallationPathValidator().isValid(installationPath, null)) {
            throw new ValidationException("Invalid installation path provided");
        }
        return new ResponseEntity<>(new ResponseMessage("Installation path validated"), HttpStatus.OK);
    }

    @PostMapping("validatePorts")
    public ResponseEntity<ResponseMessage> validatePorts(@RequestBody @Valid PortProperties portProperties) {
        return new ResponseEntity<>(new ResponseMessage("Ports validated"), HttpStatus.OK);
    }

    @PostMapping("getDbDetails")
    public ResponseEntity<DbProperties> getDbDetails(@RequestBody String jdbcUrl) {
        DbProperties dbProperties = new DbProperties();
        dbProperties.setUrl(jdbcUrl);
        return new ResponseEntity<>(dbProperties, HttpStatus.OK);
    }

    @PostMapping("getJdbcUrl")
    public ResponseEntity<Map<String, String>> getJdbcUrl(@RequestBody DbProperties dbProperties) {
        return new ResponseEntity<>(Collections.singletonMap("jdbcUrl", DbHelper.getJdbcUrl(dbProperties)), HttpStatus.OK);
    }

    @PostMapping("uploadLicense")
    public ResponseEntity<ResponseMessage> uploadLicense(@RequestParam("license") MultipartFile licenseFile) throws IOException {
        if (!licenseFile.isEmpty()) {
            Path licenseFilePath = Paths.get(ccProperties.getHome(), "server", "license", "license.dat");
            Files.write(licenseFilePath, licenseFile.getBytes());
            if (new LicenseValidator().isValid(ccProperties, null)) {
                ccProperties.setLicenseFilePath(licenseFilePath.toString());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                Files.deleteIfExists(licenseFilePath);
            }
        }
        return new ResponseEntity<>(new ResponseMessage("Invalid license"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("uploadCACertificate")
    public ResponseEntity<ResponseMessage> uploadCACertificate(@RequestParam("caCertificate") MultipartFile caCertificate) throws IOException {
        if (!caCertificate.isEmpty()) {
            Path caCertificatePath = Paths.get(ccProperties.getHome(), "server", "certificates", "cacerts",
                    caCertificate.getOriginalFilename());
            Files.write(caCertificatePath, caCertificate.getBytes());
            if (new CertificateValidator().isValid(new CertificateProperties(caCertificatePath.toString()), null)) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                Files.deleteIfExists(caCertificatePath);
            }
        }
        return new ResponseEntity<>(new ResponseMessage("Invalid database certificate"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "install")
    public ResponseEntity<ResponseMessage> install(@RequestBody CcProperties installationProperties)
            throws Exception {
        ccProperties.updateFrom(installationProperties);
        installService.install(ccProperties);
        return new ResponseEntity<>(new ResponseMessage("Installation started"), HttpStatus.OK);
    }

    @GetMapping("startCC")
    public ResponseEntity<ResponseMessage> startCC() throws IOException {
        installService.createCcStartFiles();
        return new ResponseEntity<>(new ResponseMessage("Startup configured"), HttpStatus.OK);
    }

    @GetMapping("exit")
    public ResponseEntity<ResponseMessage> exit() {
        LogManager logManager = LogManager.getLogManager();
        if (logManager instanceof ClassLoaderLogManager) {
            ((ClassLoaderLogManager) logManager).shutdown();
        }
        System.exit(0);
        return new ResponseEntity<>(new ResponseMessage("Installer exited"), HttpStatus.OK);
    }

    @GetMapping(value = "log", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    byte[] log() throws IOException {
        return Files.readAllBytes(Paths.get(ParameterHelper.INIT_CC_HOME, "server", "logs", System.getProperty("server.hostname"), "installer.log"));
    }

    @GetMapping("progressMileStones")
    public Set<String> getProgressMileStones() {
        return progressService.getAvailableTaskGroups();
    }

    @GetMapping("progress")
    public ProgressMessage currentTask() {
        return progressService.getCurrentProgress();
    }

    @DeleteMapping("deleteCACertificate")
    public ResponseEntity<ResponseMessage> deleteCACertificate(String caCertificateFileName) throws IOException {
        Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "certificates", "cacerts",
                caCertificateFileName));
        certificateManagementService.deleteCertificate(caCertificateFileName, CcProperties.CACERTS_FILE.toString(),
                ccProperties.getSsl().getInstallerTruststore().getPassword());
        return new ResponseEntity<>(new ResponseMessage("Certificate deleted"), HttpStatus.OK);
    }

    @DeleteMapping("deleteLicense")
    public ResponseEntity<ResponseMessage> deleteLicense() throws IOException {
        Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "license", "license.dat"));
        ccProperties.setLicenseFilePath("");
        return new ResponseEntity<>(new ResponseMessage("License deleted"), HttpStatus.OK);
    }

    @GetMapping("listCACertificates")
    public ResponseEntity<List<String>> listCACertificates() {
        List<String> caCertificateFileNames = null;
        File[] caCertificateFiles = Paths.get(ccProperties.getHome(), "server", "certificates", "cacerts")
                .toFile().listFiles();
        if (caCertificateFiles != null) {
            caCertificateFileNames = Arrays.stream(caCertificateFiles).map(File::getName).collect(Collectors.toList());
        }
        return new ResponseEntity<>(caCertificateFileNames, HttpStatus.OK);
    }

    @PostConstruct
    public void postConstruct() {
        if (ccProperties.isWebInstaller()) {
            String urlMessage;
            String installerUrlUsingDnsName = String.format("https://%s%s", ccProperties.getDnsName(),
                    ccProperties.getPort().getAppServicePort() == 443 ? "" :
                            String.format(":%s", ccProperties.getPort().getAppServicePort()));
            if (ccProperties.getHostname().equalsIgnoreCase(ccProperties.getDnsName())) {
                urlMessage = String.format("Please access the URL: %s and use the following key to begin.",
                        installerUrlUsingDnsName);
            } else {
                String installationUrlUsingHostname = String.format("https://%s%s", ccProperties.getHostname(),
                        ccProperties.getPort().getAppServicePort() == 443 ? "" :
                                String.format(":%s", ccProperties.getPort().getAppServicePort()));
                urlMessage = String.format("Please access the URL: %s (or %s) and use the following key to begin.",
                        installerUrlUsingDnsName, installationUrlUsingHostname);
            }
            logger.info(INSTALLER_CONSOLE_MARKER, urlMessage);
            // Access key should be displayed only on the CLI.
            System.out.println(String.format("Access Key: %s", ccProperties.getAccessKey()));
            logger.info(INSTALLER_CONSOLE_MARKER, "The same key has been saved to the file access-key.properties in your Control Center directory.");
            logger.info(INSTALLER_CONSOLE_MARKER, "Do not close this window while the {} task is in progress.",
                    ccProperties.getRunningMode() == RunningMode.UPGRADE ? "upgrade" : "installation");
        }
    }

}
