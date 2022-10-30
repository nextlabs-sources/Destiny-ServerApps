package com.nextlabs.destiny.console.services.impl;

import com.nextlabs.destiny.console.config.properties.RemoteEnvironmentProperties;
import com.nextlabs.destiny.console.enums.SecureStoreFile;
import com.nextlabs.destiny.console.services.SSLManagerService;
import com.nextlabs.destiny.console.services.SecureStoreService;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Service
public class SSLManagerServiceImpl implements SSLManagerService {

    private static final Logger logger = LoggerFactory.getLogger(SSLManagerServiceImpl.class);

    private final SecureStoreService secureStoreService;

    private final RemoteEnvironmentProperties remoteEnvironmentProperties;

    public SSLManagerServiceImpl(SecureStoreService secureStoreService, RemoteEnvironmentProperties remoteEnvironmentProperties) {
        this.secureStoreService = secureStoreService;
        this.remoteEnvironmentProperties = remoteEnvironmentProperties;
    }

    private SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        return SSLContexts.custom()
                .loadTrustMaterial(secureStoreService.getKeyStore(SecureStoreFile.CACERTS_TRUST_STORE), null)
                .build();
    }

    @Override
    public HttpComponentsClientHttpRequestFactory getRequestFactory() {
        try {
            SSLConnectionSocketFactory sslConnSocketFactory = new SSLConnectionSocketFactory(getSSLContext(), new DefaultHostnameVerifier());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslConnSocketFactory)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            HttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConnSocketFactory)
                    .setConnectionManager(poolingConnManager).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            requestFactory.setConnectTimeout(remoteEnvironmentProperties.getTimeout() * 1000);
            requestFactory.setReadTimeout(remoteEnvironmentProperties.getTimeout() * 1000);
            return requestFactory;
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e){
            logger.error("Error while setting up SSL for rest template", e);
        }
        return null;
    }
}
