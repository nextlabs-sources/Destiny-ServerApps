package com.nextlabs.destiny.console.config;

import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.console.config.listeners.ConfigUpdateListener;
import com.nextlabs.destiny.console.config.listeners.LoggerUpdateListener;
import com.nextlabs.destiny.console.config.properties.ActiveMQConnectionFactoryProperties;

/**
 * Create ActiveMQ connection and subscribe to messages received to console configuration update topic.
 * When a configuration update message is received, the configurations will be refreshed using the configuration
 * service. The additional parameters of the configuration factory can be customized by creating properties with the
 * prefix "config.activeMQConnectionFactory".
 *
 * @author Sachindra Dasun
 */
@Component
public class ConfigRefresher implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRefresher.class);

    @Value("${config.activeMQConnectionFactory.brokerURL}")
    String brokerUrl;

    @Value("${activemq.broker.ssl.enabled}")
    private boolean brokerSslEnabled;

    @Value("${config.update.refresher.enabled}")
    private boolean configUpdateRefresherEnabled;

    @Value("${logger.update.refresher.enabled}")
    private boolean loggerUpdateRefresherEnabled;

    @Value("${jms.config.update.topic:jms/cc.console.config.update}")
    private String configUpdateTopic;

    @Value("${jms.logger.update.topic:jms/cc.console.logger.update}")
    private String loggerUpdateTopic;

    @Value("${spring.application.name}")
    private String application;

    @Autowired
    private ConfigUpdateListener configUpdateListener;

    @Autowired
    private LoggerUpdateListener loggerUpdateListener;

    private ActiveMQConnectionFactoryProperties activeMQConnectionFactoryProperties;
    private Connection connection;
    private Session session;
    private MessageConsumer configUpdateConsumer;
    private MessageConsumer loggerUpdateConsumer;

    public ConfigRefresher(ActiveMQConnectionFactoryProperties activeMQConnectionFactoryProperties) {
        this.activeMQConnectionFactoryProperties = activeMQConnectionFactoryProperties;
    }

    public void run() {
        LOGGER.info("Starting refresher: Configurations={}, Loggers={}", configUpdateRefresherEnabled,
                loggerUpdateRefresherEnabled);
        if (brokerUrl == null || brokerUrl.isEmpty()) {
            LOGGER.info("ActiveMQ broker URL is empty and refresher will not be started.");
            return;
        }
        try {
            ActiveMQConnectionFactory connectionFactory = brokerSslEnabled ?
                    new ActiveMQSslConnectionFactory() : new ActiveMQConnectionFactory();

            customizeActiveMQConnectionFactory(connectionFactory);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            if (configUpdateRefresherEnabled) {
                createConfigUpdateConsumer(session, configUpdateListener);
            }
            if (loggerUpdateRefresherEnabled) {
                createLoggerUpdateConsumer(session, loggerUpdateListener);
            }
        } catch (JMSException e) {
            LOGGER.error(String.format("Error in initializing configuration refresher. Configuration changes will not" +
                    " be refreshed. Please check if application can connect to the ActiveMQ broker at %s and restart " +
                    "the application %s to retry.", brokerUrl, application), e);
        }
        LOGGER.info("Configuration refresher started and listening for configuration updates.");
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

    private void createConfigUpdateConsumer(Session session, MessageListener configUpdateListener) throws JMSException {
        this.configUpdateConsumer = session.createConsumer(session.createTopic(configUpdateTopic));
        this.configUpdateConsumer.setMessageListener(configUpdateListener);
    }

    private void createLoggerUpdateConsumer(Session session, MessageListener loggerUpdateListener) throws JMSException {
        this.loggerUpdateConsumer = session.createConsumer(session.createTopic(loggerUpdateTopic));
        this.loggerUpdateConsumer.setMessageListener(loggerUpdateListener);
    }

    @PreDestroy
    void close() {
        if (configUpdateConsumer != null) {
            try {
                configUpdateConsumer.close();
            } catch (JMSException e) {
                LOGGER.error("Error in closing config update consumer.", e);
            }
        }

        if (loggerUpdateConsumer != null) {
            try {
                loggerUpdateConsumer.close();
            } catch (JMSException e) {
                LOGGER.error("Error in closing logger update consumer.", e);
            }
        }

        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                LOGGER.error("Error in closing the ActiveMQ session.", e);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                LOGGER.error("Error in closing the ActiveMQ connection.", e);
            }
        }
    }

}
