/*
 * Created on May 10, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import javax.faces.model.ArrayDataModel;

import com.bluejungle.destiny.services.management.types.AgentDTO;

/**
 * The HostsFromAgentDTOArrayDataModel provides a DataModel view providing Host
 * data from an array of AgentDTO[] instances
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/HostsFromAgentDTOArrayDataModel.java#1 $
 */

class HostsFromAgentDTOArrayDataModel extends ArrayDataModel {

    private String[] translatedData;

    /**
     * Create an instance of HostsFromAgentDTOArrayDataModel
     * 
     * @param array
     */
    public HostsFromAgentDTOArrayDataModel(AgentDTO[] rawData) {
        super(rawData);
    }

    /**
     * @see javax.faces.model.DataModel#getRowData()
     */
    public Object getRowData() {
        if (translatedData == null) {
            int rawDataLength = ((Object[]) super.getWrappedData()).length;
            translatedData = new String[rawDataLength];
        }

        int currentIndex = getRowIndex();
        if (translatedData.length <= currentIndex) {
            throw new IllegalArgumentException("Current index out of bounds");
        }

        String hostToReturn = translatedData[currentIndex];
        if (hostToReturn == null) {
            AgentDTO currentoRowDTO = (AgentDTO) super.getRowData();
            hostToReturn = new String(currentoRowDTO.getHost());
            translatedData[currentIndex] = hostToReturn;
        }

        return hostToReturn;
    }

    /**
     * @see javax.faces.model.DataModel#getWrappedData()
     */
    public Object getWrappedData() {
        return null; // The wrapped data should never be available!
    }

    /**
     * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
     */
    public void setWrappedData(Object data) {
        super.setWrappedData((AgentDTO[]) data); // Cast to test type
    }

}