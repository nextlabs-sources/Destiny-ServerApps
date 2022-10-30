/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.mgmtconsole.CommonConstants;
import com.bluejungle.destiny.mgmtconsole.status.IComponentDataBean;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.management.types.Component;

/**
 * ComponentDataBean is a concrete implementation of the IComponentDataBean
 * interface which is populated through a Component instance retreive from the
 * DMS Component web service
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/ComponentDataBean.java#1 $
 */
public class ComponentDataBean implements IComponentDataBean {

    private static final Log LOG = LogFactory.getLog(ComponentDataBean.class.getName());
    private static final Map<String, String> DISPLAY_TYPES = new HashMap<String, String>();

    private String componentName;
    private String componentType;
    private String hostName;
    private int port;
    private Calendar lastHeartbeatTimestamp;
    private Calendar expectedHeartbeatTimestamp;
    private boolean active;

    static {
        final ResourceBundle bundle = ResourceBundle.getBundle(CommonConstants.MGMT_CONSOLE_BUNDLE_NAME);
        DISPLAY_TYPES.put(ServerComponentType.DABS.getName(), bundle.getString("server_component_type_dabs"));
        DISPLAY_TYPES.put(ServerComponentType.DAC.getName(), bundle.getString("server_component_type_dac"));
        DISPLAY_TYPES.put(ServerComponentType.DCSF.getName(), bundle.getString("server_component_type_dcsf"));
        DISPLAY_TYPES.put(ServerComponentType.DEM.getName(), bundle.getString("server_component_type_dem"));
        DISPLAY_TYPES.put(ServerComponentType.DMS.getName(), bundle.getString("server_component_type_dms"));
        DISPLAY_TYPES.put(ServerComponentType.DPS.getName(), bundle.getString("server_component_type_dps"));
        DISPLAY_TYPES.put(ServerComponentType.MGMT_CONSOLE.getName(), bundle.getString("server_component_type_mgmt"));
        DISPLAY_TYPES.put(ServerComponentType.REPORTER.getName(), bundle.getString("server_component_type_reporter"));
    }

    /**
     * Create an instance of ComponentDataBean.
     * 
     * @param component
     *            the component instance containing the component data
     */
    public ComponentDataBean(Component component) {
        super();

        if (component == null) {
            throw new NullPointerException("component cannot be null.");
        }

        this.componentName = component.getName();

        this.componentType = DISPLAY_TYPES.get(component.getType());
        if (this.componentType == null) {
            componentType = component.getTypeDisplayName();
        }

        String componentURLString = component.getComponentURL();

        try {
            URL componentURL = new URL(componentURLString);
            this.hostName = componentURL.getHost();
            this.port = componentURL.getPort();
        } catch (MalformedURLException exception) {
            LOG.warn("Failed to parse url for component with name, " + this.componentName, exception);
            this.hostName = "unknown";
            this.port = -1;
        }

        long lastHeartbeatTimeInMillis = component.getLastHeartbeat();
        this.lastHeartbeatTimestamp = Calendar.getInstance();
        this.lastHeartbeatTimestamp.setTimeInMillis(lastHeartbeatTimeInMillis);

        int heartBeatRate = component.getHeartbeatRate();
        this.expectedHeartbeatTimestamp = (Calendar) this.lastHeartbeatTimestamp.clone();
        this.expectedHeartbeatTimestamp.add(Calendar.SECOND, heartBeatRate);

        this.active = component.getActive();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IComponentDataBean#getComponentName()
     */
    public String getComponentName() {
        return this.componentName;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IComponentDataBean#getComponentType()
     */
    public String getComponentType() {
        return this.componentType;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IComponentDataBean#getComponentHostName()
     */
    public String getComponentHostName() {
        return this.hostName;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IComponentDataBean#getComponentPort()
     */
    public int getComponentPort() {
        return this.port;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IComponentDataBean#getComponentLastHeartbeatTime()
     */
    public Calendar getComponentLastHeartbeatTime() {
        return this.lastHeartbeatTimestamp;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IComponentDataBean#getComponentExpectedHeartbeatTime()
     */
    public Calendar getComponentExpectedHeartbeatTime() {
        return this.expectedHeartbeatTimestamp;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IComponentDataBean#isActive()
     */
    public boolean isActive() {
        return this.active;
    }
}