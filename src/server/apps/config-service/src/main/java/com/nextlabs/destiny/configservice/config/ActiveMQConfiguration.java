package com.nextlabs.destiny.configservice.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslBrokerService;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.activemq.transport.TransportFactory;
import org.apache.activemq.transport.tcp.SslTransportFactory;
import org.apache.activemq.usage.SystemUsage;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nextlabs.destiny.configservice.config.properties.ActiveMQConnectionFactoryProperties;
import com.nextlabs.destiny.configservice.config.properties.BrokerServiceProperties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Configuration for creating embedded ActiveMQ broker and customize ActiveMQ connection factory.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class ActiveMQConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQConfiguration.class);
    private static final String CONTROL_CENTER_KEY_ALIAS = "control_center";
    private static final String PROTOCOL = "TLSv1.2";

    @Value("${activemq.broker.connector.bindAddress}")
    private String bindAddress;

    @Value("${activemq.broker.username}")
    private String username;

    @Value("${activemq.broker.password}")
    private String password;

    @Value("${activemq.broker.ssl.enabled}")
    private boolean brokerSslEnabled;

    @Value("${cc.home}")
    private String ccHome;

    @Value("${key.store.type}")
    private String keyStoreType;

    @Value("${key.store.password}")
    private String keyStorePassword;

    @Value("${trust.store.type}")
    private String trustStoreType;

    @Value("${trust.store.password}")
    private String trustStorePassword;

    private BrokerServiceProperties brokerServiceProperties;
    private ActiveMQConnectionFactoryProperties activeMQConnectionFactoryProperties;

    public ActiveMQConfiguration(BrokerServiceProperties brokerServiceProperties,
                                 ActiveMQConnectionFactoryProperties activeMQConnectionFactoryProperties) {
        this.brokerServiceProperties = brokerServiceProperties;
        this.activeMQConnectionFactoryProperties = activeMQConnectionFactoryProperties;
    }

    @Bean
    @ConditionalOnProperty(name = "activemq.broker.enabled")
    public BrokerService brokerService() throws Exception {
        if(brokerSslEnabled) {
            char[] keyStorePasswordCharArray = keyStorePassword.toCharArray();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType.toLowerCase());
            try (InputStream inputStream =
                         Files.newInputStream(Paths.get(ccHome, "server", "certificates", "dcc-keystore.p12"),
                                 StandardOpenOption.READ)) {
                keyStore.load(inputStream, keyStorePasswordCharArray);
            }
            KeyStore controlCenterKeyStore = KeyStore.getInstance(keyStoreType.toLowerCase());
            controlCenterKeyStore.load(null, keyStorePasswordCharArray);
            controlCenterKeyStore.setKeyEntry(CONTROL_CENTER_KEY_ALIAS, keyStore.getKey(CONTROL_CENTER_KEY_ALIAS, keyStorePasswordCharArray),
                    keyStorePasswordCharArray, keyStore.getCertificateChain(CONTROL_CENTER_KEY_ALIAS));
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(controlCenterKeyStore, keyStorePasswordCharArray);

            KeyStore trustStore = KeyStore.getInstance(trustStoreType.toLowerCase());
            try (InputStream inputStream =
                         Files.newInputStream(Paths.get(ccHome, "server", "certificates", "dcc-truststore.p12"),
                                 StandardOpenOption.READ)) {
                trustStore.load(inputStream, trustStorePassword.toCharArray());
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            SslBrokerService brokerService = new SslBrokerService();
            brokerService.setPersistent(false);
            brokerService.addSslConnector(bindAddress, keyManagers, trustManagers, null);
            customizeBrokerService(brokerService);

            SslTransportFactory sslFactory = new SslTransportFactory();
            SslContext sslContext = new SslContext(keyManagers, trustManagers, null);
            sslContext.setProtocol(PROTOCOL);
            SslContext.setCurrentSslContext(sslContext);
            TransportFactory.registerTransportFactory("ssl", sslFactory);

            setAuthenticationPlugin(brokerService);

            return brokerService;
        } else {
            BrokerService brokerService = new BrokerService();
            brokerService.addConnector(bindAddress);
            customizeBrokerService(brokerService);
            setAuthenticationPlugin(brokerService);

            return brokerService;
        }
    }

    private void customizeBrokerService(BrokerService brokerService) {
        for (Map.Entry<String, String> entry : brokerServiceProperties.getBrokerService().entrySet()) {
            try {
                BeanUtils.setProperty(brokerService, entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LOGGER.error(String.format("Error in setting BrokerService property %s to %s", entry.getKey(),
                        entry.getValue()), e);
            }
        }
    }

    private void setAuthenticationPlugin(BrokerService brokerService) {
        List<AuthenticationUser> users = new ArrayList<>();
        users.add(new AuthenticationUser(username, password, "users"));
        SimpleAuthenticationPlugin authenticationPlugin = new SimpleAuthenticationPlugin(users);
        brokerService.setPlugins(new BrokerPlugin[]{authenticationPlugin});
        SystemUsage systemUsage = brokerService.getSystemUsage();
        systemUsage.getTempUsage().setLimit(1024L * 1024 * 1024 * 5); // 5 GB
        systemUsage.getStoreUsage().setLimit(1024L * 1024 * 1024 * 5); // 5 GB
    }

    @Bean
    public ActiveMQConnectionFactoryCustomizer connectionFactoryCustomizer() {
        return this::customizeActiveMQConnectionFactory;
    }

    private void customizeActiveMQConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        for (Map.Entry<String, String> entry : activeMQConnectionFactoryProperties.getActiveMQConnectionFactory().entrySet()) {
            try {
                BeanUtils.setProperty(connectionFactory, entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LOGGER.error(String.format("Error in setting ActiveMQConnectionFactory property %s to %s", entry.getKey(),
                        entry.getValue()), e);
            }
        }
    }
}
