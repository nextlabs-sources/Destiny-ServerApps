/*
 * Created on Apr 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

import com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean;

import javax.faces.model.DataModel;

import java.net.URL;
import java.util.List;

/**
 * IProfileBean provides an interface to the display layer for accessing
 * information associated with an agent configuration profile
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/IProfileBean.java#4 $
 */
public interface IProfileBean {

    /**
     * Retrive the title of the profile
     * 
     * @return the title of the profile
     */
    public String getProfileTitle();

    /**
     * Set the title of the profile
     * 
     * @param title
     *            the new title of the profile
     */
    public void setProfileTitle(String titleToSet);

    /**
     * Retrieve the URL of the agent broker with which the agent should
     * communicate
     * 
     * @return the URL of the agent broker with which the agent should
     *         communicate
     */
    public URL getProfileBrokerURL();

    /**
     * Retrieve the collection of existing DABS component urls
     * 
     * @return a collection of URL instances, representing the existing DABS
     *         components URLs
     */
    public List getDABSURLs();

    /**
     * Retrieve the alternate profile broker URL set on this profile bean.
     * 
     * @return the alternate profile broker URL set on this profile bean
     */
    public URL getAlternateProfileBrokerURL();

    /**
     * Set the alternate profile broker URL set on this profile bean. Set to
     * choose a broker URL other than that of a DABS component. This can be
     * used, for instance, in the case that a load balancer is configured to
     * balance load between multiple DABS components
     * 
     * @param alternateProfileBrokerURL
     *            an alternative DABS broker URL
     */
    public void setAlternateProfileBrokerURL(URL alternateProfileBrokerURL);

    /**
     * Set the URL of the agent broker with which the agent should communicate
     * 
     * @param brokerURL
     *            the URL of the agent broker with which the agent should
     *            communicate
     */
    public void setProfileBrokerURL(URL brokerURL);

    /**
     * Retrieve the frequency at which the agent should send heartbeats
     * 
     * @return the frequency at which the agent should send heartbeats
     */
    public ITimeIntervalBean getProfileHeartBeatFrequency();

    /**
     * Determine if the configured agent will be push enabled
     * 
     * @return true if push enabled, false otherwise
     */
    public boolean isPushEnabledOnProfile();

    /**
     * Set whether the configured agent should be push enabled
     * 
     * @param pushEnabled
     *            true to push enable the configured agent, false otherwise
     */
    public void setPushEnabledOnProfile(boolean pushEnabled);

    /**
     * Retrieve the push port that will be assigned to the agent. If there is a
     * port conflict on the agent, the actual push port may differ slightly
     * 
     * @return the push port that will be assigned to the agent (1 to 65535)
     */
    public int getProfilePushPort();

    /**
     * Set the push port that will be assigned to the agent. If there is a port
     * conflict on the agent, the actual push port may differ slightly
     * 
     * @param pushPort
     *            the push port that will be assigned to the agent (1 to 65535)
     */
    public void setProfilePushPort(int pushPort);

    /**
     * Gets the frequency at which the configured agent will send logging
     * information
     * 
     * @return the frequency at which the configured agent will send logging
     *         information
     */
    public ITimeIntervalBean getProfileLogFrequency();

    /**
     * Retrieve the size limit of the log entry accumulation on the agent
     * 
     * @return the size limit of the log entry accumulation on the agent
     */
    public short getProfileMaxLogSize();

    /**
     * Set the size limit of the log entry accumulation on the agent
     * 
     * @param logLimit
     *            the size limit of the log entry accumulation on the agent
     */
    public void setProfileMaxLogSize(short logLimit);

    /**
     * Set the password for the associated profile
     * 
     * @param the
     *            password for the associated profile
     */
    public void setProfilePassword(String password);

    /**
     * Retrieve the name of the assigned journaling settings for the associated
     * profile
     * 
     * @return the name of the assigned journaling settings for the associated
     *         profile
     */
    public String getAssignedJournalingSettingsName();

    /**
     * Set the name of the assigned journaling settings for the associated
     * profile
     * 
     * @param assignedJournalName
     *            the name of the assigned journaling settings for the
     *            associated profile
     */
    public void setAssignedJournalingSettingsName(String assignedJournalName);

    /**
     * Retrieve the custom activity journaling settings for this profile. Note
     * that this may not be the same as the journaling settings currently
     * assigned to this profile.
     * 
     * @return the custom activity journaling settings for this profile
     */
    public ICustomJournalingSettingsBean getCustomJournalingSettings();

    /**
     * Retrieve the list of hosts which are currently assigned the associate
     * profile
     * 
     * @return the list of hosts which are currently assigned the associate
     *         profile
     */
    public DataModel getHosts();

    /**
     * Determine if this bean represents a new profile (i.e. one which is not
     * yet persisted)
     * 
     * @return true if this bean represents a new profile, false otherwise
     */
    public boolean isNew();

    /**
     * Determine if this is the default profile
     * 
     * @return true if it's the default profile; false otherwise
     */
    public boolean isDefaultProfile();
}
