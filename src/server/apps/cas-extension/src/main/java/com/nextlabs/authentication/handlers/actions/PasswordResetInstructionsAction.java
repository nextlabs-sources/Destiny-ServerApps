package com.nextlabs.authentication.handlers.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.email.EmailProperties;
import org.apereo.cas.configuration.model.support.pm.PasswordManagementProperties;
import org.apereo.cas.ticket.TicketFactory;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.nextlabs.authentication.enums.InstallMode;

/**
 * Handle password reset action.
 *
 * @author Sachindra Dasun
 */
public class PasswordResetInstructionsAction extends org.apereo.cas.pm.web.flow.actions.SendPasswordResetInstructionsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetInstructionsAction.class);

    public PasswordResetInstructionsAction(CasConfigurationProperties casProperties, CommunicationsManager communicationsManager, org.apereo.cas.pm.PasswordManagementService passwordManagementService,
                    TicketRegistry ticketRegistry, TicketFactory ticketFactory) {
        super(casProperties, communicationsManager, passwordManagementService, ticketRegistry, ticketFactory);
    }

    @Override
    protected Event doExecute(RequestContext requestContext) {
        communicationsManager.validate();
        if (!communicationsManager.isMailSenderDefined()) {
            return error();
        }
        PasswordManagementProperties pm = casProperties.getAuthn().getPm();
        String usernameOrEmail = requestContext.getRequestParameters().get("username");
        if (StringUtils.isBlank(usernameOrEmail)) {
            LOGGER.warn("No username or email is provided");
            return error();
        }

        String to = passwordManagementService.findEmail(usernameOrEmail);
        if (StringUtils.isBlank(to)) {
            LOGGER.warn("Password reset request: no user account found");
            return success();
        }

        String username = passwordManagementService.findUsername(to);
        String url = buildPasswordResetUrl(username, passwordManagementService, casProperties,
                WebUtils.getService(requestContext));

        LOGGER.debug("Generated password reset URL [{}]; Link is only active for the next [{}] minute(s)", url,
                pm.getReset().getExpirationMinutes());
        if (sendPasswordResetEmailToAccount(to, url, username)) {
            return success();
        }
        LOGGER.error("Failed to notify account [{}]", to);
        return error();
    }

    @Override
    protected boolean sendPasswordResetEmailToAccount(String to, String url, String username) {
        try {
            EmailProperties resetEmailProperties = casProperties.getAuthn().getPm().getReset().getMail();
            Map<String, String> emailParameters = getEmailBodyParameters(url, username);
            String body = new StrSubstitutor(emailParameters)
                    .replace(resetEmailProperties.getText());
            resetEmailProperties.setSubject(String.format("%s: %s", emailParameters.get("service_name"),
                    resetEmailProperties.getSubject()));
            return this.communicationsManager.email(resetEmailProperties, to, body);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    private Map<String, String> getEmailBodyParameters(String url, String username) {
        Map<String, String> parameters = new HashMap<>();
        String installMode = System.getProperty("console.install.mode");
        parameters.put("url", url);
        parameters.put("username", username);
        parameters.put("mode", InstallMode.SAAS.name().equalsIgnoreCase(installMode) ? "CloudAz" : "Control Center");
        parameters.put("logo", InstallMode.SAAS.name().equalsIgnoreCase(installMode) ? "CloudAz-Logo-100.png" : "CC_LogoFS.png");
        parameters.put("service_name", InstallMode.SAAS.name().equalsIgnoreCase(installMode) ? "NextLabs CloudAz Service" :
                "NextLabs Control Center");
        parameters.put("server.prefix", casProperties.getServer().getPrefix());
        return parameters;
    }

}
