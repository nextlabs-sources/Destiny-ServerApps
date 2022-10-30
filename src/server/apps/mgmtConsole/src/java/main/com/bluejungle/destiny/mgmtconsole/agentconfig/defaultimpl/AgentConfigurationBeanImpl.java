/*
 * Created on Apr 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentTypeBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IExistingProfileBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.INewProfileBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IProfileBean;
import com.bluejungle.destiny.services.management.AgentServiceStub;
import com.bluejungle.destiny.services.management.BadArgumentFault;
import com.bluejungle.destiny.services.management.CommProfileDTOQuery;
import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ProfileServiceStub;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.UniqueConstraintViolationFault;
import com.bluejungle.destiny.services.management.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.AgentTypeDTOList;
import com.bluejungle.destiny.services.management.types.CommProfileDTO;
import com.bluejungle.destiny.services.management.types.CommProfileDTOList;
import com.bluejungle.destiny.services.management.types.CommProfileDTOQueryField;
import com.bluejungle.destiny.services.management.types.CommProfileDTOQueryTerm;
import com.bluejungle.destiny.services.management.types.CommProfileDTOQueryTermSet;
import com.bluejungle.destiny.services.management.types.CommProfileInfo;
import com.bluejungle.destiny.webui.controls.UITabbedPane;
import com.bluejungle.destiny.webui.framework.faces.CommonSelectItemResourceLists;
import com.bluejungle.destiny.webui.framework.faces.IResetableBean;
import com.bluejungle.destiny.webui.framework.faces.UIInputUtils;
import com.bluejungle.domain.types.AgentTypeDTO;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;


import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * DEVELOPER NOTE: Don't use this class as a pattern. Prefer ActionListeners
 * over Actions and put them in a seperate class rather than on the bean
 */

/**
 * Implementation of the IAgentConfigurationBean interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/AgentConfigurationBeanImpl.java#3 $
 */
public class AgentConfigurationBeanImpl implements IAgentConfigurationBean, IResetableBean {

    private static final Log LOG = LogFactory.getLog(AgentConfigurationBeanImpl.class.getName());

    private static final String AGENT_SERVICE_LOCATION_SERVLET_PATH = "/services/AgentService";
    private static final String PROFILE_SERVICE_LOCATION_SERVLET_PATH = "/services/ProfileService";
    private static final Object SELECTED_PROFILE_ID_PARAM_NAME = "selectedProfileId";

    private static final String AGENT_CONFIG_ACTION = "agentConfig";

    private static final String HOSTS_TAB_NAME = "hostsTab";
    private static final String SETTINGS_TAB_NAME = "settingsTab";

    private Set<IProfileBean> profilesWithLoadedHosts = new HashSet<IProfileBean>();
    private final Map<IAgentTypeBean, LinkedHashMap<Long, IProfileBean>> idToProfilesMaps = new HashMap<IAgentTypeBean, LinkedHashMap<Long, IProfileBean>>();
    private final Map<IAgentTypeBean, DataModel> cachedProfilesDataModel = new HashMap<IAgentTypeBean, DataModel>();
    private final List<IAgentTypeBean> agentTypes = new LinkedList<IAgentTypeBean>();
    private final Map<String, IAgentTypeBean> agentTypeIdToBeanMap = new HashMap<String, IAgentTypeBean>();
    private final Map<IAgentTypeBean, IProfileBean> currentlySelectedProfile = new HashMap<IAgentTypeBean, IProfileBean>();
    private IAgentTypeBean currentAgentType;
    private ProfileServiceStub profileService;
    private AgentServiceStub agentService;
    private boolean hostsTabSelected = false;
    private int maxHostsToDisplay = -1;
    private String hostSearchString = "";

    // DABS component URLs are cached here to avoid lookup for every profile
    private List dabsComponentURLs;

