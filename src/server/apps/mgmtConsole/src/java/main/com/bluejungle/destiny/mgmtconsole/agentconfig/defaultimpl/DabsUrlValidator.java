/*
 * Created on Oct 31, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.destiny.services.ping.PingServiceStub;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import java.net.URL;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * A Java Server Faces validator that will check the validity of a DABS URL
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/DabsUrlValidator.java#1 $
 */

public class DabsUrlValidator implements Validator {
    private static final Log LOG = LogFactory.getLog(DabsUrlValidator.class.getName());
    
    private static final String PING_SERVICE_LOCATION_SERVLET_PATH = "/services/PingService";
    private static final String VALIDATION_ERROR_DETAILED_MESSAGE_KEY = "agent_config_server_invalid_dabs_url_error_message_detail";
    private static final String VALIDATION_ERROR_SUMMARY;

    private PingServiceStub pingService;

    static {
        ResourceBundle commonBundle = ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME);
        VALIDATION_ERROR_SUMMARY = commonBundle.getString("conversion_failed_message_summary");
    }

    /**
     * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.Object)
     */
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!(value instanceof URL)) {
            throw new IllegalStateException("Unknow value type, " + value.getClass().getName() + ".  Expecting java.net.URL");
        }

        boolean isValid = false;

        try {
            PingServiceStub pingService = getPingService((URL) value);
            String systemTime = String.valueOf(System.currentTimeMillis());
            String echoedPingData = pingService.ping(systemTime);
            isValid = systemTime.equals(echoedPingData);
        }  catch (RemoteException exception) {
            Log log = getLog();
            log.warn("Invalid DABS Broker URL: " + value.toString());
            log.debug("Logging throwable at debug level", exception);
            isValid = false;
        }

        if (!isValid) {
            String validationErrorDetail = getValidationErrorDetail((URL) value);
            FacesMessage validationFailedMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, VALIDATION_ERROR_SUMMARY, validationErrorDetail);
            throw new ValidatorException(validationFailedMessage);
        }
    }

    /**
     * Retrieve the detailed portion of the validation error message based on
     * the provided invalid URL
     * 
     * @param url
     * @return
     */
    private String getValidationErrorDetail(URL url) {
        ResourceBundle mgmtConsoleBundle = ResourceBundle.getBundle(com.bluejungle.destiny.mgmtconsole.CommonConstants.MGMT_CONSOLE_BUNDLE_NAME);
        String detailMessage = mgmtConsoleBundle.getString(VALIDATION_ERROR_DETAILED_MESSAGE_KEY);
        return MessageFormat.format(detailMessage, new Object[] { url.toString() });
    }

    /**
     * Retrieve the Ping Service interface.
     * 
     * @return the Ping Service interface
     * @throws ServiceException
     *             if the ping service interface could not be located
     */
    private PingServiceStub getPingService(URL dabsURL) throws AxisFault {
        if(this.pingService == null) {
            String location = dabsURL.toString();
            location += PING_SERVICE_LOCATION_SERVLET_PATH;
            this.pingService = new PingServiceStub(location);
        }

        return this.pingService;
    }
    
    /**
     * @return
     */
    private static Log getLog() {
        return LOG;
    }
}
