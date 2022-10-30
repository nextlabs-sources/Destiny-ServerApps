/*
 * Created on Apr 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.framework.types.Name;
import com.bluejungle.destiny.framework.types.TimeIntervalDTO;
import com.bluejungle.destiny.framework.types.TimeUnits;
import com.bluejungle.destiny.mgmtconsole.agentconfig.ICustomJournalingSettingsBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IExistingProfileBean;
import com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean;
import com.bluejungle.destiny.mgmtconsole.shared.defaultimpl.TimeIntervalBeanImpl;
import com.bluejungle.destiny.services.management.BadArgumentFault;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTO;
import com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO;
import com.bluejungle.destiny.services.management.types.CommProfileDTO;
import com.bluejungle.framework.comp.ComponentManagerFactory;

import org.apache.axis2.databinding.types.Token;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.UnsignedShort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the IExistingProfileBean interface. Contains information
 * about a particular agent profile
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/ProfileBeanImpl.java#3 $
 */

public class ProfileBeanImpl implements IExistingProfileBean {

    private static final Log LOG = LogFactory.getLog(ProfileBeanImpl.class.getName());

    private CommProfileDTO wrappedProfileDTO;
    private ITimeIntervalBean heartBeatFrequency;
    private ITimeIntervalBean logFrequency;
    private CustomJournalingSettingsBeanImpl customJournalingSettings;
    private String assignedJournalingSettingsName;
    private DataModel hosts;
    private URL profileBrokerURL;
    private URL alternateProfileBrokerURL;
    private List dabsComponentURLs;

