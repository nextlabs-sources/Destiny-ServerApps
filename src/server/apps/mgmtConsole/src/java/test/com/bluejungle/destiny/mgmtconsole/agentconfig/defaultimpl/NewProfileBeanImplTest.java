/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import junit.framework.TestCase;

import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentTypeBean;
import com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean;
import com.bluejungle.destiny.mgmtconsole.shared.defaultimpl.TimeIntervalBeanImpl;
import com.bluejungle.destiny.services.management.types.ActionTypeDTO;
import com.bluejungle.destiny.services.management.types.ActionTypeDTOList;
import com.bluejungle.destiny.services.management.types.AgentTypeDTO;
import com.bluejungle.domain.agenttype.AgentTypeEnumType;

/**
 * Test for NewProfileBeanImpl
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/NewProfileBeanImplTest.java#1 $
 */

public class NewProfileBeanImplTest extends TestCase {
    private NewProfileBeanImpl beanToTest;
    private List dabsComponentURLs;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(NewProfileBeanImpl.class);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
                
        this.dabsComponentURLs = Collections.singletonList(new SelectItem(new URL("http://www.bar.com"), "Foo"));
        AgentTypeDTO agentTypeDTO = new AgentTypeDTO(AgentTypeEnumType.DESKTOP.getName(), "Desktop", new ActionTypeDTOList(new ActionTypeDTO[0]));
        IAgentTypeBean agentTypeBean = new AgentTypeBeanImpl(agentTypeDTO);
        this.beanToTest = new NewProfileBeanImpl(agentTypeBean, dabsComponentURLs);
    }

    public void testGetSetProfileTitle() {
        assertEquals("Ensure profile title is correct", "Untitled", this.beanToTest.getProfileTitle());
        
        // Now set it
        String newTitle = "whatsupdoc";
        this.beanToTest.setProfileTitle(newTitle);
        assertEquals("Ensure profile title set is as expected", newTitle, this.beanToTest.getProfileTitle());
    }

    public void testGetSetProfileBrokerURL() throws MalformedURLException {
        assertEquals("Ensure initial profile brokers url correct", ((SelectItem)this.dabsComponentURLs.get(0)).getValue(), this.beanToTest.getProfileBrokerURL());

        // Now set it
        URL urlToSet = new URL("http://www.achew.com");
        this.beanToTest.setProfileBrokerURL(urlToSet);
        assertEquals("Ensure profile is set as expected", urlToSet, this.beanToTest.getProfileBrokerURL());
    }

    public void testGetSetAlternateProfileBrokerURL() throws MalformedURLException {
        assertEquals("Ensure initial alternate profile brokers url correct", null, this.beanToTest.getAlternateProfileBrokerURL());

        // Now set it
        URL urlToSet = new URL("http://www.achew.com");
        this.beanToTest.setAlternateProfileBrokerURL(urlToSet);
        assertEquals("Ensure alternate profile URL is set as expected", urlToSet, this.beanToTest.getAlternateProfileBrokerURL());        
    }
    
    public void testGetSetProfileHeartBeatFrequency() {
        assertEquals("Ensure initial heart beat frequency time value correct", 30, this.beanToTest.getProfileHeartBeatFrequency().getTime());
        assertEquals("Ensure initial heart beat frequency time unit correct", "minutes", this.beanToTest.getProfileHeartBeatFrequency().getTimeUnit());

        // Now set it
        ITimeIntervalBean timeIntervalToSet = new TimeIntervalBeanImpl(790, "foo");
        this.beanToTest.setProfileHeartBeatFrequency(timeIntervalToSet);
        assertEquals("Ensure heart beat frequence set as expected", timeIntervalToSet, this.beanToTest.getProfileHeartBeatFrequency());
    }

    public void testGetDABSURLs() {
        assertEquals("Ensure dabs urls are as expected", this.dabsComponentURLs, this.beanToTest.getDABSURLs());
    }
    
    public void testSetIsPushEnabledOnProfile() {
        assertEquals("Ensure initial push enabled is correct", true, this.beanToTest.isPushEnabledOnProfile());

        // Now, set is
        this.beanToTest.setPushEnabledOnProfile(true);
        assertEquals("Ensure push enabled set to true as expected", true, this.beanToTest.isPushEnabledOnProfile());
        this.beanToTest.setPushEnabledOnProfile(false);
        assertEquals("Ensure push enabled set to true as expected", false, this.beanToTest.isPushEnabledOnProfile());
    }

    public void testGetSetProfilePushPort() {
        assertEquals("Ensure initial push port is correct", (short)9000, this.beanToTest.getProfilePushPort());

        // Now, set is
        this.beanToTest.setProfilePushPort((short)2);
        assertEquals("Ensure push port set to as expected", (short)2, this.beanToTest.getProfilePushPort());
    }

    public void testGetSetProfileLogFrequency() {
        assertEquals("Ensure initial log frequency time value correct", 5, this.beanToTest.getProfileLogFrequency().getTime());
        assertEquals("Ensure initial log frequency time unit correct", "minutes", this.beanToTest.getProfileLogFrequency().getTimeUnit());

        // Now set it
        ITimeIntervalBean timeIntervalToSet = new TimeIntervalBeanImpl(790, "foo");
        this.beanToTest.setProfileLogFrequency(timeIntervalToSet);
        assertEquals("Ensure log frequency set as expected", timeIntervalToSet, this.beanToTest.getProfileLogFrequency());
    }

    public void testGetSetProfileMaxLogSize() {
        assertEquals("Ensure initial max log size is correct", (short)5, this.beanToTest.getProfileMaxLogSize());

        // Now, set is
        this.beanToTest.setProfileMaxLogSize((short)2);
        assertEquals("Ensure max log size set to as expected", (short)2, this.beanToTest.getProfileMaxLogSize()); 
    }

    public void testGetSetAssignedJournalingSettingsName() {
        assertEquals("Ensure initial journaling settings name is correct", "Default", this.beanToTest.getAssignedJournalingSettingsName());

        // Now, set is
        String journalingName = "heheh";
        this.beanToTest.setAssignedJournalingSettingsName(journalingName);
        assertEquals("Ensure journaling setting name set to as expected", journalingName, this.beanToTest.getAssignedJournalingSettingsName()); 
    }

    public void testGetCustomJournalingSettings() {
        assertNotNull("Ensure custom journaling settings are not null", this.beanToTest.getCustomJournalingSettings());
    }

    public void testIsNew() {
        assertTrue("Ensure profile is always new", this.beanToTest.isNew());
    }

    public void testIsDefaultProfile() {
        assertFalse("Ensure new profile is never defalt", this.beanToTest.isDefaultProfile());
    }

    public void testSetProfilePassword() {
        String passwordToSet = "foobar";
        this.beanToTest.setProfilePassword(passwordToSet);

        assertEquals("Ensure that it was set as expected on the backing profile info instance", passwordToSet, this.beanToTest.getWrappedProfileInfo().getPassword());

        NullPointerException expectedException = null;
        try {
            this.beanToTest.setProfilePassword(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("testSetProfilePassword - Ensure NPE thrown when setting null password", expectedException);
    }
    
    public void testGetWrappedProfileInfo() {
        assertNotNull("Ensure wrapped profile info is not null", this.beanToTest.getWrappedProfileInfo());
    }

}