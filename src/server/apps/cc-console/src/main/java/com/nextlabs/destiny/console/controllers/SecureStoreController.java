package com.nextlabs.destiny.console.controllers;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.dto.store.CertificateDTO;
import com.nextlabs.destiny.console.dto.store.SecureStoreDTO;
import com.nextlabs.destiny.console.enums.EntryType;
import com.nextlabs.destiny.console.enums.SecureStoreFile;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.services.SecureStoreService;
import com.nextlabs.destiny.console.services.SysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * REST controller to manage key stores.
 *
 * @author Chok Shah Neng
 */
@RestController
@ApiVersion(1)
@RequestMapping("secureStore")
public class SecureStoreController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureStoreController.class);

    private static final List<String> MESSAGE_DIGEST_ALGORITHM = Arrays.asList("MD2", "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512");
    private static final List<String> KEY_PAIR_ALGORITHMS = Arrays.asList("DSA", "RSA");
    private static final List<String> SECRET_KEY_ALGORITHMS = Arrays.asList("AES", "DES");
    private static final Set<Integer> AES_KEY_SIZES = new TreeSet<>();
    private static final Set<Integer> DES_KEY_SIZES = new TreeSet<>();
    private static final Map<String, String> EC_SIGNATURE_ALGORITHMS = new LinkedHashMap();
    private static final Map<String, String> DSA_SIGNATURE_ALGORITHMS = new LinkedHashMap();
    private static final Map<String, String> RSA_SIGNATURE_ALGORITHMS = new LinkedHashMap();
    private static final List<String> MESSAGE_DIGEST_ALGORITHMS = Arrays.asList("MD2", "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512");
    @Autowired
    private SecureStoreService secureStoreService;

    @Autowired
    private SysConfigService sysConfigService;

    static {
        AES_KEY_SIZES.add(128);
        AES_KEY_SIZES.add(192);
        AES_KEY_SIZES.add(256);

        DES_KEY_SIZES.add(56);

        EC_SIGNATURE_ALGORITHMS.put("SHA256withECDSA", "SHA-256 with ECDSA");
        EC_SIGNATURE_ALGORITHMS.put("SHA384withECDSA", "SHA-384 with ECDSA");
        EC_SIGNATURE_ALGORITHMS.put("SHA512withECDSA", "SHA-512 with ECDSA");

        DSA_SIGNATURE_ALGORITHMS.put("SHA256withDSA", "SHA-256 with DSA");

        RSA_SIGNATURE_ALGORITHMS.put("MD2withRSA", "MD2 with RSA");
        RSA_SIGNATURE_ALGORITHMS.put("MD5withRSA", "MD5 with RSA");
        RSA_SIGNATURE_ALGORITHMS.put("SHA1withRSA", "SHA-1 with RSA");
        RSA_SIGNATURE_ALGORITHMS.put("SHA224withRSA", "SHA-224 with RSA");
        RSA_SIGNATURE_ALGORITHMS.put("SHA256withRSA", "SHA-256 with RSA");
        RSA_SIGNATURE_ALGORITHMS.put("SHA384withRSA", "SHA-384 with RSA");
        RSA_SIGNATURE_ALGORITHMS.put("SHA512withRSA", "SHA-512 with RSA");
    }

    @GetMapping("list/storeNames/{storeType}")
    public ConsoleResponseEntity<List<String>> listStoreNames(@PathVariable("storeType") String storeType) {
        validations.assertNotBlank(storeType, "storeType");
        List<String> storeNames = new ArrayList<>();

        for(SecureStoreFile secureStoreFile : SecureStoreFile.values()) {
            if(secureStoreFile.getUIManageable()
                    && secureStoreFile.getStoreType().name().equalsIgnoreCase(storeType)) {
                storeNames.add(secureStoreFile.getStoreName());
            }
        }

        return ConsoleResponseEntity.get(storeNames, HttpStatus.OK);
    }

    @GetMapping("list/entries/{storeType}")
    public ConsoleResponseEntity<List<CertificateDTO>> listEntries(@PathVariable("storeType") String storeType) {
        validations.assertNotBlank(storeType, "storeType");

        return ConsoleResponseEntity.get(secureStoreService.list(storeType), HttpStatus.OK);
    }

    @GetMapping("list/storeFiles")
    public ConsoleResponseEntity<List<SecureStoreFile>> listStoreFiles() {
        List<SecureStoreFile> storeFiles = new ArrayList<>();
        storeFiles.addAll(Arrays.asList(SecureStoreFile.values()));
        return ConsoleResponseEntity.get(storeFiles, HttpStatus.OK);
    }

    @GetMapping("list/digestAlgorithms")
    public ConsoleResponseEntity<List<String>> listDigestAlgorithms() {
        return ConsoleResponseEntity.get(MESSAGE_DIGEST_ALGORITHM, HttpStatus.OK);
    }

    @GetMapping("list/keyAlgorithms")
    public ConsoleResponseEntity<List<String>> listKeyAlgorithms() {
        return ConsoleResponseEntity.get(KEY_PAIR_ALGORITHMS, HttpStatus.OK);
    }

    @GetMapping("list/signatureAlgorithms/{keyAlgorithm}")
    public ConsoleResponseEntity<List<SinglevalueFieldDTO>> listSignatureAlgorithms(@PathVariable("keyAlgorithm") String keyAlgorithm) {
        List<SinglevalueFieldDTO> signatureAlgorithms = new ArrayList<>();
        if("DSA".equals(keyAlgorithm)) {
            for(Map.Entry<String, String> dsaSignature : DSA_SIGNATURE_ALGORITHMS.entrySet()) {
                signatureAlgorithms.add(SinglevalueFieldDTO.create(dsaSignature.getKey(), dsaSignature.getValue()));
            }
        } else if("RSA".equals(keyAlgorithm)) {
            for(Map.Entry<String, String> rsaSignature : RSA_SIGNATURE_ALGORITHMS.entrySet()) {
                signatureAlgorithms.add(SinglevalueFieldDTO.create(rsaSignature.getKey(), rsaSignature.getValue()));
            }
        } else if("EC".equals(keyAlgorithm)) {
            for(Map.Entry<String, String> ecSignature : EC_SIGNATURE_ALGORITHMS.entrySet()) {
                signatureAlgorithms.add(SinglevalueFieldDTO.create(ecSignature.getKey(), ecSignature.getValue()));
            }
        }

        return ConsoleResponseEntity.get(signatureAlgorithms, HttpStatus.OK);
    }

    @GetMapping("getEntriesByStore/{storeName}")
    public ConsoleResponseEntity getEntriesByStore(@PathVariable("storeName") String storeName)
            throws ConsoleException {
        validations.assertNotBlank(storeName, "storeName");

        List<CertificateDTO> certificateDTOs = secureStoreService.getEntriesByStoreName(storeName);

        if(certificateDTOs == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        return ConsoleResponseEntity.get(SimpleResponseDTO.create(
                        msgBundle.getText("success.data.found.code"),
                        msgBundle.getText("success.data.found"), certificateDTOs),
                HttpStatus.OK);
    }

    @GetMapping("entryDetails/{storeName}/{alias}")
    public ConsoleResponseEntity entryDetails(@PathVariable("storeName") String storeName,
        @PathVariable("alias") String alias)
                    throws ConsoleException {
        validations.assertNotBlank(storeName, "storeName");
        validations.assertNotBlank(alias, "alias");

        CertificateDTO certificateDTO = secureStoreService.getDetails(storeName, alias);

        if(certificateDTO == null) {
            throw new NoDataFoundException(
                            msgBundle.getText("no.data.found.code"),
                            msgBundle.getText("no.data.found"));
        }

        return ConsoleResponseEntity.get(SimpleResponseDTO.create(
                        msgBundle.getText("success.data.found.code"),
                        msgBundle.getText("success.data.found"), certificateDTO),
            HttpStatus.OK);
    }

    @PutMapping("changePassword")
    public ConsoleResponseEntity changePassword(@RequestBody SecureStoreDTO secureStoreDTO) {
        validations.assertNotBlank(secureStoreDTO.getStoreType(), "storeType");
        validations.assertNotBlank(secureStoreDTO.getNewPassword(), "password");
        validations.assertValidPassword(secureStoreDTO.getNewPassword());

        ResponseDTO response;
        try {
            if(secureStoreService.changePassword(secureStoreDTO.getStoreType(), secureStoreDTO.getNewPassword())) {
                sysConfigService.sendSecureStoreRefreshRequest();
                response = SimpleResponseDTO
                                .createWithType(msgBundle.getText("success.data.saved.code"),
                                                msgBundle.getText("success.data.saved"),
                                                msgBundle.getText("success.data.saved"));
            } else {
                response = SimpleResponseDTO
                                .createWithType(msgBundle.getText("task.execution.error.code"),
                                                msgBundle.getText("task.execution.error"),
                                                msgBundle.getText("task.execution.error"));
            }
        } catch(ConsoleException err) {
            response = ResponseDTO.create(err.getStatusCode(), err.getStatusMsg());
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @PostMapping("generateKey")
    public ConsoleResponseEntity generateKey(@RequestBody CertificateDTO certificateDTO) {
        validations.assertNotBlank(certificateDTO.getStoreName(), "storeName");
        validations.assertNotBlank(certificateDTO.getAlias(), "alias");
        validations.assertNotBlank(certificateDTO.getKeyAlgorithmName(), "keyAlgorithm");
        validations.assertNotZero(certificateDTO.getKeySize(), "keySize");
        if(EntryType.KEY_PAIR.name().equalsIgnoreCase(certificateDTO.getType())) {
            validations.assertNotZero(certificateDTO.getValidity(), "validity");
            validations.assertNotZero(certificateDTO.getValidFrom(), "validFrom");
            validations.assertNotBlank(certificateDTO.getSubjectDN(), "subjectDN");
        } else {
            if(certificateDTO.getKeyAlgorithmName().equalsIgnoreCase("AES")) {
                validations.assertWithin(certificateDTO.getKeySize(), AES_KEY_SIZES, "keySize");
            } else {
                validations.assertWithin(certificateDTO.getKeySize(), DES_KEY_SIZES, "keySize");
            }
        }
        
        ResponseDTO response;
        try {
            if(secureStoreService.generateKey(certificateDTO)) {
                sysConfigService.sendSecureStoreRefreshRequest();
                response = SimpleResponseDTO
                                .createWithType(msgBundle.getText("success.data.saved.code"),
                                                msgBundle.getText("success.data.saved"),
                                                msgBundle.getText("success.data.saved"));
            } else {
                response = SimpleResponseDTO
                                .createWithType(msgBundle.getText("task.execution.error.code"),
                                                msgBundle.getText("task.execution.error"),
                                                msgBundle.getText("task.execution.error"));
            }
        } catch(ConsoleException err) {
            response = ResponseDTO.create(err.getStatusCode(), err.getStatusMsg());
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @PostMapping("generateCsr")
    public ConsoleResponseEntity<ResponseDTO> generateCsr(@RequestBody CertificateDTO certificateDTO,
                    HttpServletResponse response) {
        validations.assertNotBlank(certificateDTO.getStoreName(), "storeName");
        validations.assertNotBlank(certificateDTO.getAlias(), "alias");

        ResponseDTO responseDTO;
        try {
            String fileName = secureStoreService.generateCsr(certificateDTO);

            responseDTO = SimpleResponseDTO.create(
                            msgBundle.getText("success.file.export.code"),
                            msgBundle.getText("success.file.export"), fileName);

            response.setHeader("Content-Disposition", "attachment");
        } catch (ConsoleException e) {
            responseDTO = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
        }

        return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("remove/{storeName}/{alias}")
    public ConsoleResponseEntity remove(@PathVariable("storeName") String storeName,
                    @PathVariable("alias") String alias)
                    throws ConsoleException {
        validations.assertNotBlank(storeName, "storeName");
        validations.assertNotBlank(alias, "alias");

        ResponseDTO response;
        try {
            if (secureStoreService.removeEntry(storeName, alias)) {
                sysConfigService.sendSecureStoreRefreshRequest();
                response = SimpleResponseDTO.createWithType(msgBundle.getText("success.data.deleted.code"),
                                msgBundle.getText("success.data.deleted"), msgBundle.getText("success.data.deleted"));
            } else {
                response = SimpleResponseDTO.createWithType(msgBundle.getText("task.execution.error.code"),
                                msgBundle.getText("task.execution.error"), msgBundle.getText("task.execution.error"));
            }
        } catch (ConsoleException e) {
            response = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @DeleteMapping("bulkDelete")
    public ConsoleResponseEntity<ResponseDTO> bulkDelete(@RequestBody Map<String, Set<String>> entries)
                    throws ConsoleException {
        validations.assertNotNull(entries, "entries");

        SimpleResponseDTO<String> response;
        if(secureStoreService.removeEntries(entries)) {
            sysConfigService.sendSecureStoreRefreshRequest();
            response = SimpleResponseDTO
                            .createWithType(msgBundle.getText("success.data.deleted.code"),
                                            msgBundle.getText("success.data.deleted"),
                                            msgBundle.getText("success.data.deleted"));
        } else {
            response = SimpleResponseDTO
                            .createWithType(msgBundle.getText("task.execution.error.code"),
                                            msgBundle.getText("task.execution.error"),
                                            msgBundle.getText("task.execution.error"));
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @PostMapping(value = "importCertificate", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ConsoleResponseEntity<ResponseDTO> importCertificate(@RequestPart(value = "storeName") String storeName,
                    @RequestPart(value = "alias") String alias,
                    @RequestPart(value = "verifyTrustChain") String verifyTrustChain,
                    @RequestPart(value = "aliasShouldExist") String aliasShouldExist,
                    @RequestPart(value = "replaceIfAliasExist") String replaceIfAliasExist,
                    @RequestPart(value = "file") MultipartFile inputFile) {
        validations.assertNotBlank(storeName, "storeName");
        validations.assertNotBlank(alias, "alias");
        validations.assertNotBlank(verifyTrustChain, "verifyTrustChain");
        validations.assertNotBlank(aliasShouldExist, "aliasShouldExist");

        ResponseDTO response;

        try {
            if (secureStoreService.importCertificate(storeName, alias, inputFile.getName(),
                    Boolean.valueOf(verifyTrustChain), Boolean.valueOf(aliasShouldExist),
                    Boolean.valueOf(replaceIfAliasExist), inputFile.getBytes())) {
                sysConfigService.sendSecureStoreRefreshRequest();
                response = SimpleResponseDTO
                                .createWithType(msgBundle.getText("success.data.saved.code"),
                                                msgBundle.getText("success.data.saved"),
                                                msgBundle.getText("success.data.saved"));
            } else {
                response = SimpleResponseDTO
                                .createWithType(msgBundle.getText("task.execution.error.code"),
                                                msgBundle.getText("task.execution.error"),
                                                msgBundle.getText("task.execution.error"));
            }
        } catch(ConsoleException err) {
            response = ResponseDTO.create(err.getStatusCode(), err.getStatusMsg());
        } catch(IOException err) {
            response = ResponseDTO.create(msgBundle.getText("file.upload.failed.code"),
                            msgBundle.getText("file.upload.failed"));
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @GetMapping("exportCertificate/{storeName}/{alias}")
    public ConsoleResponseEntity<ResponseDTO> exportCertificate(@PathVariable("storeName") String storeName,
                    @PathVariable("alias") String alias, HttpServletResponse response) {
        validations.assertNotBlank(storeName, "storeName");
        validations.assertNotBlank(alias, "alias");

        ResponseDTO responseDTO;
        try {
            String fileName = secureStoreService.exportCertificate(storeName, alias);

            responseDTO = SimpleResponseDTO.create(
                            msgBundle.getText("success.file.export.code"),
                            msgBundle.getText("success.file.export"), fileName);

            response.setHeader("Content-Disposition", "attachment");
        } catch (ConsoleException e) {
            responseDTO = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
        }

        return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    @GetMapping("exportAll")
    public ConsoleResponseEntity<ResponseDTO> exportAll(HttpServletResponse response) {
        ResponseDTO responseDTO;
        try {
            String fileName = secureStoreService.exportAll();

            responseDTO = SimpleResponseDTO.create(
                            msgBundle.getText("success.file.export.code"),
                            msgBundle.getText("success.file.export"), fileName);

            response.setHeader("Content-Disposition", "attachment");
        } catch (ConsoleException e) {
            responseDTO = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
        }

        return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    @PostMapping("replaceStoreFile")
    public ConsoleResponseEntity<ResponseDTO> replaceStoreFile(@RequestParam("storeFile") List<MultipartFile> storeFiles) {
        ResponseDTO responseDTO;

        validations.assertCollectionEmpty(storeFiles, "Secure store file");

        try {
            secureStoreService.replaceStoreFile(storeFiles);
            sysConfigService.sendSecureStoreRefreshRequest();
            responseDTO = SimpleResponseDTO
                            .createWithType(msgBundle.getText("success.data.saved.code"),
                                            msgBundle.getText("success.data.saved"),
                                            msgBundle.getText("success.data.saved"));
        } catch(ConsoleException e) {
            responseDTO = ResponseDTO.create(e.getStatusCode(), e.getStatusMsg());
        }

        return ConsoleResponseEntity.get(responseDTO, HttpStatus.OK);
    }

    @PostMapping(value = "list/keyPair", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ConsoleResponseEntity<ResponseDTO> listKeyPair(@RequestPart(value = "pkcs12FilePassword") String pkcs12FilePassword,
                    @RequestPart(value = "pkcs12File") MultipartFile pkcs12File) {
        validations.assertNotBlank(pkcs12FilePassword, "pkcs12FilePassword");
        validations.assertNotNull(pkcs12File, "pkcs12File");

        ResponseDTO response;

        try {
            response = SimpleResponseDTO.createWithType(msgBundle.getText("success.data.loaded.code"),
                            msgBundle.getText("success.data.loaded"),
                            secureStoreService.listKey(pkcs12File.getName(), pkcs12FilePassword, pkcs12File.getBytes()));
        } catch(ConsoleException err) {
            response = ResponseDTO.create(err.getStatusCode(), err.getStatusMsg());
        } catch(IOException err) {
            response = ResponseDTO.create(msgBundle.getText("file.upload.failed.code"),
                            msgBundle.getText("file.upload.failed"));
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @PostMapping(value = "importPkcs12KeyPair", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ConsoleResponseEntity<ResponseDTO> importPkcs12KeyPair(@RequestPart(value = "pkcs12FilePassword") String pkcs12FilePassword,
                    @RequestPart(value = "aliasEntries") String aliasEntries,
                    @RequestPart(value = "targetStore") String targetStore,
                    @RequestPart(value = "overwrite") String overwrite,
                    @RequestPart(value = "pkcs12File") MultipartFile pkcs12File) {
        validations.assertNotBlank(pkcs12FilePassword, "pkcs12FilePassword");
        validations.assertNotBlank(targetStore, "targetStore");
        validations.assertNotBlank(aliasEntries, "aliasEntries");
        validations.assertNotNull(pkcs12File, "pkcs12File");

        ResponseDTO response;

        try {
            String[] entries = aliasEntries.split(";");
            Map<String, String> entryMap = new HashMap<>();
            for(String entry : entries) {
                String[] fromTo = entry.split("::");
                if(fromTo .length == 2) {
                    entryMap.put(fromTo[0], fromTo[1]);
                }
            }

            LOGGER.info("Entry to import = {}", entryMap.size());
            LOGGER.info("File name = {}", pkcs12File.getName());

            if (secureStoreService.importKey(pkcs12File.getName(), pkcs12FilePassword, entryMap,
                            Boolean.valueOf(overwrite), pkcs12File.getBytes(), targetStore)) {
                sysConfigService.sendSecureStoreRefreshRequest();
                response = SimpleResponseDTO
                                .createWithType(msgBundle.getText("success.data.saved.code"),
                                                msgBundle.getText("success.data.saved"),
                                                msgBundle.getText("success.data.saved"));
            } else {
                response = SimpleResponseDTO
                                .createWithType(msgBundle.getText("task.execution.error.code"),
                                                msgBundle.getText("task.execution.error"),
                                                msgBundle.getText("task.execution.error"));
            }
        } catch(ConsoleException err) {
            response = ResponseDTO.create(err.getStatusCode(), err.getStatusMsg());
        } catch(IOException err) {
            response = ResponseDTO.create(msgBundle.getText("file.upload.failed.code"),
                            msgBundle.getText("file.upload.failed"));
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @PostMapping(value = "importPemKeyPair", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ConsoleResponseEntity<ResponseDTO> importPemKeyPair(@RequestPart(value = "alias") String alias,
                    @RequestPart(value = "targetStore") String targetStore,
                    @RequestPart(value = "overwrite") String overwrite,
                    @RequestPart(value = "certificateFile") MultipartFile certificateFile,
                    @RequestPart(value = "privateKeyFile") MultipartFile privateKeyFile) {
        validations.assertNotBlank(targetStore, "targetStore");
        validations.assertNotBlank(alias, "alias");
        validations.assertNotNull(certificateFile, "certificateFile");
        validations.assertNotNull(privateKeyFile, "privateKeyFile");

        ResponseDTO response;

        try {
            LOGGER.info("Certificate file name = {}", certificateFile.getName());

            if (secureStoreService.importKey(certificateFile.getBytes(), privateKeyFile.getBytes(),
                    Boolean.valueOf(overwrite), targetStore, alias)) {
                sysConfigService.sendSecureStoreRefreshRequest();
                response = SimpleResponseDTO
                        .createWithType(msgBundle.getText("success.data.saved.code"),
                                msgBundle.getText("success.data.saved"),
                                msgBundle.getText("success.data.saved"));
            } else {
                response = SimpleResponseDTO
                        .createWithType(msgBundle.getText("task.execution.error.code"),
                                msgBundle.getText("task.execution.error"),
                                msgBundle.getText("task.execution.error"));
            }
        } catch(ConsoleException err) {
            response = ResponseDTO.create(err.getStatusCode(), err.getStatusMsg());
        } catch(IOException err) {
            response = ResponseDTO.create(msgBundle.getText("file.upload.failed.code"),
                    msgBundle.getText("file.upload.failed"));
        }

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return LOGGER;
    }
}
