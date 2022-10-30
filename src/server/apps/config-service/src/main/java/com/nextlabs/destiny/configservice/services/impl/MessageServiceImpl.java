package com.nextlabs.destiny.configservice.services.impl;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.configservice.services.MessageService;

/**
 * Message service implementation.
 *
 * @author Sachindra Dasun
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);
    private static final String CONFIG_UPDATED_MESSAGE = "CONFIG_UPDATED";
    private static final String LOGGER_UPDATED_MESSAGE = "LOGGER_UPDATED";
    private static final String SECURE_STORE_UPDATED_MESSAGE = "SECURE_STORE_UPDATED";

    @Value("${configservice.jms.config.update.topic:jms/cc.%application.name%.config.update}")
    private String configUpdateTopic;

    @Value("${configservice.jms.logger.update.topic:jms/cc.%application.name%.logger.update}")
    private String loggerUpdateTopic;

    @Value("${configservice.jms.securestore.update.topic:jms/cc.%application.name%.securestore.update}")
    private String secureStoreUpdateTopic;

    @Value("${client.applications}")
    private String clientApplications;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void sendConfigRefresh(Set<String> applications) {
        if (applications.contains("application")) {
            applications = Arrays.stream(clientApplications.split(",")).map(String::trim).collect(Collectors.toSet());
        }
        applications.forEach(application -> {
            String topicName = configUpdateTopic.replace("%application.name%", application.replace("-", ""));
            String message = String.format("%d:%s", System.currentTimeMillis(), CONFIG_UPDATED_MESSAGE);
            jmsTemplate.convertAndSend(topicName, message);
            LOGGER.info("Configuration updated message: {} sent to topic: {}", message, topicName);
        });
    }

    @Override
    public void sendLoggerRefresh(Set<String> applications) {
        if (applications == null || applications.isEmpty() || applications.contains("application")) {
            applications = Arrays.stream(clientApplications.split(",")).map(String::trim).collect(Collectors.toSet());
        }
        applications.forEach(application -> {
            String topicName = loggerUpdateTopic.replace("%application.name%", application.replace("-", ""));
            String message = String.format("%d:%s", System.currentTimeMillis(), LOGGER_UPDATED_MESSAGE);
            jmsTemplate.convertAndSend(topicName, message);
            LOGGER.info("Logger updated message: {} sent to topic: {}", message, topicName);
        });
    }

    @Override
    public void sendSecureStoreRefresh(Set<String> applications) {
        if (applications == null || applications.isEmpty() || applications.contains("application")) {
            applications = Arrays.stream(clientApplications.split(",")).map(String::trim).collect(Collectors.toSet());
        }
        applications.forEach(application -> {
            String topicName = secureStoreUpdateTopic.replace("%application.name%", application.replace("-", ""));
            String message = String.format("%d:%s", System.currentTimeMillis(), SECURE_STORE_UPDATED_MESSAGE);
            jmsTemplate.convertAndSend(topicName, message);
            LOGGER.info("Secure store updated message: {} sent to topic: {}", message, topicName);
        });
    }
}
