package com.nextlabs.authentication.config.listeners;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.logmanager.LogManagerClient;

/**
 * Listen for logger configuration updates and perform logger configuration refresh.
 *
 * @author Sachindra Dasun
 */
@Component
public class LoggerUpdateListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerUpdateListener.class);
    private static final String LOGGER_UPDATED_MESSAGE = "LOGGER_UPDATED";

    @Value("${spring.application.name}")
    private String application;

    @Value("${spring.cloud.config.uri}")
    private String configServiceUrl;

    @Value("${spring.cloud.config.username}")
    private String configServiceUsername;

    @Value("${spring.cloud.config.password}")
    private String configServicePassword;

    @Autowired
    private TextEncryptor textEncryptor;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ActiveMQTextMessage) {
                ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
                if (textMessage.getText() != null && textMessage.getText().contains(LOGGER_UPDATED_MESSAGE)) {
                    LOGGER.info("Logger update message received: {}", textMessage.getText());
                    LogManagerClient.refresh(configServiceUrl, configServiceUsername,
                            textEncryptor.encrypt(configServicePassword));
                }
            }
        } catch (JMSException e) {
            LOGGER.error("Error in getting logger update text message", e);
        } catch (Exception e) {
            LOGGER.error("Error in refreshing logger configurations", e);
        }
    }

}