    /**
     * Create an instance of ProfileBeanImpl
     * @param agentType 
     * 
     * @param wrappedProfileDTO
     * @param dabsComponentURLs
     *            TODO
     * 
     */
    public ProfileBeanImpl(CommProfileDTO wrappedProfileDTO, List dabsComponentURLs) {
        super();

        if (wrappedProfileDTO == null) {
            throw new NullPointerException("wrappedProfileDTO cannot be null.");
        }

        if (dabsComponentURLs == null) {
            throw new NullPointerException("dabsComponentURLs cannot be null.");
        }
        
        this.wrappedProfileDTO = wrappedProfileDTO;

        this.dabsComponentURLs = dabsComponentURLs;
        resolveProfileBrokerURL();

        TimeIntervalDTO heartBeatFrequencyDTO = this.wrappedProfileDTO.getHeartBeatFrequency();
        this.heartBeatFrequency = new TimeIntervalBeanImpl(heartBeatFrequencyDTO.getTime().intValue(), heartBeatFrequencyDTO.getTimeUnit().getValue());

        TimeIntervalDTO logFrequencyDTO = this.wrappedProfileDTO.getLogFrequency();
        this.logFrequency = new TimeIntervalBeanImpl(logFrequencyDTO.getTime().intValue(), logFrequencyDTO.getTimeUnit().getValue());

        this.customJournalingSettings = new CustomJournalingSettingsBeanImpl(this.wrappedProfileDTO.getCustomActivityJournalingSettings());
        this.assignedJournalingSettingsName = this.wrappedProfileDTO.getCurrentActivityJournalingSettings().getName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getProfileId()
     */
    public long getProfileId() {
        return this.wrappedProfileDTO.getId();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getTitle()
     */
    public String getProfileTitle() {
        return this.wrappedProfileDTO.getName().toString();
    }

    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setProfileTitle(java.lang.String)
     */
    public void setProfileTitle(String titleToSet) {
        if (titleToSet == null) {
            throw new NullPointerException("titleToSet cannot be null.");
        }

        Name title = new Name();
        title.setName(new Token(titleToSet));
        this.wrappedProfileDTO.setName(title);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getBrokerURL()
     */
    public URL getProfileBrokerURL() {
        return this.profileBrokerURL;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setBrokerURL(java.net.URL)
     */
    public void setProfileBrokerURL(URL brokerURL) {
        if (brokerURL == null) {
            throw new NullPointerException("brokerURL cannot be null.");
        }

        this.profileBrokerURL = brokerURL;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getAlternateProfileBrokerURL()
     */
    public URL getAlternateProfileBrokerURL() {
        return this.alternateProfileBrokerURL;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setAlternateProfileBrokerURL(java.net.URL)
     */
    public void setAlternateProfileBrokerURL(URL alternateProfileBrokerURL) {
        this.alternateProfileBrokerURL = alternateProfileBrokerURL;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getDABSURLs()
     */
    public List getDABSURLs() {
        return this.dabsComponentURLs;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getHeartBeatFrequency()
     */
    public ITimeIntervalBean getProfileHeartBeatFrequency() {
        return this.heartBeatFrequency;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setHeartBeatFrequency(com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean)
     */
    public void setProfileHeartBeatFrequency(ITimeIntervalBean timeInterval) {
        if (timeInterval == null) {
            throw new NullPointerException("timeInterval cannot be null.");
        }

        this.heartBeatFrequency = timeInterval;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#isPushEnabled()
     */
    public boolean isPushEnabledOnProfile() {
        return this.wrappedProfileDTO.getPushEnabled();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setPushEnabled(boolean)
     */
    public void setPushEnabledOnProfile(boolean pushEnabled) {
        this.wrappedProfileDTO.setPushEnabled(pushEnabled);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getPushPort()
     */
    public int getProfilePushPort() {
        return this.wrappedProfileDTO.getDefaultPushPort().intValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setPushPort(int)
     */
    public void setProfilePushPort(int pushPort) {
        this.wrappedProfileDTO.setDefaultPushPort(new UnsignedShort(pushPort));
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getProfileLogFrequency()
     */
    public ITimeIntervalBean getProfileLogFrequency() {
        return this.logFrequency;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setProfileLogFrequency(com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean)
     */
    public void setProfileLogFrequency(ITimeIntervalBean logFrequencyToSet) {
        if (logFrequencyToSet == null) {
            throw new NullPointerException("logFrequencyToSet cannot be null.");
        }

        this.logFrequency = logFrequencyToSet;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getProfileMaxLogSize()
     */
    public short getProfileMaxLogSize() {
        return this.wrappedProfileDTO.getLogLimit().shortValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setProfileMaxLogSize(short)
     */
    public void setProfileMaxLogSize(short logLimit) {
        this.wrappedProfileDTO.setLogLimit(new UnsignedShort(logLimit));
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setProfilePassword(java.lang.String)
     */
    public void setProfilePassword(String password) {
        if (password == null) {
            throw new NullPointerException("password cannot be null.");
        }

        this.wrappedProfileDTO.setPassword(password);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getAssignedJournalingSettingsName()
     */
    public String getAssignedJournalingSettingsName() {
        return this.assignedJournalingSettingsName;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setAssignedJournalingSettingsName(java.lang.String)
     */
    public void setAssignedJournalingSettingsName(String assignedJournalingSettingsName) {
        if (assignedJournalingSettingsName == null) {
            throw new NullPointerException("assignedJournalName cannot be null.");
        }

        this.assignedJournalingSettingsName = assignedJournalingSettingsName;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getCustomJournalingSettings()
     */
    public ICustomJournalingSettingsBean getCustomJournalingSettings() {
        return this.customJournalingSettings;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#isNew()
     */
    public boolean isNew() {
        return false;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#isDefaultProfile()
     */
    public boolean isDefaultProfile() {
        return this.wrappedProfileDTO.getDefaultProfile();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getHosts()
     */
    public DataModel getHosts() {
        return this.hosts;
    }

    /**
     * Called to load the hosts for this profile during the prerender phase of
     * the page
     * 
     * @param maxHostsToLoad
     * @param searchFilter
     * 
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     * 
     */
    void loadHosts(String searchFilter, int maxHostsToLoad) throws RemoteException, ServiceNotReadyFault, BadArgumentFault {
        if (searchFilter == null) {
            throw new NullPointerException("searchFilter cannot be null.");
        }
        
        if (maxHostsToLoad < 0) {
            throw new IllegalArgumentException("maxHostsToLoad must be greated than 0");
        }
        
        AgentQueryBroker agentQueryBroker = getAgentQueryBroker();
        String agentTypeId = this.wrappedProfileDTO.getAgentType().getValue();
        AgentQueryResultsDTO queryResults = agentQueryBroker.getAgentsForProfile(getProfileId(), agentTypeId, searchFilter, maxHostsToLoad);
        this.hosts = new HostsFromAgentDTOArrayDataModel(queryResults.getAgentList().getAgents());
    }

    /**
     * Resolve the profile broker url based on the profile data
     */
    private void resolveProfileBrokerURL() {
        URI existingBrokerURI = this.wrappedProfileDTO.getDABSLocation();
        if (existingBrokerURI != null) {
            String existingBrokerURIAsString = existingBrokerURI.toString();
            Iterator dabsComponentIterator = this.dabsComponentURLs.iterator();
            while (dabsComponentIterator.hasNext()) {
                SelectItem nextDABSURLSelectItem = (SelectItem) dabsComponentIterator.next();
                URL nextDABSComponentURL = (URL) nextDABSURLSelectItem.getValue();
                if (existingBrokerURIAsString.equals(nextDABSComponentURL.toString())) {
                    this.profileBrokerURL = nextDABSComponentURL;
                }
            }
        } else {
            // Should only happen if we don't have a registered DABS component
            // yet. Very rare
            try {
                this.profileBrokerURL = new URL("http://");
            } catch (MalformedURLException exception) {
                // Shouldn't happen, as the URL above is hard coded
                getLog().warn("Failed to create profile bean");
            }
        }

        if (this.profileBrokerURL == null) {
            /**
             * If the profile broker url is still null, here, it means that
             * we're using a load balancer
             */
            this.alternateProfileBrokerURL = ProfileBeanUtils.getURLFromAxisURI(existingBrokerURI);
        }
    }

    /**
     * Retrieve the Agent Query Broker
     * 
     * @return the Agent Query Broker
     */
    private AgentQueryBroker getAgentQueryBroker() {
        return (AgentQueryBroker) ComponentManagerFactory.getComponentManager().getComponent(AgentQueryBroker.class);
    }

    /**
     * Retrieve the wrapped CommProfileDTO reflecting the current state of this
     * bean
     * 
     * @return the wrapped CommProfileDTO
     */
    CommProfileDTO getWrappedProfileDTO() {
        // Update heart beat frequency
        UnsignedShort time = new UnsignedShort(this.heartBeatFrequency.getTime());
        TimeIntervalDTO timeIntervalToSet = new TimeIntervalDTO();
        timeIntervalToSet.setTime(time);
        timeIntervalToSet.setTimeUnit(TimeUnits.Factory.fromValue(this.heartBeatFrequency.getTimeUnit()));

        this.wrappedProfileDTO.setHeartBeatFrequency(timeIntervalToSet);

        // Update log frequency
        time = new UnsignedShort(this.logFrequency.getTime());
        timeIntervalToSet = new TimeIntervalDTO();
        timeIntervalToSet.setTime(time);
        timeIntervalToSet.setTimeUnit(TimeUnits.Factory.fromValue(this.logFrequency.getTimeUnit()));

        this.wrappedProfileDTO.setLogFrequency(timeIntervalToSet);

        // Update the profile URL
        URI brokerURIToSet = null;
        if (this.alternateProfileBrokerURL != null) {
            brokerURIToSet = ProfileBeanUtils.getAxisURIFromURL(this.alternateProfileBrokerURL);
        } else {
            brokerURIToSet = ProfileBeanUtils.getAxisURIFromURL(this.profileBrokerURL);
        }
        this.wrappedProfileDTO.setDABSLocation(brokerURIToSet);

        // Update custom journaling settings
        ActivityJournalingSettingsDTO customJournalingSettings = this.customJournalingSettings.getWrappedJournalingSettingsDTO();
        this.wrappedProfileDTO.setCustomActivityJournalingSettings(customJournalingSettings);

        if (this.assignedJournalingSettingsName.equals(customJournalingSettings.getName())) {
            this.wrappedProfileDTO.setCurrentActivityJournalingSettings(customJournalingSettings);
        } else {
            ActivityJournalingSettingsHelper settingsHelper = ActivityJournalingSettingsHelper.getInstance();
            try {
                ActivityJournalingSettingsDTO settingsDTO = settingsHelper.getActivityJournalingSettings(this.wrappedProfileDTO.getAgentType().getValue(), this.assignedJournalingSettingsName);
                if (settingsDTO == null) {
                    // This should never happen. Log it
                    getLog().warn("Failed to retrieve selected journaling setting, " + this.assignedJournalingSettingsName);
                }

                this.wrappedProfileDTO.setCurrentActivityJournalingSettings(settingsDTO);
            } catch (ActivityJournalingSettingsHelper.ActivityJournalingSettingsException exception) {
                /**
                 * FIX ME - Not sure what the best action is here. I don't want
                 * to throw an exception, because I want to try to save the
                 * other changes made. The best thing to do might be to add a
                 * user facing warning here stating that some update may have
                 * failed
                 */
                getLog().warn("Failed to retrieve selected journaling setting, " + this.assignedJournalingSettingsName);
            }
        }

        return this.wrappedProfileDTO;
    }

    /**
     * Retrieve a log to which log messages can be written
     * 
     * @return a log to which log messages can be written
     */
    private Log getLog() {
        return LOG;
    }
}
