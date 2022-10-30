package com.nextlabs.destiny.console.services.enrollment.impl;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.NoSuchElementException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.enrollment.EnrollmentServiceStub;
import com.nextlabs.destiny.console.config.properties.EnrollmentServiceProperties;
import com.nextlabs.destiny.console.config.properties.ServiceUrlProperties;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.Component;
import com.nextlabs.destiny.console.repositories.ComponentRepository;
import com.nextlabs.destiny.console.services.enrollment.EnrollmentServiceWrapperService;

/**
 * Service implementation for accessing enrollment service.
 *
 * @author Sachindra Dasun.
 */
@Service
public class EnrollmentServiceWrapperServiceImpl implements EnrollmentServiceWrapperService {


    private static final String ENROLLMENT_SERVICE_URL_FORMAT = "%s/services/EnrollmentService";
    private static final int TIME_OUT_IN_MILLI_SECONDS = 120000;
    private static final String WS_SECURITY_HEADER_ELEMENT_NAME = "Security";
    private static final String WS_SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String WS_SECURITY_PASSWORD_ELEMENT_NAME = "Password";
    private static final String WS_SECURITY_TYPES_PREFIX = "wsse";
    private static final String WS_SECURITY_USERNAME_ELEMENT_NAME = "Username";
    private static final String WS_SECURITY_USERNAME_TOKEN_ELEMENT_NAME = "UsernameToken";
    @Autowired
    EnrollmentServiceProperties enrollmentServiceProperties;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    @Qualifier("dccSSLProtocolSocketFactory")
    private ProtocolSocketFactory protocolSocketFactory;
    @Autowired
    private ServiceUrlProperties serviceUrlProperties;

    @Override
    public void remove(String domainName) throws ConsoleException {
        try {
            getEnrollmentService().deleteRealmByName(domainName);
        } catch (Exception e) {
            throw new ConsoleException("Enrollment error", e);
        }
    }

    @Override
    public void sync(String domainName) throws ConsoleException {
        try {
            getEnrollmentService().enrollRealmAsync(domainName);
        } catch (Exception e) {
            throw new ConsoleException("Enrollment error", e);
        }
    }

    @Override
    public void cancelAutoSync(String domainName) throws ConsoleException {
        try {
            getEnrollmentService().cancelAutoSyncForRealm(domainName);
        } catch (Exception e) {
            throw new ConsoleException("Enrollment error", e);
        }
    }


    private EnrollmentServiceStub getEnrollmentService() throws AxisFault {
        String enrollmentServiceUrl = getEnrollmentServiceUrl();
        EnrollmentServiceStub enrollmentServiceStub = new EnrollmentServiceStub(null, enrollmentServiceUrl);
        enrollmentServiceStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(TIME_OUT_IN_MILLI_SECONDS);
        enrollmentServiceStub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, TIME_OUT_IN_MILLI_SECONDS);
        enrollmentServiceStub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, TIME_OUT_IN_MILLI_SECONDS);
        URI enrollmentServiceUri = URI.create(enrollmentServiceUrl);
        enrollmentServiceStub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER,
                new Protocol(enrollmentServiceUri.getScheme(), protocolSocketFactory,
                        enrollmentServiceUri.getPort()));
        addAuthenticationHeader(enrollmentServiceStub);
        return enrollmentServiceStub;
    }

    private String getEnrollmentServiceUrl() {
        String url = serviceUrlProperties.getDem();
        if (StringUtils.isEmpty(url)) {
            url = componentRepository.findByType(ServerComponentType.DEM.getName())
                    .map(Component::getComponentUrl)
                    .orElseThrow(NoSuchElementException::new);
        } else {
            url = url.replaceAll("/$", "");
        }
        return String.format(ENROLLMENT_SERVICE_URL_FORMAT, url);
    }

    private void addAuthenticationHeader(EnrollmentServiceStub enrollmentServiceStub) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement secureSessionElement = factory.createOMElement(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY_HEADER_ELEMENT_NAME, WS_SECURITY_TYPES_PREFIX), null);

        OMElement usernameTokenElement = factory.createOMElement(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY_USERNAME_TOKEN_ELEMENT_NAME, WS_SECURITY_TYPES_PREFIX), null);

        OMElement usernameElement = factory.createOMElement(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY_USERNAME_ELEMENT_NAME, WS_SECURITY_TYPES_PREFIX), null);
        usernameElement.setText(enrollmentServiceProperties.getClientId());

        OMElement passwordElement = factory.createOMElement(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY_PASSWORD_ELEMENT_NAME, WS_SECURITY_TYPES_PREFIX), null);
        passwordElement.setText(enrollmentServiceProperties.getClientSecret());

        usernameTokenElement.addChild(usernameElement);
        usernameTokenElement.addChild(passwordElement);

        secureSessionElement.addChild(usernameTokenElement);
        enrollmentServiceStub._getServiceClient().addHeader(secureSessionElement);
    }

}
