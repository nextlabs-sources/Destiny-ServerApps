/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.framework.types.CommitFault;
import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.framework.types.TimeIntervalDTO;
import com.bluejungle.destiny.framework.types.TimeUnits;
import com.bluejungle.destiny.framework.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.framework.types.UniqueConstraintViolationFault;
import com.bluejungle.destiny.framework.types.UnknownEntryFault;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentTypeBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IExistingProfileBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.INewProfileBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean;
import com.bluejungle.destiny.services.management.AgentProfileDTOQuery;
import com.bluejungle.destiny.services.management.AgentServiceIF;
import com.bluejungle.destiny.services.management.CommProfileDTOQuery;
import com.bluejungle.destiny.services.management.ProfileServiceIF;
import com.bluejungle.destiny.services.management.UserProfileDTOQuery;
import com.bluejungle.destiny.services.management.types.ActionTypeDTO;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTO;
import com.bluejungle.destiny.services.management.types.ActivityJournalingSettingsDTOList;
import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.services.management.types.AgentDTOQuerySpec;
import com.bluejungle.destiny.services.management.types.AgentProfileDTO;
import com.bluejungle.destiny.services.management.types.AgentProfileDTOList;
import com.bluejungle.destiny.services.management.types.AgentProfileInfo;
import com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO;
import com.bluejungle.destiny.services.management.types.AgentStatistics;
import com.bluejungle.destiny.services.management.types.AgentTypeDTOList;
import com.bluejungle.destiny.services.management.types.CommProfileDTO;
import com.bluejungle.destiny.services.management.types.CommProfileDTOList;
import com.bluejungle.destiny.services.management.types.CommProfileInfo;
import com.bluejungle.destiny.services.management.types.UserProfileDTO;
import com.bluejungle.destiny.services.management.types.UserProfileDTOList;
import com.bluejungle.destiny.services.management.types.UserProfileInfo;
import com.bluejungle.destiny.webui.controls.UITabbedPane;
import com.bluejungle.destiny.webui.framework.faces.CommonSelectItemResourceLists;
import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.domain.agenttype.AgentTypeEnumType;
import com.bluejungle.domain.types.ActionTypeDTOList;
import com.bluejungle.domain.types.AgentTypeDTO;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;

import org.apache.axis.types.Token;
import org.apache.axis.types.UnsignedShort;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.xml.rpc.ServiceException;

import java.math.BigInteger;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test for AgentConfigurationBeanImpl
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/AgentConfigurationBeanImplTest.java#1 $
 */
public class AgentConfigurationBeanImplTest extends TestCase {

    private List dabsComponentURLs;
    private AgentConfigurationBeanImpl beanToTest;
    private CommProfileDTO[] testProfiles;
    private TestProfileServiceIFImpl testProfileService;
    private MockFacesContext mockFacesContext;
    private MockAgentQueryBroker mockAgentQueryBroker;
    private TestAgentServiceIFImpl testAgentService;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AgentConfigurationBeanImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        ComponentInfo mockAgentBrokerInfo = new ComponentInfo(AgentQueryBroker.COMPONENT_NAME, MockAgentQueryBroker.class.getName(), LifestyleType.SINGLETON_TYPE);
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        componentManager.registerComponent(mockAgentBrokerInfo, true);
        this.mockAgentQueryBroker = (MockAgentQueryBroker) componentManager.getComponent(AgentQueryBroker.COMPONENT_NAME);

        this.dabsComponentURLs = Collections.singletonList(new SelectItem(new URL("http://www.foo.com"), "Foo"));

        this.beanToTest = new TestAgentConfigurationBeanImpl(this.dabsComponentURLs);

        this.testProfiles = new CommProfileDTO[3];
        CommProfileDTO profileOne = new CommProfileDTO();
        profileOne.setId(0l);
        profileOne.setName(new Token("profileOne"));
        profileOne.setHeartBeatFrequency(new TimeIntervalDTO(new UnsignedShort(3), TimeUnits.days));
        profileOne.setLogFrequency(new TimeIntervalDTO(new UnsignedShort(3), TimeUnits.days));
        profileOne.setCustomActivityJournalingSettings(new ActivityJournalingSettingsDTO("one", new ActionTypeDTOList()));
        profileOne.setCurrentActivityJournalingSettings(new ActivityJournalingSettingsDTO("one", new ActionTypeDTOList()));
        profileOne.setAgentType(AgentTypeDTO.DESKTOP);
        testProfiles[0] = profileOne;

