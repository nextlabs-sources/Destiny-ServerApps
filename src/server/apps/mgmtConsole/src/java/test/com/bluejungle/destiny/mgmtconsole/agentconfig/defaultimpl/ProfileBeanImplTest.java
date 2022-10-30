/*
 * Created on May 5, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import junit.framework.TestCase;

import org.apache.axis.types.Token;
import org.apache.axis.types.URI;
import org.apache.axis.types.UnsignedShort;

import com.bluejungle.destiny.framework.types.TimeIntervalDTO;
import com.bluejungle.destiny.framework.types.TimeUnits;
import com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean;
import com.bluejungle.destiny.mgmtconsole.shared.defaultimpl.TimeIntervalBeanImpl;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTO;
import com.bluejungle.destiny.services.management.types.CommProfileDTO;
import com.bluejungle.domain.types.ActionTypeDTO;
import com.bluejungle.domain.types.ActionTypeDTOList;
import com.bluejungle.domain.types.AgentTypeDTO;

/**
 * JUnit test for ProfileBeanImpl
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/branch/Destiny_Beta2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/ProfileBeanImplTest.java#1 $
 */

public class ProfileBeanImplTest extends TestCase {

    private ProfileBeanImpl beanToTest;
    private CommProfileDTO testProfile;
    private List dabsComponentURLs;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ProfileBeanImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.testProfile = new CommProfileDTO();
        this.testProfile.setAgentType(AgentTypeDTO.DESKTOP);
        this.testProfile.setCreatedDate(Calendar.getInstance());
        this.testProfile.setCurrentActivityJournalingSettings(new ActivityJournalingSettingsDTO("foo", new ActionTypeDTOList(new ActionTypeDTO[0])));
        this.testProfile.setCustomActivityJournalingSettings(new ActivityJournalingSettingsDTO("foo", new ActionTypeDTOList(new ActionTypeDTO[0])));
        this.testProfile.setDABSLocation(new URI("http://www.dabs.com"));
        this.testProfile.setDefaultPushPort(new UnsignedShort(50));
        this.testProfile.setHeartBeatFrequency(new TimeIntervalDTO(new UnsignedShort(10), TimeUnits.days));
        this.testProfile.setId(0);
        this.testProfile.setLogFrequency(new TimeIntervalDTO(new UnsignedShort(990), TimeUnits.milliseconds));
        this.testProfile.setLogLimit(new UnsignedShort(15));
        this.testProfile.setModifiedDate(Calendar.getInstance());
        this.testProfile.setName(new Token("Foo"));
        this.testProfile.setPushEnabled(false);
        this.testProfile.setDefaultProfile(true);

        this.dabsComponentURLs = Collections.singletonList(new SelectItem(new URL("http://www.foo.com"), "Foo"));
        
