package com.nextlabs.authentication.handlers;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.model.support.email.EmailProperties;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.notifications.push.NotificationSender;
import org.apereo.cas.notifications.sms.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Communication manage implementation.
 *
 * @author Sachindra Dasun
 */
public class CommunicationManager extends CommunicationsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationManager.class);

    private final JavaMailSender mailSender;

    public CommunicationManager(SmsSender smsSender, JavaMailSender mailSender, NotificationSender notificationSender) {
        super(smsSender, mailSender, notificationSender);
        this.mailSender = mailSender;
    }

    @Override
    public boolean email(EmailProperties emailProperties, String to, String text) {
        try {
            String from = emailProperties.getFrom();
            String subject = emailProperties.getSubject();
            if (!isMailSenderDefined() || StringUtils.isBlank(text) || StringUtils.isBlank(from)
                    || StringUtils.isBlank(subject) || StringUtils.isBlank(to)) {
                LOGGER.warn("Could not send email to [{}] because either no address/subject/text is found or email settings are not configured.", to);
                return false;
            }

            final MimeMessage message = this.mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(to);
            helper.setText(text, true);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setPriority(1);

            String cc = emailProperties.getCc();
            if (StringUtils.isNotBlank(cc)) {
                helper.setCc(cc);
            }

            String bcc = emailProperties.getBcc();
            if (StringUtils.isNotBlank(bcc)) {
                helper.setBcc(bcc);
            }
            this.mailSender.send(message);
            return true;
        } catch (final Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return false;
    }

}
