package com.nextlabs.authentication.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.core.util.EncryptionJwtSigningJwtCryptographyProperties;
import org.apereo.cas.configuration.model.support.pm.PasswordManagementProperties;
import org.apereo.cas.pm.PasswordResetTokenCipherExecutor;
import org.apereo.cas.pm.impl.history.AmnesiacPasswordHistoryService;
import org.apereo.cas.ticket.TicketFactory;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.apereo.cas.notifications.CommunicationsManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nextlabs.authentication.handlers.PasswordManagementHandler;
import com.nextlabs.authentication.handlers.actions.PasswordResetInstructionsAction;

/**
 * Password management configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class PasswordConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;
    @Autowired
    @Qualifier("defaultTicketFactory")
    private TicketFactory ticketFactory;
    @Autowired
    private TicketRegistry ticketRegistry;

    @Bean
    public org.apereo.cas.pm.PasswordManagementService passwordChangeService() {
        return new PasswordManagementHandler(casProperties.getAuthn().getPm(),
                passwordManagementCipherExecutor(),
                casProperties.getServer().getPrefix(),
                new AmnesiacPasswordHistoryService());
    }

    @Bean
    public CipherExecutor passwordManagementCipherExecutor() {
        final PasswordManagementProperties pm = casProperties.getAuthn().getPm();
        final EncryptionJwtSigningJwtCryptographyProperties crypto = pm.getReset().getCrypto();
        if (pm.isEnabled() && crypto.isEnabled()) {
            return new PasswordResetTokenCipherExecutor(
                    crypto.getEncryption().getKey(),
                    crypto.getSigning().getKey(),
                    crypto.getAlg(),
                    crypto.getSigning().getKeySize(),
                    crypto.getEncryption().getKeySize());
        }
        return CipherExecutor.noOp();
    }

    @Bean(name = "sendPasswordResetInstructionsAction")
    public PasswordResetInstructionsAction sendPasswordResetAction(ObjectProvider<CommunicationsManager> communicationsManager,
                                                                   ObjectProvider<PasswordManagementHandler> passwordManagementService) {
        return new PasswordResetInstructionsAction(casProperties, communicationsManager.getIfAvailable(),
                passwordManagementService.getIfAvailable(), ticketRegistry, ticketFactory);
    }

}
