/*
 * Created on Apr 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.framework.types.Name;
import com.bluejungle.destiny.framework.types.TimeIntervalDTO;
import com.bluejungle.destiny.framework.types.TimeUnits;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentTypeBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.ICustomJournalingSettingsBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.INewProfileBean;
import com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean;
import com.bluejungle.destiny.mgmtconsole.shared.defaultimpl.TimeIntervalBeanImpl;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsInfo;
import com.bluejungle.destiny.services.management.types.CommProfileInfo;
import com.bluejungle.domain.types.AgentTypeDTO;

import org.apache.axis2.databinding.types.Token;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.UnsignedShort;

import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Implementation of the INewProfileBean interface. Contains data for a new
 * profile to be created
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/NewProfileBeanImpl.java#3 $
 */
public class NewProfileBeanImpl implements INewProfileBean {

    /**
     * Intial values for a profile
     */
    private static final Token INITIAL_PROFILE_NAME = new Token("Untitled");
    private static final TimeIntervalDTO INITIAL_HEART_BEAT_FREQUENCY;
    private static final Boolean INITIAL_PUSH_ENABLED = Boolean.TRUE;
    private static final UnsignedShort INITIAL_DEFAULT_PUSH_PORT = new UnsignedShort(9000);
    private static final TimeIntervalDTO INITIAL_LOG_FREQUENCY;
    private static final UnsignedShort INITIAL_MAX_LOG_SIZE = new UnsignedShort(5);
    private static final String INITIAL_ASSIGNED_JOURNALING_SETTINGS_NAME = "Default"; // FIX
    // ME -
    // Should
    // be
    // read
    // from
    // server?

    private static URL BACKUP_INITIAL_PROFILE_BROKER_URL;
    static {
        INITIAL_HEART_BEAT_FREQUENCY = new TimeIntervalDTO();
        INITIAL_HEART_BEAT_FREQUENCY.setTime(new UnsignedShort(30));
        INITIAL_HEART_BEAT_FREQUENCY.setTimeUnit(TimeUnits.minutes);
        INITIAL_LOG_FREQUENCY = new TimeIntervalDTO();
        INITIAL_LOG_FREQUENCY.setTime(new UnsignedShort(5));
        INITIAL_LOG_FREQUENCY.setTimeUnit(TimeUnits.minutes);
        try {
            BACKUP_INITIAL_PROFILE_BROKER_URL = new URL("http://Enter_URL");
        } catch (MalformedURLException exception) {
        }
    }

    /*
     * This title won't actually be set in the profile, but will be used as the
     * value of the custom journaling settings radio button FIX ME - A bit of a
     * hack
     */
    private static final String INITIAL_CUSTOM_JOURNALING_SETTINGS_NAME = "Custom";

    private CommProfileInfo wrappedInfo;
    private ITimeIntervalBean heartBeatFrequency;
    private ITimeIntervalBean logFrequency;
    private CustomJournalingSettingsBeanImpl customJournalingSettings;
    private URL profileBrokerURL;
    private URL alternateProfileBrokerURL;
    private List dabsComponentURLs;