        this.beanToTest = new ProfileBeanImpl(this.testProfile, this.dabsComponentURLs);
    }

    public void testGetProfileId() {
        assertEquals("Ensure profile id is correct", this.testProfile.getId(), this.beanToTest.getProfileId());
    }

    public void testGetSetProfileTitle() {
        assertEquals("Ensure profile title is correct", this.testProfile.getName(), this.beanToTest.getProfileTitle());
        String nameToSet = "foobarfoo";
        this.beanToTest.setProfileTitle(nameToSet);
        assertEquals("testGetSetProfileTitle - Ensure profile title set as expected", nameToSet, this.beanToTest.getProfileTitle());
        
        try {
            this.beanToTest.setProfileTitle(null);
            fail("Should throw NPE for null profile title");
        } catch (NullPointerException exception) {}
    }

    public void testGetSetProfileBrokerURL() throws MalformedURLException {
        // Broker URL will be initially null, since it's an alternate URL (not a DABS URL)
        assertEquals("Ensure initial profile brokers url correct", null, this.beanToTest.getProfileBrokerURL());

        // Now set it
        URL urlToSet = new URL("http://www.achew.com");
        this.beanToTest.setProfileBrokerURL(urlToSet);
        assertEquals("Ensure profile is set as expected", urlToSet, this.beanToTest.getProfileBrokerURL());
    }

    public void testGetSetAlternativeProfileBrokerURL() throws MalformedURLException {
        assertEquals("Ensure initial alternate profile brokers url correct", this.testProfile.getDABSLocation().toString(), this.beanToTest.getAlternateProfileBrokerURL().toString());

        // Now set it
        URL urlToSet = new URL("http://www.achew.com");
        this.beanToTest.setAlternateProfileBrokerURL(urlToSet);
        assertEquals("Ensure alternate profile URL is set as expected", urlToSet, this.beanToTest.getAlternateProfileBrokerURL());
    }
    
    public void testGetDABSURLs() {
        assertEquals("Ensure dabs component urls as expected", this.dabsComponentURLs, this.beanToTest.getDABSURLs());
    }
    
    public void testGetSetProfileHeartBeatFrequency() {
        assertEquals("Ensure initial heart beat frequency time value correct", this.testProfile.getHeartBeatFrequency().getTime().intValue(), this.beanToTest.getProfileHeartBeatFrequency().getTime());
        assertEquals("Ensure initial heart beat frequency time unit correct", this.testProfile.getHeartBeatFrequency().getTimeUnit().toString(), this.beanToTest.getProfileHeartBeatFrequency().getTimeUnit());

        // Now set it
        ITimeIntervalBean timeIntervalToSet = new TimeIntervalBeanImpl(790, "foo");
        this.beanToTest.setProfileHeartBeatFrequency(timeIntervalToSet);
        assertEquals("Ensure heart beat frequence set as expected", timeIntervalToSet, this.beanToTest.getProfileHeartBeatFrequency());
    }

    public void testSetIsSetPushEnabledOnProfile() {
        assertEquals("Ensure initial push enabled is correct", this.testProfile.isPushEnabled(), this.beanToTest.isPushEnabledOnProfile());

        // Now, set is
        this.beanToTest.setPushEnabledOnProfile(true);
        assertEquals("Ensure push enabled set to true as expected", true, this.beanToTest.isPushEnabledOnProfile());
        this.beanToTest.setPushEnabledOnProfile(false);
        assertEquals("Ensure push enabled set to true as expected", false, this.beanToTest.isPushEnabledOnProfile());
    }

    public void testGetSetProfilePushPort() {
        assertEquals("Ensure initial push port is correct", this.testProfile.getDefaultPushPort().shortValue(), this.beanToTest.getProfilePushPort());

        // Now, set is
        this.beanToTest.setProfilePushPort((short) 2);
        assertEquals("Ensure push port set to as expected", (short) 2, this.beanToTest.getProfilePushPort());
    }

    public void testGetSetProfileLogFrequency() {
        assertEquals("Ensure initial log frequency time value correct", this.testProfile.getLogFrequency().getTime().intValue(), this.beanToTest.getProfileLogFrequency().getTime());
        assertEquals("Ensure initial log frequency time unit correct", this.testProfile.getLogFrequency().getTimeUnit().toString(), this.beanToTest.getProfileLogFrequency().getTimeUnit());

        // Now set it
        ITimeIntervalBean timeIntervalToSet = new TimeIntervalBeanImpl(790, "foo");
        this.beanToTest.setProfileLogFrequency(timeIntervalToSet);
        assertEquals("Ensure log frequency set as expected", timeIntervalToSet, this.beanToTest.getProfileLogFrequency());
    }

    public void testGetSetProfileMaxLogSize() {
        assertEquals("Ensure initial max log size is correct", this.testProfile.getLogLimit().shortValue(), this.beanToTest.getProfileMaxLogSize());

        // Now, set is
        this.beanToTest.setProfileMaxLogSize((short) 2);
        assertEquals("Ensure max log size set to as expected", (short) 2, this.beanToTest.getProfileMaxLogSize());
    }

    public void testGetSetAssignedJournalingSettingsName() {
        assertEquals("Ensure initial journaling settings name is correct", this.testProfile.getCurrentActivityJournalingSettings().getName(), this.beanToTest.getAssignedJournalingSettingsName());

        // Now, set is
        String journalingName = "heheh";
        this.beanToTest.setAssignedJournalingSettingsName(journalingName);
        assertEquals("Ensure journaling setting name set to as expected", journalingName, this.beanToTest.getAssignedJournalingSettingsName());
    }

    public void testGetCustomJournalingSettings() {
        assertNotNull("Ensure custom journaling settings are not null", this.beanToTest.getCustomJournalingSettings());
    }

    public void testIsNew() {
        assertFalse("Ensure profile is never new", this.beanToTest.isNew());
    }

    public void testIsDefaultProfile() {
        assertEquals("Ensure default field as expected", this.testProfile.isDefaultProfile(), this.beanToTest.isDefaultProfile());
    }

    public void testGetHosts() {
        //TODO Implement getHosts().

        // FIX ME - Test after changin AgentQueryBroker to a component and
        // replacing it with a test AgentQueryBroker

    }

    public void testSetProfilePassword() {
        String passwordToSet = "foobar";
        this.beanToTest.setProfilePassword(passwordToSet);

        assertEquals("Ensure that it was set as expected on the backing DTO", passwordToSet, this.beanToTest.getWrappedProfileDTO().getPassword());

        NullPointerException expectedException = null;
        try {
            this.beanToTest.setProfilePassword(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("testSetProfilePassword - Ensure NPE thrown when setting null password", expectedException);
    }

    public void testGetWrappedProfileDTO() {
        assertEquals("Ensure wrapped DTO is as expected", this.testProfile, this.beanToTest.getWrappedProfileDTO());
    }
}