        CommProfileDTO profileTwo = new CommProfileDTO();
        profileTwo.setId(1l);
        profileTwo.setName(new Token("profileTwo"));
        profileTwo.setHeartBeatFrequency(new TimeIntervalDTO(new UnsignedShort(300), TimeUnits.seconds));
        profileTwo.setLogFrequency(new TimeIntervalDTO(new UnsignedShort(3), TimeUnits.days));
        profileTwo.setCustomActivityJournalingSettings(new ActivityJournalingSettingsDTO("two", new ActionTypeDTOList()));
        profileTwo.setCurrentActivityJournalingSettings(new ActivityJournalingSettingsDTO("two", new ActionTypeDTOList()));
        profileTwo.setAgentType(AgentTypeDTO.DESKTOP);
        testProfiles[1] = profileTwo;

        CommProfileDTO profileThree = new CommProfileDTO();
        profileThree.setId(2l);
        profileThree.setName(new Token("profileThree"));
        profileThree.setHeartBeatFrequency(new TimeIntervalDTO(new UnsignedShort(57), TimeUnits.minutes));
        profileThree.setLogFrequency(new TimeIntervalDTO(new UnsignedShort(3), TimeUnits.days));
        profileThree.setCustomActivityJournalingSettings(new ActivityJournalingSettingsDTO("three", new ActionTypeDTOList()));
        profileThree.setCurrentActivityJournalingSettings(new ActivityJournalingSettingsDTO("three", new ActionTypeDTOList()));
        profileThree.setAgentType(AgentTypeDTO.FILE_SERVER);
        testProfiles[2] = profileThree;

        this.testProfileService = new TestProfileServiceIFImpl();

        this.testAgentService = new TestAgentServiceIFImpl();
        
        this.mockFacesContext = new MockFacesContext();
        MockExternalContext extCtx = new MockExternalContext("/myApp");
        this.mockFacesContext.setExternalContext(extCtx);
        UIViewRoot viewRoot = this.mockFacesContext.getViewRoot();
        viewRoot.setLocale(Locale.US);