    /**
     * Create an instance of NewProfileBeanImpl
     * 
     * @param dabsComponentURLs
     *            TODO
     * 
     */
    public NewProfileBeanImpl(IAgentTypeBean bean, List dabsComponentURLs) {
        super();

        if (bean == null) {
            throw new NullPointerException("agentType cannot be null.");
        }

        if (dabsComponentURLs == null) {
            throw new NullPointerException("dabsComponentURLs cannot be null.");
        }

        this.dabsComponentURLs = dabsComponentURLs;

        wrappedInfo = new CommProfileInfo();
        wrappedInfo.setAgentType(AgentTypeDTO.Factory.fromValue(bean.getAgentTypeId()));
        Name profileName = new Name();
        profileName.setName(INITIAL_PROFILE_NAME);
        wrappedInfo.setName(profileName);

        wrappedInfo.setHeartBeatFrequency(INITIAL_HEART_BEAT_FREQUENCY);
        wrappedInfo.setPushEnabled(INITIAL_PUSH_ENABLED);
        wrappedInfo.setDefaultPushPort(INITIAL_DEFAULT_PUSH_PORT);
        wrappedInfo.setLogFrequency(INITIAL_LOG_FREQUENCY);
        wrappedInfo.setLogLimit(INITIAL_MAX_LOG_SIZE);
        wrappedInfo.setAssignedActivityJournalingName(INITIAL_ASSIGNED_JOURNALING_SETTINGS_NAME);

        TimeIntervalDTO heartBeatFrequencyDTO = this.wrappedInfo.getHeartBeatFrequency();
        this.heartBeatFrequency = new TimeIntervalBeanImpl(heartBeatFrequencyDTO.getTime().intValue(), heartBeatFrequencyDTO.getTimeUnit().getValue());

        TimeIntervalDTO logFrequencyDTO = this.wrappedInfo.getLogFrequency();
        this.logFrequency = new TimeIntervalBeanImpl(logFrequencyDTO.getTime().intValue(), logFrequencyDTO.getTimeUnit().getValue());

        this.customJournalingSettings = new CustomJournalingSettingsBeanImpl(INITIAL_CUSTOM_JOURNALING_SETTINGS_NAME);

        if (this.dabsComponentURLs.isEmpty()) {
            // will only happen in a system without a dabs. Not likely

            this.profileBrokerURL = BACKUP_INITIAL_PROFILE_BROKER_URL;
        } else {
            this.profileBrokerURL = (URL) ((SelectItem) this.dabsComponentURLs.get(0)).getValue();
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.INewProfileBean#setTitle(java.lang.String)
     */
    public void setProfileTitle(String title) {
        if (title == null) {
            throw new NullPointerException("title cannot be null.");
        }

        Name profileTitle = new Name();
        profileTitle.setName(new Token(title));

        this.wrappedInfo.setName(profileTitle);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getProfileTitle()
     */
    public String getProfileTitle() {
        return this.wrappedInfo.getName().toString();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getProfileBrokerURL()
     */
    public URL getProfileBrokerURL() {
        return this.profileBrokerURL;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setProfileBrokerURL(java.net.URL)
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
        return this.wrappedInfo.getPushEnabled();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setPushEnabled(boolean)
     */
    public void setPushEnabledOnProfile(boolean pushEnabled) {
        this.wrappedInfo.setPushEnabled(pushEnabled);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getPushPort()
     */
    public int getProfilePushPort() {
        return this.wrappedInfo.getDefaultPushPort().shortValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setPushPort(int)
     */
    public void setProfilePushPort(int pushPort) {
        this.wrappedInfo.setDefaultPushPort(new UnsignedShort(pushPort));
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
        return this.wrappedInfo.getLogLimit().shortValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setProfileMaxLogSize(short)
     */
    public void setProfileMaxLogSize(short logLimit) {
        this.wrappedInfo.setLogLimit(new UnsignedShort(logLimit));
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setProfilePassword(java.lang.String)
     */
    public void setProfilePassword(String password) {
        if (password == null) {
            throw new NullPointerException("password cannot be null.");
        }

        this.wrappedInfo.setPassword(password);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#getAssignedJournalingSettingsName()
     */
    public String getAssignedJournalingSettingsName() {
        return this.wrappedInfo.getAssignedActivityJournalingName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#setAssignedJournalingSettingsName(java.lang.String)
     */
    public void setAssignedJournalingSettingsName(String assignedJournalName) {
        if (assignedJournalName == null) {
            throw new NullPointerException("assignedJournalName cannot be null.");
        }

        if (assignedJournalName.equals(INITIAL_CUSTOM_JOURNALING_SETTINGS_NAME)) {
            this.wrappedInfo.setCustomActivityJournalingSettingsAssigned(Boolean.TRUE);
        } else {
            this.wrappedInfo.setCustomActivityJournalingSettingsAssigned(Boolean.FALSE);
            this.wrappedInfo.setAssignedActivityJournalingName(assignedJournalName);
        }
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
        return true;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean#isDefaultProfile()
     */
    public boolean isDefaultProfile() {
        return false;
    }

    /* FIX ME - Remove this method once UI is fixed */
    public DataModel getHosts() {
        return null;
    }

    /**
     * Retrieve the wrapped CommProfileInfo reflecting the current state of this
     * bean
     * 
     * @return the wrapped CommProfileInfo reflecting the current state of this
     *         bean
     */
    public CommProfileInfo getWrappedProfileInfo() {
        // Update heart beat frequency
        UnsignedShort time = new UnsignedShort(this.heartBeatFrequency.getTime());
        TimeIntervalDTO timeIntervalToSet = new TimeIntervalDTO();
        timeIntervalToSet.setTime(time);
        timeIntervalToSet.setTimeUnit(TimeUnits.Factory.fromValue(this.heartBeatFrequency.getTimeUnit()));
        this.wrappedInfo.setHeartBeatFrequency(timeIntervalToSet);

        // Update log frequency
        time = new UnsignedShort(this.logFrequency.getTime());
        timeIntervalToSet = new TimeIntervalDTO();
        timeIntervalToSet.setTime(time);
        timeIntervalToSet.setTimeUnit(TimeUnits.Factory.fromValue(this.logFrequency.getTimeUnit()));
        this.wrappedInfo.setLogFrequency(timeIntervalToSet);

        // Update custom journaling settings
        ActivityJournalingSettingsInfo customJournalingSettings = this.customJournalingSettings.getWrappedJournalingSettingsInfo();
        this.wrappedInfo.setCustomActivityJournalingSettings(customJournalingSettings);

        // Update the profile URL
        URI brokerURIToSet = null;
        if (this.alternateProfileBrokerURL != null) {
            brokerURIToSet = ProfileBeanUtils.getAxisURIFromURL(this.alternateProfileBrokerURL);
        } else {
            brokerURIToSet = ProfileBeanUtils.getAxisURIFromURL(this.profileBrokerURL);
        }
        this.wrappedInfo.setDABSLocation(brokerURIToSet);

        return this.wrappedInfo;
    }

}