    /**
     * Action called before the agent configuration page is loaded
     * 
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    public void prerender() throws RemoteException, ServiceNotReadyFault, BadArgumentFault, UnauthorizedCallerFault, CommitFault {

        initializeIfFirstUse();

        if (!this.cachedProfilesDataModel.containsKey(getAgentType())) {
            loadProfileData();
            selectFirstProfile();
        }

        if (this.hostsTabSelected) {
            IProfileBean selectedProfile = getSelectedProfile();
            if ((!selectedProfile.isNew()) && (!this.profilesWithLoadedHosts.contains(selectedProfile))) {
                ((ProfileBeanImpl) selectedProfile).loadHosts(this.hostSearchString, this.maxHostsToDisplay);
                this.profilesWithLoadedHosts.add(selectedProfile);
            }
        }

    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getAgentTypes()
     */
    public List<IAgentTypeBean> getAgentTypes() {
        return this.agentTypes;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getProfiles()
     */
    public DataModel getProfiles() {
        return this.cachedProfilesDataModel.get(this.getAgentType());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getSelectedProfile()
     */
    public IProfileBean getSelectedProfile() {
        return this.currentlySelectedProfile.get(this.getAgentType());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getAgentType()
     */
    public IAgentTypeBean getAgentType() {
        /*
         * This is a bit of a hack. Due to the nature of the menu items for this
         * page, the agent type is required to be available before the
         * prerender() method is called. Therefore we do some initialization
         * here if necessary
         */
        try {
            initializeIfFirstUse();
        } catch (ServiceNotReadyFault | RemoteException exception) {
            getLog().warn("Unable to initialize agent configuration java bean", exception);
        }

        return this.currentAgentType;
    }

    /**
     * Set the agent type for this agent configuration bean.
     * 
     * @param agentTypeId
     */
    public void setAgentType(String type) {
        if (type == null) {
            throw new NullPointerException("currentAgentType cannot be null.");
        }

        if (!this.agentTypeIdToBeanMap.containsKey(type)) {
            throw new IllegalArgumentException(type + " is not a valid agent type");
        }

        this.currentAgentType = this.agentTypeIdToBeanMap.get(type);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getHostsTabName()
     */
    public String getHostsTabName() {
        return HOSTS_TAB_NAME;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getSettingsTabName()
     */
    public String getSettingsTabName() {
        return SETTINGS_TAB_NAME;
    }

    /**
     * 
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getMaxHostsToDisplayOptions()
     */
    public List getMaxHostsToDisplayOptions() {
        Locale currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return CommonSelectItemResourceLists.MAX_UI_ELEMENT_LIST_SIZE_SELECT_ITEMS.getSelectItemResources(currentLocal);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getMaxHostsToDisplay()
     */
    public int getMaxHostsToDisplay() {
        return this.maxHostsToDisplay;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#setMaxHostsToDisplay(int)
     */
    public void setMaxHostsToDisplay(int maxHostsToDisplay) {
        if (maxHostsToDisplay < 0) {
            throw new IllegalArgumentException("Maximum Hosts to Display must be 0 or greater");
        }

        if (maxHostsToDisplay != this.maxHostsToDisplay) {
            this.maxHostsToDisplay = maxHostsToDisplay;
            this.profilesWithLoadedHosts.clear();
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#getHostSearchString()
     */
    public String getHostSearchString() {
        return this.hostSearchString;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#setHostSearchString(java.lang.String)
     */
    public void setHostSearchString(String hostSearchString) {
        if (hostSearchString == null) {
            throw new NullPointerException("hostSearchString cannot be null.");
        }

        if (!hostSearchString.equals(this.hostSearchString)) {
            this.hostSearchString = hostSearchString;
            this.profilesWithLoadedHosts.clear();
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#setHostTabSelected(boolean)
     */
    public void setHostTabSelected(boolean selected) {
        this.hostsTabSelected = selected;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#handleSelectProfileActionEvent(javax.faces.event.ActionEvent)
     */
    public String handleSelectProfileActionEvent() {
        if (this.getSelectedProfile().isNew()) {
            clearNewProfile();
        }

        FacesContext currentContext = FacesContext.getCurrentInstance();
        Map requestParameters = currentContext.getExternalContext().getRequestParameterMap();
        String selectedProfileId = (String) requestParameters.get(SELECTED_PROFILE_ID_PARAM_NAME);
        if (selectedProfileId != null) {
            IAgentTypeBean agentType = this.getAgentType();
            Map idToProfileMap = this.idToProfilesMaps.get(agentType);
            IProfileBean profileToSetSelected = (IProfileBean) idToProfileMap.get(Long.valueOf(selectedProfileId));
            if (profileToSetSelected != null) {
                this.currentlySelectedProfile.put(agentType, profileToSetSelected);
                UIInputUtils.resetUIInput(currentContext);
            } else {
                getLog().warn("Unexpected state in Agent configuration page - selected Profile ID does not match an existing profile.");
                selectFirstProfile();
            }
        } else {
            // If it is null, not much that can be done except logging it
            getLog().warn("Unexpected state in Agent configuration page - selected Profile ID was null.");
            selectFirstProfile();
        }

        return getAgentConfigAction();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#addNewProfile()
     */
    public String addNewProfile() {
        // Clear new profile if one already exists
        clearNewProfile();

        IProfileBean newProfile = new NewProfileBeanImpl(this.currentAgentType, getDABSComponentURLs());
        this.currentlySelectedProfile.put(getAgentType(), newProfile);
        IAgentTypeBean agentTypeBean = getAgentType();
        List<IProfileBean> rawList = (List<IProfileBean>) this.cachedProfilesDataModel.get(agentTypeBean).getWrappedData();
        rawList.add(newProfile);

        FacesContext currentContext = FacesContext.getCurrentInstance();
        UIInputUtils.resetUIInput(currentContext);

        /*
         * This is a hack which I couldn't find a way to avoid. We search for
         * the tabbed pane compnent and set the selected tab to the settings
         * tab. The following alternatives were attempted: 1. Using a component
         * binding from this bean. Didn't work due to use of the AliasBean
         * component on the page
         * 
         * 2. Passing a parameter to the UITabbedPane. Didn't work due to the
         * fact that the tabbed pane must be nested within a form. The form
         * prohibits the tabbed pane from receiving the request parameter
         * 
         * 2-24-07 - SDG - Revisit when there's time. The AliasBean has been
         * removed, so using a component binding may be best solution
         */
        UIViewRoot viewRoot = currentContext.getViewRoot();
        UITabbedPane tabbedPane = findTabbedPane(viewRoot);
        tabbedPane.setSelectedTab(SETTINGS_TAB_NAME);

        return null;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#cancelAddNewProfile()
     */
    public String cancelAddNewProfile() {
        clearNewProfile();
        selectFirstProfile();

        return getAgentConfigAction();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#saveSelectedProfile()
     */
    public String saveSelectedProfile() {
        String actionToReturn = null;

        IProfileBean selectedProfile = getSelectedProfile();
        try {
            IExistingProfileBean storedProfile = null;
            if (selectedProfile.isNew()) {
                storedProfile = performProfileInsert((INewProfileBean) selectedProfile);
            } else {
                IExistingProfileBean beanToUpdate = (IExistingProfileBean) selectedProfile;
                performProfileUpdate(beanToUpdate);
                storedProfile = beanToUpdate;
            }
            MessageHelper.addProfileStoreSuccessMessage();

            try {
                this.resetAndSelectProfile(this.getAgentType(), new Long(storedProfile.getProfileId()));
            } catch (ServiceNotReadyFault | UnauthorizedCallerFault | RemoteException exception) {
                getLog().error("Failed to load data after profile save", exception);
                MessageHelper.addProfileLoadErrorMessage();
            }
        } catch (UniqueConstraintViolationFault exception) {
            // The name is not unique
            MessageHelper.addUniqueNameViolationMessage();
        } catch (ServiceNotReadyFault | CommitFault | UnknownEntryFault | RemoteException exception) {
            getLog().error("Failed to save new profile", exception);
            MessageHelper.addProfileSaveErrorMessage();
        } catch (UnauthorizedCallerFault exception) {
            getLog().error("Failed to save new profile", exception);
            MessageHelper.addUserUnauthorizedErrorMessage();
        }

        return actionToReturn;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean#deleteSelectedProfile()
     */
    public String deleteSelectedProfile() throws RemoteException {
        try {
            ProfileServiceStub profileService = getProfileService();
            ProfileBeanImpl selectedProfile = (ProfileBeanImpl) getSelectedProfile();
            profileService.removeCommProfile(selectedProfile.getProfileId());

            this.reset();

            MessageHelper.addProfileDeleteSuccessMessage();
        } catch (ServiceNotReadyFault | UnauthorizedCallerFault | UnknownEntryFault | AxisFault exception) {
            MessageHelper.addProfileDeleteErrorMessage();
            getLog().warn("Profile deletion failed.", exception);
        }

        return getAgentConfigAction();
    }

    /**
     * Reset the cached data stored in this bean and set the profile with the
     * specified profile id as the selected profile
     * 
     * @param agentTypeToReset
     * 
     * @param profileId
     * @throws ServiceNotReadyFault
     * @throws UnauthorizedCallerFault
     * @throws RemoteException
     */
    public void resetAndSelectProfile(IAgentTypeBean agentTypeToReset, Long profileId) throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, CommitFault {
        this.cachedProfilesDataModel.remove(agentTypeToReset);
        this.idToProfilesMaps.remove(agentTypeToReset);

        /* Not terribly efficient. Should change this map to be by agent type */
        this.profilesWithLoadedHosts.clear();

        this.loadProfileData();
        IAgentTypeBean agentType = this.getAgentType();
        Map<Long, IProfileBean> idToProfileMap = this.idToProfilesMaps.get(agentType);
        this.currentlySelectedProfile.put(agentType, idToProfileMap.get(profileId));

        UIInputUtils.resetUIInput(FacesContext.getCurrentInstance());
    }

    /**
     * Reset the cached data stored in this bean
     */
    public void reset() {
        this.cachedProfilesDataModel.clear();
        this.currentlySelectedProfile.clear();
        this.idToProfilesMaps.clear();
        this.profilesWithLoadedHosts.clear();
        this.dabsComponentURLs = null;
        this.maxHostsToDisplay = -1;
        this.agentTypes.clear();
    }

    /**
     * Retrieve the collection of dabs
     * 
     * Note: This method is protected for unit testing purposes only. Yes, I
     * know, not ideal. I don't like it, either, but I did it to save time. I
     * got myself in trouble by going against my prinicpal of avoiding static
     * utility classes.
     * 
     * @return
     */
    protected List getDABSComponentURLs() {
        List collectionToReturn = null;
        if (this.dabsComponentURLs == null) {
            try {
                this.dabsComponentURLs = ProfileBeanUtils.getDABSComponentsURLs();
                collectionToReturn = this.dabsComponentURLs;
            } catch (ServiceNotReadyFault exception) {
                getLog().error("Failed to load dabs component urls.  DMS not ready", exception);
                /**
                 * I generally don't like to hide error from end user. However,
                 * in this case, the page can still be displayed and the dabs
                 * url can be entered manually. Therefore, an error message is a
                 * little aggressive.
                 */
                collectionToReturn = Collections.EMPTY_LIST;
            } catch (RemoteException exception) {
                getLog().error("Failed to load dabs component urls", exception);
                collectionToReturn = Collections.EMPTY_LIST;
            } catch (CommitFault exception) {
                getLog().error("Failed to commit dabs component urls", exception);
                collectionToReturn = Collections.EMPTY_LIST;
            } catch (UnauthorizedCallerFault exception) {
                getLog().error("Unauthorized caller to dabs component urls", exception);
                collectionToReturn = Collections.EMPTY_LIST;
            }
        } else {
            collectionToReturn = this.dabsComponentURLs;
        }

        return collectionToReturn;
    }

    /**
     * Initialization if first use
     * 
     * @throws ServiceException
     * @throws RemoteException
     * @throws ServiceNotReadyFault
     */
    private void initializeIfFirstUse() throws ServiceNotReadyFault, RemoteException {
        if (this.maxHostsToDisplay == -1) {
            SelectItem firstMaximumSelectableItemsMenuOption = (SelectItem) getMaxHostsToDisplayOptions().iterator().next();
            this.maxHostsToDisplay = ((Integer) firstMaximumSelectableItemsMenuOption.getValue()).intValue();
            loadAgentTypes();
            this.currentAgentType = this.agentTypes.get(0);
        }
    }

    /**
     * Find the first tabbed pane component nested within the specified UI
     * component
     * 
     * @param component
     *            Either the tabbed pane component or a parent of the tabbed
     *            pane component
     * @return the first tabbed pane component found
     * @throws IllegalStateException
     *             if the tabbed pane cannot be found
     */
    private UITabbedPane findTabbedPane(UIComponent component) {
        UITabbedPane tabbedPaneFound = findTabbedPaneImpl(component);
        if (tabbedPaneFound == null) {
            throw new IllegalStateException("Tabbed pane could not be found.");
        }

        return tabbedPaneFound;
    }

    /**
     * Find the first tabbed pane component nested within the specified UI
     * component
     * 
     * @param component
     *            Either the tabbed pane component or a parent of the tabbed
     *            pane component
     * @return the first tabbed pane component found or null if the tabbed pane
     *         cannot be found
     */
    private UITabbedPane findTabbedPaneImpl(UIComponent component) {
        UITabbedPane tabbedPaneToReturn = null;

        if (component instanceof UITabbedPane) {
            tabbedPaneToReturn = (UITabbedPane) component;
        } else {
            List componentChildren = component.getChildren();
            Iterator childIterator = componentChildren.iterator();
            while (childIterator.hasNext() && (tabbedPaneToReturn == null)) {
                tabbedPaneToReturn = findTabbedPaneImpl((UIComponent) childIterator.next());
            }
        }

        return tabbedPaneToReturn;
    }

    /**
     * Retrieve the action to return which dictates the profile configueration
     * create page location
     * 
     * @return the action to return which dictates the profle create page
     *         location
     */
    private String getAgentConfigAction() {
        return AGENT_CONFIG_ACTION;
    }

    /**
     * Retrieve the Agent Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the Agent Service interface
     * @throws ServiceException
     *             if the agent service interface could not be located
     */
    protected AgentServiceStub getAgentService() throws AxisFault {
        if (this.agentService == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += AGENT_SERVICE_LOCATION_SERVLET_PATH;

            this.agentService = new AgentServiceStub(location);
        }

        return this.agentService;
    }

    /**
     * Retrieve the Profile Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the Profile Service interface
     */
    protected ProfileServiceStub getProfileService() throws AxisFault {
        if (this.profileService == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += PROFILE_SERVICE_LOCATION_SERVLET_PATH;

            this.profileService = new ProfileServiceStub(location);
        }

        return this.profileService;
    }

    /**
     * Load the Agent Types list
     * 
     */
    private void loadAgentTypes() throws ServiceNotReadyFault, RemoteException {
        AgentServiceStub agentService = getAgentService();
        AgentTypeDTOList agentTypesList = agentService.getAgentTypes();
        com.bluejungle.destiny.services.management.types.AgentTypeDTO[] agentTypes = agentTypesList.getAgentTypes();
        for (int i = 0; i < agentTypes.length; i++) {
            com.bluejungle.destiny.services.management.types.AgentTypeDTO nextAgentType = agentTypes[i];
            AgentTypeBeanImpl agentTypeBean = new AgentTypeBeanImpl(nextAgentType);
            this.agentTypes.add(agentTypeBean);
            this.agentTypeIdToBeanMap.put(agentTypeBean.getAgentTypeId(), agentTypeBean);
        }
    }

    /**
     * Load the data to provide to teh display layer
     * 
     * @throws ServiceNotReadyFault
     * @throws UnauthorizedCallerFault
     * @throws RemoteException
     */
    private void loadProfileData() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, CommitFault {
        CommProfileDTOList profileList = getProfileList();
        IAgentTypeBean currentAgentType = this.getAgentType();
        DataModel profilesToCache = processProfileList(profileList);
        this.cachedProfilesDataModel.put(currentAgentType, profilesToCache);
    }

    /**
     * Retrieve the list of profiles from the management server
     * 
     * @return the list of profiles from the management server
     * @throws RemoteException
     * @throws UnauthorizedCallerFault
     * @throws ServiceNotReadyFault
     */
    private CommProfileDTOList getProfileList() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, CommitFault {
        ProfileServiceStub profileService = getProfileService();

        CommProfileDTOQueryTerm[] queryTerms = new CommProfileDTOQueryTerm[1];
        queryTerms[0] = new CommProfileDTOQueryTerm();
        queryTerms[0].setCommProfileDTOQueryField(CommProfileDTOQueryField.agentType);
        queryTerms[0].setValue(AgentTypeDTO.Factory.fromValue(getAgentType().getAgentTypeId()));

        CommProfileDTOQueryTermSet commProfileQueryTermSet = new CommProfileDTOQueryTermSet();
        commProfileQueryTermSet.setCommProfileDTOQueryTerm(queryTerms);

        CommProfileDTOQuery commProfileDTOQuery = new CommProfileDTOQuery();
        commProfileDTOQuery.setCommProfileDTOQueryTermSet(commProfileQueryTermSet);
        commProfileDTOQuery.setFetchSize(Integer.MAX_VALUE);
        commProfileDTOQuery.setSortField(CommProfileDTOQueryField.name);

        return profileService.getCommProfiles(commProfileDTOQuery);
    }

    /**
     * Process the profile list to be in a state which can be returned to the
     * display layer
     * 
     * @param profileList
     *            the profile list to process
     */
    private DataModel processProfileList(CommProfileDTOList profileList) {
        DataModel modelToReturn = null;

        /**
         * Note that we're iterating through all elements to build DataModel.
         * This should be okay, as we don't expect too many profiles. In
         * addition, we need to build the id to profile map. Otherwise, we could
         * build a DataModel which creates the ProfileBean's as the data is
         * iterated through in the UI (See ComponentStatusBean)
         */
        IAgentTypeBean agentType = getAgentType();
        List<IExistingProfileBean> commProfileList = new LinkedList<IExistingProfileBean>();
        CommProfileDTO[] commProfiles = profileList.getCommProfileDTO();
        LinkedHashMap<Long, IProfileBean> idToProfileMap = new LinkedHashMap<Long, IProfileBean>();
        for (int i = 0; i < commProfiles.length; i++) {
            CommProfileDTO nextCommProfile = commProfiles[i];
            IExistingProfileBean nextProfile = new ProfileBeanImpl(nextCommProfile, getDABSComponentURLs());
            idToProfileMap.put(new Long(nextProfile.getProfileId()), nextProfile);
            commProfileList.add(nextProfile);
        }

        this.idToProfilesMaps.put(agentType, idToProfileMap);

        modelToReturn = new ListDataModel(commProfileList);

        return modelToReturn;
    }

    /**
     * Set the first profile in the list to be the selected profile
     */
    private void selectFirstProfile() {
        IAgentTypeBean agentType = this.getAgentType();
        Map<Long, IProfileBean> idToProfileMap = this.idToProfilesMaps.get(agentType);
        Iterator<Map.Entry<Long, IProfileBean>> profileBeanIterator = idToProfileMap.entrySet().iterator();
        if (profileBeanIterator.hasNext()) {
            Map.Entry<Long, IProfileBean> firstEntry = profileBeanIterator.next();
            this.currentlySelectedProfile.put(agentType, firstEntry.getValue());
            UIInputUtils.resetUIInput(FacesContext.getCurrentInstance());
        } else {
            // Not much we can do here. An error must have occured loading the
            // profiles
        }
    }

    /**
     * When a new profile is being created, a menu item is added to the profile
     * list which the title, "New Profile". this method clears that menu item
     */
    private void clearNewProfile() {
        IProfileBean selectedProfile = this.getSelectedProfile();
        if (selectedProfile.isNew()) {
            IAgentTypeBean agentType = getAgentType();
            List rawList = (List) this.cachedProfilesDataModel.get(agentType).getWrappedData();
            rawList.remove(selectedProfile);
        }
    }

    /**
     * Perform the insertion of a new profile
     * 
     * @param profileBeanWithProfileToCreate
     *            the profile to insert
     * @return a bean representing the created profile
     * @throws ServiceNotReadyFault
     * @throws CommitFault
     * @throws UniqueConstraintViolationFault
     * @throws UnauthorizedCallerFault
     * @throws RemoteException
     */
    private IExistingProfileBean performProfileInsert(INewProfileBean profileBeanWithProfileToCreate) throws ServiceNotReadyFault, CommitFault, UniqueConstraintViolationFault, UnauthorizedCallerFault, RemoteException, UnknownEntryFault {
        ProfileServiceStub profileService = getProfileService();
        CommProfileInfo commProfileInfo = ((NewProfileBeanImpl) profileBeanWithProfileToCreate).getWrappedProfileInfo();
        CommProfileDTO profileCreated = profileService.addCommProfile(commProfileInfo);
        return new ProfileBeanImpl(profileCreated, getDABSComponentURLs());
    }

    /**
     * Store the updates to the specified profile
     * 
     * @param profileBeanWithProfileToUpdate
     *            the profile to update
     * @throws ServiceNotReadyFault
     * @throws UnknownEntryFault
     * @throws UnauthorizedCallerFault
     * @throws RemoteException
     */
    private void performProfileUpdate(IExistingProfileBean profileBeanWithProfileToUpdate) throws ServiceNotReadyFault, UnknownEntryFault, UnauthorizedCallerFault, RemoteException, CommitFault {
        ProfileServiceStub profileService = getProfileService();
        CommProfileDTO commProfileToUpdate = ((ProfileBeanImpl) profileBeanWithProfileToUpdate).getWrappedProfileDTO();
        profileService.updateCommProfile(commProfileToUpdate);
    }

    /**
     * Retrieve a Log instance to log information
     */
    private static Log getLog() {
        return LOG;
    }
}
