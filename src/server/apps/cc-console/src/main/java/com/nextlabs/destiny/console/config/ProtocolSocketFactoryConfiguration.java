package com.nextlabs.destiny.console.config;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.axis2.transport.http.security.SSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nextlabs.destiny.console.config.properties.KeyStoreProperties;
import com.nextlabs.destiny.console.config.properties.TrustStoreProperties;

/**
 * Configure protocol socket factory.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class ProtocolSocketFactoryConfiguration {

    private static final String DCC_KEY_ALIAS = "dcc";
    private static final String PROTOCOL = "TLSv1.2";
    @Value("${cc.home}")
    private String ccHome;

    @Bean(name = "dccSSLProtocolSocketFactory")
    public ProtocolSocketFactory dccSSLProtocolSocketFactory(KeyStoreProperties keyStoreProperties,
                                                             TrustStoreProperties trustStoreProperties)
            throws CertificateException,
            UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException,
            IOException {
        char[] keyStorePassword = keyStoreProperties.getPassword().toCharArray();
        KeyStore keyStore = KeyStore.getInstance(keyStoreProperties.getType().toLowerCase());
        try (InputStream inputStream =
                     Files.newInputStream(Paths.get(ccHome, "server", "certificates", "dcc-keystore.p12"),
                             StandardOpenOption.READ)) {
            keyStore.load(inputStream, keyStorePassword);
        }
        KeyStore dccKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        dccKeyStore.load(null, keyStorePassword);
        dccKeyStore.setKeyEntry(DCC_KEY_ALIAS, keyStore.getKey(DCC_KEY_ALIAS, keyStorePassword), keyStorePassword,
                keyStore.getCertificateChain(DCC_KEY_ALIAS));
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(dccKeyStore, keyStorePassword);

        KeyStore trustStore = KeyStore.getInstance(keyStoreProperties.getType().toLowerCase());
        try (InputStream inputStream =
                     Files.newInputStream(Paths.get(ccHome, "server", "certificates", "dcc-truststore.p12"),
                             StandardOpenOption.READ)) {
            trustStore.load(inputStream, trustStoreProperties.getPassword().toCharArray());
        }
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return new SSLProtocolSocketFactory(sslContext);
    }

}
