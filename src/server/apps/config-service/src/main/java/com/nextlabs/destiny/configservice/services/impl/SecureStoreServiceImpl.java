package com.nextlabs.destiny.configservice.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.configservice.entities.SecureStore;
import com.nextlabs.destiny.configservice.repositories.SecureStoreRepository;
import com.nextlabs.destiny.configservice.services.SecureStoreService;

/**
 * Secure store service implementation.
 *
 * @author Chok Shah Neng
 */
@Service
public class SecureStoreServiceImpl
        implements SecureStoreService {

    @Autowired
    private SecureStoreRepository secureStoreRepository;

    @Override
    public byte[] getStoreZip() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (SecureStore secureStore : secureStoreRepository.findAll()) {
                if (secureStore.getStoreFile() != null) {
                    ZipEntry zipEntry = new ZipEntry(secureStore.getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(secureStore.getStoreFile());
                    zipOutputStream.flush();
                }
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

}