        // The Agent configuration bean requires a tabbed pane for some methods
        List viewRootChildren = viewRoot.getChildren();
        viewRootChildren.add(new UITabbedPane());
    }

    /**
     * Test prerender(), reset() and resetAndSelectProfile()
     * 
     * @throws ServiceNotReadyFault
     * @throws UnauthorizedCallerFault
     * @throws RemoteException
     * @throws ServiceException
     */
    public void testPrerenderAndReset() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        // Ensure it doesn't explode
        this.beanToTest.prerender();

        // Ensure that the profile service was called and the hosts for the
        // selected profile were not retrieve
        assertTrue("Ensure profile service was called", this.testProfileService.wasGetCommProfilesCalled());
        assertFalse("Ensure the hosts for the select profile were not retrieved", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());

        // Try again, test caching
        this.beanToTest.prerender();

        // Ensure that the service was not called and the hosts for the selected
        // profile were not retrieve
        assertFalse("Ensure profile service was not called", this.testProfileService.wasGetCommProfilesCalled());
        assertFalse("Ensure the hosts for the select profile were not retrieved", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());

        this.beanToTest.setHostTabSelected(true);
        this.beanToTest.prerender();

        // Ensure that the service was not called and the hosts for the selected
        // profile were not retrieve
        assertFalse("Ensure profile service was not called", this.testProfileService.wasGetCommProfilesCalled());
        assertTrue("Ensure the hosts for the select profile were retrieved", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());

        // Test reset
        this.beanToTest.reset();
        this.beanToTest.prerender();
        // Ensure that the service was not called
        assertTrue("Ensure reset lead to retrieval of profiles", this.testProfileService.wasGetCommProfilesCalled());
        assertEquals("Ensure the first profile selected", this.testProfiles[0].getName(), this.beanToTest.getSelectedProfile().getProfileTitle());
        assertTrue("Ensure the hosts for the select profile were retrieved after reset", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());

        // Test reset and select profile
        this.beanToTest.resetAndSelectProfile(this.beanToTest.getAgentType(), new Long(this.testProfiles[2].getId()));
        this.beanToTest.prerender();
        // Ensure that the service was not called
        assertTrue("Ensure reset and select profile lead to retrieval of profiles", this.testProfileService.wasGetCommProfilesCalled());
        assertEquals("Ensure the profile selected as expected", this.testProfiles[2].getName(), this.beanToTest.getSelectedProfile().getProfileTitle());
        assertTrue("Ensure the hosts for the select profile were retrieved after reset and select", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());

        this.beanToTest.setHostTabSelected(false);
        // Test reset and select profile without host tab selected
        this.beanToTest.resetAndSelectProfile(this.beanToTest.getAgentType(), new Long(this.testProfiles[2].getId()));
        this.beanToTest.prerender();
        // Ensure that the service was not called
        assertTrue("Ensure reset and select profile lead to retrieval of profiles", this.testProfileService.wasGetCommProfilesCalled());
        assertEquals("Ensure the profile selected as expected", this.testProfiles[2].getName(), this.beanToTest.getSelectedProfile().getProfileTitle());
        assertFalse("Ensure the hosts for the select profile were not retrieved after reset and select with host tab not currently selected", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());
    }

    
    public void testGetProfiles() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();

        // Test that the test profiles are retrieved and returned
        DataModel profiles = this.beanToTest.getProfiles();
        assertEquals("Ensure data model size is correct", this.testProfiles.length, profiles.getRowCount());
        for (int i = 0; i < this.testProfiles.length; i++) {
            profiles.setRowIndex(i);
            IProfileBean nextProfile = (IProfileBean) profiles.getRowData();
            assertEquals("Ensure retrieved profiles are correct and in correct order " + i, this.testProfiles[i].getName(), nextProfile.getProfileTitle());
        }
    }

    /**
     * Test both getSelectedProfile and handleSelectedProfileActionEvent()
     * 
     * @throws ServiceNotReadyFault
     * @throws UnauthorizedCallerFault
     * @throws RemoteException
     * @throws ServiceException
     */
    public void testSelectedProfile() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();

        IProfileBean selectedProfile = this.beanToTest.getSelectedProfile();
        assertEquals("Ensure the first profile initially selected", this.testProfiles[0].getName(), selectedProfile.getProfileTitle());

        MockExternalContext mockExtContext = (MockExternalContext) this.mockFacesContext.getExternalContext();
        Map parameterMap = mockExtContext.getRequestParameterMap();
        parameterMap.put("selectedProfileId", String.valueOf(this.testProfiles[2].getId()));

        this.beanToTest.handleSelectProfileActionEvent();

        selectedProfile = this.beanToTest.getSelectedProfile();
        assertEquals("Ensure the profile selected", this.testProfiles[2].getName(), selectedProfile.getProfileTitle());
    }

    public void testGetAgentTypes() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();
        List<IAgentTypeBean> agentTypes = this.beanToTest.getAgentTypes();
        assertEquals("testGetAgentTypes - Ensure correct number of agent types", 2, agentTypes.size());
    }
    
    public void testGetSetAgentType() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();
        this.beanToTest.setAgentType(AgentTypeEnumType.DESKTOP.toString());
        assertEquals("Ensure agent type set as expected one", AgentTypeEnumType.DESKTOP.toString(), this.beanToTest.getAgentType().getAgentTypeId());

        this.beanToTest.setAgentType(AgentTypeEnumType.FILE_SERVER.toString());
        assertEquals("Ensure agent type set as expected two", AgentTypeEnumType.FILE_SERVER.toString(), this.beanToTest.getAgentType().getAgentTypeId());
    }

    public void testGetHostsTabName() {
        assertNotNull("Ensure host tab name is not null", this.beanToTest.getHostsTabName());
    }

    public void testGetSettingsTabName() {
        assertNotNull("Ensure settings tab name is not null", this.beanToTest.getSettingsTabName());
    }

    public void testGetMaxHostsToDisplayOptions() {
        Locale currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        List expectedOptions = CommonSelectItemResourceLists.MAX_UI_ELEMENT_LIST_SIZE_SELECT_ITEMS.getSelectItemResources(currentLocal);
        assertEquals("Ensure max hosts to display options as expected", expectedOptions, this.beanToTest.getMaxHostsToDisplayOptions());
    }

    public void testGetSetMaxHostsToDisplay() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();
        int maxHostsToDisplayExpected = ((Integer)((SelectItem) this.beanToTest.getMaxHostsToDisplayOptions().get(0)).getValue()).intValue();
        assertEquals("Ensure max hosts to display initialy as expected", maxHostsToDisplayExpected, this.beanToTest.getMaxHostsToDisplay());

        // Now, set it and ensure it's set as expected
        maxHostsToDisplayExpected = 53;
        this.beanToTest.setMaxHostsToDisplay(maxHostsToDisplayExpected);
        assertEquals("Ensure max hosts to display set as expected", maxHostsToDisplayExpected, this.beanToTest.getMaxHostsToDisplay());

        // Check invalid value
        try {
            this.beanToTest.setMaxHostsToDisplay(-1);
            fail("Should throw IllegalArgumentException for negative max hosts to display");
        } catch (IllegalArgumentException exception) {
        }
        
        // Make sure that when we set max hosts to display, the host list is reloaded
        this.beanToTest.setHostTabSelected(true);
        this.beanToTest.prerender();
        assertTrue("Ensure the hosts were retrieved", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());
        this.beanToTest.prerender();
        assertFalse("Ensure the hosts were not retrieved", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());
        this.beanToTest.setMaxHostsToDisplay(52);
        this.beanToTest.prerender();
        assertTrue("Ensure the hosts were retrieved after max hosts set", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());                
    }

    public void testGetSetHostSearchString() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        String hostSearchStringExpected = IAgentConfigurationBean.EMPTY_HOST_SEARCH_STRING;
        assertEquals("Ensure host search string initially empty", hostSearchStringExpected, this.beanToTest.getHostSearchString());
        
        hostSearchStringExpected = "foobar";
        this.beanToTest.setHostSearchString(hostSearchStringExpected);
        assertEquals("Ensure host search string set as expected", hostSearchStringExpected, this.beanToTest.getHostSearchString());
        
        // Make sure that when we set host search string, the host list is reloaded
        this.beanToTest.setHostTabSelected(true);
        this.beanToTest.prerender();
        assertTrue("Ensure the hosts were retrieved", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());
        this.beanToTest.prerender();
        assertFalse("Ensure the hosts were not retrieved", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());
        this.beanToTest.setHostSearchString("foo");
        this.beanToTest.prerender();
        assertTrue("Ensure the hosts were retrieved after host string", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());                   
    }
    
    public void testSetHostTabSelected() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();
        assertFalse("Ensure the hosts were not retrieved", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());
        this.beanToTest.setHostTabSelected(true);
        this.beanToTest.prerender();
        assertTrue("Ensure the hosts were retrieved after setting host tab selected", this.mockAgentQueryBroker.wasGetAgentsForProfileInvoked());                   
    }
    
    /**
     * Tests addNewProfile() and cancelAddNewProfile()
     * 
     * @throws ServiceException
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws ServiceNotReadyFault
     */
    public void testNewProfile() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();

        String returnedAction = this.beanToTest.addNewProfile();
        assertTrue("Ensure new profile selected", this.beanToTest.getSelectedProfile() instanceof INewProfileBean);

        returnedAction = this.beanToTest.cancelAddNewProfile();
        assertTrue("Ensure existing profile selected", this.beanToTest.getSelectedProfile() instanceof IExistingProfileBean);
    }

    public void testSaveSelectedProfile() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();

        String returnedAction = this.beanToTest.saveSelectedProfile();
        assertNull("Ensure null action returned for save existing", returnedAction);
        assertTrue("Ensure update commprofile called", this.testProfileService.wasUpdateCommProfilesCalled());

        // now add new
        this.beanToTest.addNewProfile();
        returnedAction = this.beanToTest.saveSelectedProfile();
        assertNull("Ensure null action returned for save new", returnedAction);
        assertTrue("Ensure add commprofile called", this.testProfileService.wasAddCommProfilesCalled());

    }

    public void testDeleteSelectedProfile() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.beanToTest.prerender();

        this.beanToTest.deleteSelectedProfile();
        assertTrue("Ensure remove commprofile called", this.testProfileService.wasRemoveCommProfilesCalled());
    }

    private class TestAgentConfigurationBeanImpl extends AgentConfigurationBeanImpl {

        private List dabsComponentURLs;

        /**
         * Create an instance of TestAgentConfigurationBeanImpl
         * 
         * @param collection
         */
        public TestAgentConfigurationBeanImpl(List dabsComponentURLs) {
            this.dabsComponentURLs = dabsComponentURLs;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentConfigurationBeanImpl#getProfileService()
         */
        protected ProfileServiceIF getProfileServiceInterface() throws ServiceException {
            return AgentConfigurationBeanImplTest.this.testProfileService;
        }

        
        /**
         * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentConfigurationBeanImpl#getAgentService()
         */
        @Override
        protected AgentServiceIF getAgentServiceInterface() throws ServiceException {        
            return AgentConfigurationBeanImplTest.this.testAgentService;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentConfigurationBeanImpl#getDABSComponentURLs()
         */
        protected List getDABSComponentURLs() {
            return this.dabsComponentURLs;
        }
    }

    private class TestProfileServiceIFImpl implements ProfileServiceIF {

        private boolean getCommProfilesWasCalled = false;
        private boolean addCommProfileWasCalled = false;
        private boolean updateCommProfileWasCalled = false;
        private boolean removeCommProfileWasCalled = false;

        // FIX ME - Create base mock profile service in dms
        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#addAgentProfile(com.bluejungle.destiny.services.management.types.AgentProfileInfo)
         */
        public AgentProfileDTO addAgentProfile(AgentProfileInfo agentProfileInfo) throws RemoteException, ServiceNotReadyFault, CommitFault, UniqueConstraintViolationFault, UnauthorizedCallerFault {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#addCommProfile(com.bluejungle.destiny.services.management.types.CommProfileInfo)
         */
        public CommProfileDTO addCommProfile(CommProfileInfo commProfileInfo) throws RemoteException, ServiceNotReadyFault, CommitFault, UniqueConstraintViolationFault, UnauthorizedCallerFault {
            this.addCommProfileWasCalled = true;
            return AgentConfigurationBeanImplTest.this.testProfiles[0]; // Not
            // ideal,
            // but
            // should
            // work
        }

        private boolean wasAddCommProfilesCalled() {
            boolean valueToReturn = addCommProfileWasCalled;
            addCommProfileWasCalled = false;
            return valueToReturn;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#addUserProfile(com.bluejungle.destiny.services.management.types.UserProfileInfo)
         */
        public UserProfileDTO addUserProfile(UserProfileInfo userProfileInfo) throws RemoteException, ServiceNotReadyFault, CommitFault, UniqueConstraintViolationFault, UnauthorizedCallerFault {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#updateAgentProfile(com.bluejungle.destiny.services.management.types.AgentProfileDTO)
         */
        public void updateAgentProfile(AgentProfileDTO agentProfileDTO) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, UnauthorizedCallerFault {
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#updateCommProfile(com.bluejungle.destiny.services.management.types.CommProfileDTO)
         */
        public void updateCommProfile(CommProfileDTO commProfileDTO) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, UnauthorizedCallerFault {
            updateCommProfileWasCalled = true;
        }

        private boolean wasUpdateCommProfilesCalled() {
            boolean valueToReturn = updateCommProfileWasCalled;
            updateCommProfileWasCalled = false;
            return valueToReturn;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#updateUserProfile(com.bluejungle.destiny.services.management.types.UserProfileDTO)
         */
        public void updateUserProfile(UserProfileDTO userProfileDTO) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, UnauthorizedCallerFault {
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#getAgentProfiles(com.bluejungle.destiny.services.management.AgentProfileDTOQuery)
         */
        public AgentProfileDTOList getAgentProfiles(AgentProfileDTOQuery agentProfileQuery) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#getCommProfiles(com.bluejungle.destiny.services.management.CommProfileDTOQuery)
         */
        public CommProfileDTOList getCommProfiles(CommProfileDTOQuery commProfileQuery) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault {
            getCommProfilesWasCalled = true;
            return new CommProfileDTOList(AgentConfigurationBeanImplTest.this.testProfiles);
        }

        private boolean wasGetCommProfilesCalled() {
            boolean valueToReturn = getCommProfilesWasCalled;
            getCommProfilesWasCalled = false;
            return valueToReturn;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#getUserProfiles(com.bluejungle.destiny.services.management.UserProfileDTOQuery)
         */
        public UserProfileDTOList getUserProfiles(UserProfileDTOQuery userProfileQuery) throws RemoteException, ServiceNotReadyFault, UnauthorizedCallerFault {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#removeAgentProfile(long)
         */
        public void removeAgentProfile(long id) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, UnauthorizedCallerFault {
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#removeCommProfile(long)
         */
        public void removeCommProfile(long id) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, UnauthorizedCallerFault {
            removeCommProfileWasCalled = true;
        }

        /**
         * @return
         */
        public boolean wasRemoveCommProfilesCalled() {
            boolean valueToReturn = removeCommProfileWasCalled;
            removeCommProfileWasCalled = false;
            return valueToReturn;
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#removeUserProfile(long)
         */
        public void removeUserProfile(long id) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, UnauthorizedCallerFault {
        }

        /**
         * @see com.bluejungle.destiny.services.management.ProfileServiceIF#getActivityJournalingSettings()
         */
        public ActivityJournalingSettingsDTOList getActivityJournalingSettings(String agentTypeId) throws RemoteException, ServiceNotReadyFault {
            return null;
        }
    }
        
    /**
     * @author sgoldstein
     */
    private class TestAgentServiceIFImpl implements AgentServiceIF {
        private AgentTypeDTOList agentTypeDTOList;
        
        
        /**
         * Create an instance of TestAgentServiceIFImpl
         */
        public TestAgentServiceIFImpl() {
            com.bluejungle.destiny.services.management.types.AgentTypeDTO desktopAgentTypeDTO = new com.bluejungle.destiny.services.management.types.AgentTypeDTO("DESKTOP", "desktop", new com.bluejungle.destiny.services.management.types.ActionTypeDTOList(new ActionTypeDTO[0])); 
            com.bluejungle.destiny.services.management.types.AgentTypeDTO fileServerAgentTypeDTO = new com.bluejungle.destiny.services.management.types.AgentTypeDTO("FILE_SERVER", "file server", new com.bluejungle.destiny.services.management.types.ActionTypeDTOList(new ActionTypeDTO[0])); 
            com.bluejungle.destiny.services.management.types.AgentTypeDTO[] agentTypes = {desktopAgentTypeDTO, fileServerAgentTypeDTO};
            this.agentTypeDTOList = new AgentTypeDTOList(agentTypes);
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgentById(java.math.BigInteger)
         */
        public AgentDTO getAgentById(BigInteger arg0) throws RemoteException, UnknownEntryFault, ServiceNotReadyFault {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgents(com.bluejungle.destiny.services.management.types.AgentDTOQuerySpec)
         */
        public AgentQueryResultsDTO getAgents(AgentDTOQuerySpec arg0) throws RemoteException, ServiceNotReadyFault {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgentStatistics()
         */
        public AgentStatistics getAgentStatistics() throws RemoteException, ServiceNotReadyFault {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgentTypes()
         */
        public AgentTypeDTOList getAgentTypes() throws RemoteException, ServiceNotReadyFault {
            return this.agentTypeDTOList;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#setAgentProfile(java.math.BigInteger, java.lang.String)
         */
        public void setAgentProfile(BigInteger arg0, String arg1) throws RemoteException, UnknownEntryFault, ServiceNotReadyFault {
            // TODO Auto-generated method stub
            
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#setCommProfile(java.math.BigInteger, java.lang.String)
         */
        public void setCommProfile(BigInteger arg0, String arg1) throws RemoteException, UnknownEntryFault, ServiceNotReadyFault {
            // TODO Auto-generated method stub
            
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#setCommProfileForAgents(com.bluejungle.destiny.framework.types.IDList, java.math.BigInteger)
         */
        public void setCommProfileForAgents(IDList arg0, BigInteger arg1) throws RemoteException, UnknownEntryFault, ServiceNotReadyFault {
            // TODO Auto-generated method stub
            
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#unregisterAgent(java.math.BigInteger)
         */
        public void unregisterAgent(BigInteger arg0) throws RemoteException, UnauthorizedCallerFault, CommitFault, UnknownEntryFault, ServiceNotReadyFault {
            // TODO Auto-generated method stub
            
        }
        
    }
}
