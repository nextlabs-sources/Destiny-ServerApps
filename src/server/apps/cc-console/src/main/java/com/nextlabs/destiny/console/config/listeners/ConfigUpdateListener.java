package com.nextlabs.destiny.console.config.listeners;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.stereotype.Component;

/**
 * Listen for configuration updates and perform configuration refresh.
 *
 * @author Sachindra Dasun
 */
@Component
public class ConfigUpdateListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUpdateListener.class);
    private static final String CONFIG_UPDATED_MESSAGE = "CONFIG_UPDATED";

    @Autowired
    private ContextRefresher contextRefresher;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ActiveMQTextMessage) {
                ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
                if (textMessage.getText() != null && textMessage.getText().contains(CONFIG_UPDATED_MESSAGE)) {
                    LOGGER.info("Configuration update message received: {}", textMessage.getText());
                    contextRefresher.refresh();
                }
            }
        } catch (JMSException e) {
            LOGGER.error("Error in getting config update text message", e);
        } catch (Exception e) {
            LOGGER.error("Error in refreshing configurations", e);
        }
    }

}
