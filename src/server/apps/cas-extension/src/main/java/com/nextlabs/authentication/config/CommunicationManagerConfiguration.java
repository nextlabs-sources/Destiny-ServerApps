package com.nextlabs.authentication.config;

import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.notifications.push.NotificationSender;
import org.apereo.cas.notifications.sms.SmsSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import com.nextlabs.authentication.handlers.CommunicationManager;

/**
 * Communication manager configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class CommunicationManagerConfiguration {

    @Bean
    public CommunicationsManager communicationsManager(ObjectProvider<SmsSender> smsSender,
                                                       ObjectProvider<JavaMailSender> mailSender,
                                                       ObjectProvider<NotificationSender> notificationSender) {
        return new CommunicationManager(smsSender.getIfAvailable(), mailSender.getIfAvailable(), notificationSender.getIfAvailable());
    }

}
