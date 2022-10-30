package com.nextlabs.authentication.config;

import com.warrenstrange.googleauth.IGoogleAuthenticator;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.otp.repository.credentials.OneTimeTokenCredentialRepository;
import org.apereo.cas.otp.repository.token.OneTimeTokenRepository;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nextlabs.authentication.handlers.gauth.GoogleAuthOneTimeTokenCredentialRepository;
import com.nextlabs.authentication.handlers.gauth.GoogleAuthOneTimeTokenRepository;

/**
 * Google Authenticator configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
@EnableConfigurationProperties(CasConfigurationProperties.class)
@ConditionalOnProperty(value = "mfa.gauth.enabled", havingValue = "true")
public class GoogleAuthConfiguration {

    @Bean
    public OneTimeTokenCredentialRepository googleAuthenticatorAccountRegistry(@Qualifier("googleAuthenticatorInstance") IGoogleAuthenticator googleAuthenticatorInstance,
                                                                               @Qualifier("googleAuthenticatorAccountCipherExecutor")
                                                                                               CipherExecutor googleAuthenticatorAccountCipherExecutor) {
        return new GoogleAuthOneTimeTokenCredentialRepository(googleAuthenticatorAccountCipherExecutor, googleAuthenticatorInstance);
    }

    @Bean
    public OneTimeTokenRepository oneTimeTokenAuthenticatorTokenRepository(CasConfigurationProperties casConfigurationProperties) {
        return new GoogleAuthOneTimeTokenRepository(
                casConfigurationProperties.getAuthn().getMfa().getGauth().getTimeStepSize()
        );
    }

}
