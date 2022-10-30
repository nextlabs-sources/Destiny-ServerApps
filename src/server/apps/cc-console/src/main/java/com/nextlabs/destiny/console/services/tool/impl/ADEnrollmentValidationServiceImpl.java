package com.nextlabs.destiny.console.services.tool.impl;

import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.tool.EnrollmentValidationService;
import com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants;
import com.nextlabs.destiny.console.validators.EnrollmentValidationHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.NamingSecurityException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.net.ssl.SSLSocketFactory;
import javax.validation.ConstraintValidatorContext;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.*;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.PASSWORD;

@Service
public class ADEnrollmentValidationServiceImpl implements EnrollmentValidationService {

    private static final Logger log = LoggerFactory.getLogger(ADEnrollmentValidationServiceImpl.class);

    private static final Set<String> MANDATORY_PROPERTIES = new HashSet<>();

    private MessageBundleService msgBundle;

    private EnrollmentValidationHelper enrollmentValidationHelper;

    static {
        MANDATORY_PROPERTIES.add(EnrollmentConstants.SERVER);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.PORT);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.LOGIN);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.PASSWORD);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.ROOTS);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.STATIC_ID_ATTRIBUTE);
    }

    @Override
    public boolean isEnrollmentType(EnrollmentType enrollmentType) {
        return enrollmentType == EnrollmentType.ACTIVE_DIRECTORY;
    }

    @Override
    public boolean isValid(EnrollmentDTO enrollmentDTO, ConstraintValidatorContext context) {
        boolean isRequiredFieldsValid = enrollmentValidationHelper.validateRequiredFields(enrollmentDTO, MANDATORY_PROPERTIES, context, "enrollment.invalid.field");
        if (!isRequiredFieldsValid) {
            return false;
        }

        String errorMsg = null;
        try {
            int port = Integer.parseInt(enrollmentDTO.getEnrollmentPropertyValue(EnrollmentConstants.PORT));
            if (port < 1 || port > 65535) {
                errorMsg = msgBundle.getText("enrollment.ad.invalid.port");
            }
        } catch (NumberFormatException e) {
            errorMsg = msgBundle.getText("enrollment.ad.invalid.port");
        }
        if (errorMsg != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMsg)
                    .addConstraintViolation();
            return false;
        }
        try {
            for (String root : enrollmentDTO.getEnrollmentPropertyValue(ROOTS).split(":")) {
                if (StringUtils.isBlank(root)) {
                    continue;
                }
                setLdapTemplate(enrollmentDTO, root);
            }
        } catch (NamingSecurityException e) {
            errorMsg = msgBundle.getText("enrollment.ad.auth.failure");
        } catch (InvalidNameException e) {
            errorMsg = e.getExplanation();
        } catch (NamingException e) {
            if (e.getRootCause() instanceof UnknownHostException) {
                errorMsg = msgBundle.getText("enrollment.unknown.host");
            } else if (e.getRootCause() instanceof ConnectException) {
                errorMsg = msgBundle.getText("enrollment.ad.connect.error");
            } else {
                log.error("Error occurred while validating LDAP connection", e);
                errorMsg = ExceptionUtils.getRootCauseMessage(e);
            }
        } catch (Exception e) {
            errorMsg = ExceptionUtils.getRootCauseMessage(e);
        }
        if (errorMsg != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMsg)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private LdapTemplate setLdapTemplate(EnrollmentDTO enrollment, String root) {
        LdapContextSource ctxSrc = new LdapContextSource();
        LdapTemplate tmpl;
        boolean isSecure = SSL_TRANSPORT_MODE.equals(enrollment.getEnrollmentPropertyValue(SECURE_TRANSPORT_MODE));
        ctxSrc.setUrl((isSecure? "ldaps://" : "ldap://") + enrollment.getEnrollmentPropertyValue(SERVER) + ":"
                + Integer.parseInt(enrollment.getEnrollmentPropertyValue(PORT)));
        ctxSrc.setBase(root);
        ctxSrc.setUserDn(enrollment.getEnrollmentPropertyValue(LOGIN));
        ctxSrc.setPassword(enrollment.getEnrollmentPropertyValue(PASSWORD));

        final Map<String, Object> envProps = new HashMap<>();
        envProps.put("java.naming.ldap.attributes.binary","objectSid objectGUID");
        if(isSecure) {
            envProps.put(Context.SECURITY_PROTOCOL, "ssl");
            envProps.put("java.naming.ldap.ref.separator", ":");
            envProps.put("java.naming.ldap.factory.socket", SSLSocketFactory.class.getName());
        }
        ctxSrc.setBaseEnvironmentProperties(envProps);
        ctxSrc.afterPropertiesSet(); // this method should be called.

        tmpl = new LdapTemplate(ctxSrc);
        tmpl.getContextSource().getContext(enrollment.getEnrollmentPropertyValue(LOGIN),
                enrollment.getEnrollmentPropertyValue(PASSWORD));
        return tmpl;
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
