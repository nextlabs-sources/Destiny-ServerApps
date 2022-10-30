package com.nextlabs.destiny.console.services.tool.impl;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.tool.EnrollmentValidationService;
import com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants;
import com.nextlabs.destiny.console.validators.EnrollmentValidationHelper;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLHandshakeException;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.*;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.APPLICATION_KEY;

@Service
public class AzureEnrollmentValidationServiceImpl implements EnrollmentValidationService {

    private static final Set<String> MANDATORY_PROPERTIES = new HashSet<>();
    private static final String GRAPH = "https://graph.microsoft.com/.default";

    static {
        MANDATORY_PROPERTIES.add(EnrollmentConstants.OAUTH_AUTHORITY);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.TENANT);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.APPLICATION_KEY);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.APPLICATION_ID);
    }

    private MessageBundleService msgBundle;

    private EnrollmentValidationHelper enrollmentValidationHelper;

    @Override
    public boolean isEnrollmentType(EnrollmentType enrollmentType) {
        return enrollmentType == EnrollmentType.AZURE_ACTIVE_DIRECTORY;
    }

    @Override
    public boolean isValid(EnrollmentDTO enrollmentDTO, ConstraintValidatorContext context) {
        boolean isRequiredFieldsValid = enrollmentValidationHelper.validateRequiredFields(enrollmentDTO, MANDATORY_PROPERTIES, context, "enrollment.invalid.field");
        if (!isRequiredFieldsValid) {
            return false;
        }
        try {
            authenticate(enrollmentDTO);
        } catch (ConsoleException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    public IAuthenticationResult authenticate(EnrollmentDTO enrollmentDTO) throws ConsoleException {
        String authority = enrollmentDTO.getEnrollmentPropertyValue(OAUTH_AUTHORITY);
        String tenant = enrollmentDTO.getEnrollmentPropertyValue(TENANT);
        String applicationId = enrollmentDTO.getEnrollmentPropertyValue(APPLICATION_ID);
        String applicationKey = enrollmentDTO.getEnrollmentPropertyValue(APPLICATION_KEY);

        IAuthenticationResult result;
        String authorizationURL = null;

        try {
            if (!authority.endsWith("/")) {
                authority = String.format("%s/", authority);
            }
            authorizationURL = authority + tenant + "/oauth2/authorize";

            ConfidentialClientApplication clientApplication = ConfidentialClientApplication
                            .builder(applicationId,
                                            ClientCredentialFactory.createFromSecret(applicationKey))
                            .authority(authorizationURL)
                            .build();

            ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                            Collections.singleton(GRAPH))
                            .build();

            CompletableFuture<IAuthenticationResult> future = clientApplication.acquireToken(clientCredentialParam);
            result = future.get(30, TimeUnit.SECONDS);

            if (result == null) {
                throw new ConsoleException(msgBundle.getText("enrollment.aad.auth.failure"));
            }
        } catch (TimeoutException e) {
            throw new ConsoleException(msgBundle.getText("enrollment.aad.request.timeout", authorizationURL), e);
        } catch (MalformedURLException e) {
            throw new ConsoleException(msgBundle.getText("enrollment.aad.malformed.url", authorizationURL), e);
        } catch (Exception e) {
            if (e.getCause() instanceof AuthenticationException) {
                throw new ConsoleException(msgBundle.getText("enrollment.aad.auth.failure"), e);
            } else if (e.getCause() instanceof UnknownHostException) {
                throw new ConsoleException(msgBundle.getText("enrollment.unknown.host"), e);
            } else if (e.getCause() instanceof SSLHandshakeException) {
                throw new ConsoleException(msgBundle.getText("enrollment.aad.ssl.error"), e);
            }
            throw new ConsoleException("Unhandled exception while authenticating with Azure AD", e);
        }
        return result;
    }

    @Autowired
    public void setMsgBundle(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }

    @Autowired
    public void setEnrollmentValidationHelper(EnrollmentValidationHelper enrollmentValidationHelper) {
        this.enrollmentValidationHelper = enrollmentValidationHelper;
    }
}
