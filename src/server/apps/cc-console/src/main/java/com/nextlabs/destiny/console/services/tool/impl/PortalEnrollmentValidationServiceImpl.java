package com.nextlabs.destiny.console.services.tool.impl;

import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.tool.EnrollmentValidationService;
import com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants;
import com.nextlabs.destiny.console.utils.enrollment.JCIFSNTLMScheme;
import com.nextlabs.destiny.console.validators.EnrollmentValidationHelper;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.impl.httpclient3.HttpTransportPropertiesImpl.Authenticator;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidatorContext;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortalEnrollmentValidationServiceImpl implements EnrollmentValidationService {

    private static final Logger log = LoggerFactory.getLogger(PortalEnrollmentValidationServiceImpl.class);

    private static final Set<String> MANDATORY_PROPERTIES = new HashSet<>();

    private static final Pattern PORTAL_URL_PATTERN = Pattern.compile("(https?\\\\:.*?):");

    static {
        MANDATORY_PROPERTIES.add(EnrollmentConstants.DOMAIN_PROPERTY);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.LOGIN_PROPERTY);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.PASSWORD_PROPERTY);
        MANDATORY_PROPERTIES.add(EnrollmentConstants.PORTALS_PROPERTY);
    }

    private MessageBundleService msgBundle;

    private EnrollmentValidationHelper enrollmentValidationHelper;

    @Override
    public boolean isEnrollmentType(EnrollmentType enrollmentType) {
        return enrollmentType == EnrollmentType.SHAREPOINT;
    }

    @Override
    public boolean isValid(EnrollmentDTO enrollmentDTO, ConstraintValidatorContext context) {
        boolean isRequiredFieldsValid = enrollmentValidationHelper.validateRequiredFields(enrollmentDTO, MANDATORY_PROPERTIES, context, "enrollment.invalid.field");
        if (!isRequiredFieldsValid) {
            return false;
        }
        try {
            testConnection(enrollmentDTO);
        } catch (ConsoleException e) {
            log.error("Invalid URL for portal enrollment", e);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private void testConnection(EnrollmentDTO enrollmentDTO) throws ConsoleException {
        String username = enrollmentDTO.getEnrollmentPropertyValue(EnrollmentConstants.LOGIN_PROPERTY);
        String password = enrollmentDTO.getEnrollmentPropertyValue(EnrollmentConstants.PASSWORD_PROPERTY);
        String domain = enrollmentDTO.getEnrollmentPropertyValue(EnrollmentConstants.DOMAIN_PROPERTY);
        Matcher matcher = PORTAL_URL_PATTERN.matcher(enrollmentDTO.getEnrollmentPropertyValue(EnrollmentConstants.PORTALS_PROPERTY));
        Set<String> portalUrls = new HashSet<>();
        while (matcher.find()) {
            portalUrls.add(matcher.group(1).replace("\\:", ":"));
        }
        if (portalUrls.isEmpty()) {
            throw new ConsoleException("Unable to find a valid portal URL in enrollment");
        }
        for (String portalUrl: portalUrls) {
            log.info(String.format("Validating portal enrollment for site %s", portalUrl));
            String hostDomain;
            try {
                hostDomain = getDomainName(portalUrl);
            } catch (MalformedURLException e) {
                throw new ConsoleException(msgBundle.getText("enrollment.portal.malformed.url", portalUrl));
            }
            AuthPolicy.registerAuthScheme(AuthPolicy.BASIC, JCIFSNTLMScheme.class);
            Authenticator authenticator = new Authenticator();
            List<String> authScheme = new ArrayList<>();
            authScheme.add(Authenticator.NTLM);
            authScheme.add(Authenticator.BASIC);
            authenticator.setAuthSchemes(authScheme);
            authenticator.setUsername(username);
            authenticator.setPassword(password);
            authenticator.setHost(hostDomain);
            authenticator.setDomain(domain);
            authenticator.setPreemptiveAuthentication(true);
            try {
                ServiceClient client = new ServiceClient();
                Options opts = new Options();
                opts.setTo(new EndpointReference(String.format("%s/_vti_bin/SiteData.asmx", portalUrl)));
                opts.setAction("http://schemas.microsoft.com/sharepoint/soap/GetSite");

                opts.setProperty(HTTPConstants.AUTHENTICATE, authenticator);
                opts.setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);
                opts.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
                opts.setUserName(String.format("%s\\%s", domain, username));
                opts.setPassword(password);
                client.setOptions(opts);
                    client.sendReceive(createPayLoad());
            } catch (AxisFault e) {
                log.error("Error while invoking sharepoint web service", e);
                if (e.getCause() instanceof UnknownHostException) {
                    throw new ConsoleException(msgBundle.getText("enrollment.portal.unknown.host", portalUrl));
                } else if (e.getCause() instanceof ConnectException) {
                    throw new ConsoleException(msgBundle.getText("enrollment.portal.connect.error", portalUrl));
                } else if (e.getMessage().contains("401 Error: Unauthorized")) {
                    throw new ConsoleException(msgBundle.getText("enrollment.portal.auth.failure"));
                }
            }
        }
    }

    private OMElement createPayLoad() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://schemas.microsoft.com/sharepoint/soap/", "soap");
        return fac.createOMElement("GetSite", omNs);
    }

    public String getDomainName(String url) throws MalformedURLException {
        if(!url.startsWith("http") && !url.startsWith("https")){
            url = "http://" + url;
        }
        URL netUrl = new URL(url);
        String host = netUrl.getHost();
        if(host.startsWith("www")){
            host = host.substring("www".length()+1);
        }
        return host;
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
