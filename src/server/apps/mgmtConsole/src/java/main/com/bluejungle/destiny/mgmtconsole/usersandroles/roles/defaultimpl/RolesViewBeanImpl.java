/*
 * Created on May 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRoleBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.roles.RolesException;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.policy.types.DMSRoleData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.services.policy.types.SubjectDTOList;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;
import com.bluejungle.destiny.webui.framework.faces.IResetableBean;
import com.bluejungle.framework.comp.ComponentManagerFactory;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IInternalRolesViewBean}
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/RolesViewBeanImpl.java#2 $
 */

public class RolesViewBeanImpl implements IInternalRolesViewBean, IResetableBean {

    private SubjectToRoleMenuItemTranslatingDataModel roleMenuItems;
    private Long selectedRoleId;
    private Map roleBeanCache = new HashMap();

    /**
     * Performs actions necessary before render takes place
     * 
     * @throws ServiceException
     * @throws RemoteException
     */
    public void prerender() throws RemoteException, UserRoleServiceException {
        // FIX ME - Currently throwing exceptions. Better to catch and add error
        // message to page

        if (roleMenuItems == null) {
            loadRoleMenuItems();
            selectAndLoadFirstRole();
        } else {
            loadSelectedRoleIfNecessary();
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRolesViewBean#getRoles()
     */
    public DataModel getRoles() {
        return this.roleMenuItems;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRolesViewBean#getSelectedRole()
     */
    public IRoleBean getSelectedRole() {
        return (IRoleBean) this.roleBeanCache.get(this.selectedRoleId);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRolesViewBean#setSelectedRole(long)
     */
    public void setSelectedRole(long selectedRoleId) {
        this.selectedRoleId = new Long(selectedRoleId);        
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRolesViewBean#saveSelectedRole()
     */
    public void saveSelectedRole() throws RolesException {
        IInternalRoleBean selectedRole = (IInternalRoleBean) this.roleBeanCache.get(this.selectedRoleId);
        SubjectDTO roleSubject = selectedRole.getWrappedSubjectDTO();
        DMSRoleData roleData = selectedRole.getWrappedDMSRoleData();
        try {
            getRoleServiceFacade().updateRole(roleSubject, roleData);
        } catch (RemoteException | UserRoleServiceException exception) {
            throw new RolesException("Failed to update role with id, " + roleSubject.getId(), exception);
        }
    }

    
    /**
     * @see com.bluejungle.destiny.webui.framework.faces.IResetableBean#reset()
     */
    public void reset() {
        roleMenuItems = null;
        selectedRoleId = null;
        roleBeanCache = new HashMap();
    }
    
    /**
     * Load the list of role menu items
     * 
     * @throws RemoteException
     */
    private void loadRoleMenuItems() throws RemoteException, UserRoleServiceException {
        IRoleServiceFacade roleServiceFaces = getRoleServiceFacade();
        SubjectDTOList rawRoleMenuItems = roleServiceFaces.getAllRoles();
        SubjectDTO[] rawRoleMenuItemsArray = rawRoleMenuItems.getSubjects();
        if (rawRoleMenuItemsArray == null) {
            rawRoleMenuItemsArray = new SubjectDTO[0];
        }
        
		Arrays.sort(rawRoleMenuItemsArray, new Comparator<SubjectDTO>() {

			@Override
			public int compare(SubjectDTO o1, SubjectDTO o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

        this.roleMenuItems = new SubjectToRoleMenuItemTranslatingDataModel(rawRoleMenuItemsArray);
    }

    /**
     * Select the first role in the role menu item list
     * 
     * @throws RemoteException
     */
    private void selectAndLoadFirstRole() throws RemoteException, UserRoleServiceException {
        IInternalRoleMenuItemBean firstRoleMenuItem = (IInternalRoleMenuItemBean) this.roleMenuItems.getFirstMenuItem();
        this.selectedRoleId = new Long(firstRoleMenuItem.getRoleId());
        loadRoleForRoleMenuItem(firstRoleMenuItem);
    }

    /**
     * Load the selected role into memory
     * 
     * @throws RemoteException
     */
    private void loadSelectedRoleIfNecessary() throws RemoteException, UserRoleServiceException {
        if (this.selectedRoleId == null) {
            selectAndLoadFirstRole();
        }

        if (!this.roleBeanCache.containsKey(this.selectedRoleId)) {
            IInternalRoleMenuItemBean selectedMenuItem = this.roleMenuItems.getRoleMenuItem(selectedRoleId);
            loadRoleForRoleMenuItem(selectedMenuItem);
        }
    }

    /**
     * Load the role data associated with the specified role menu items
     * 
     * @param roleMenuItem
     *            the menu item for which to load role data
     * @throws RemoteException
     */
    private void loadRoleForRoleMenuItem(IInternalRoleMenuItemBean roleMenuItem) throws RemoteException, UserRoleServiceException {
        if (roleMenuItem == null) {
            throw new NullPointerException("roleMenuItem cannot be null.");
        }

        SubjectDTO roleSubject = roleMenuItem.getWrappedSubjectDTO();
        DMSRoleData roleData = getRoleServiceFacade().getRoleData(roleSubject);
        SubjectDTO[] allRoles = this.roleMenuItems.getWrappedRoleSubjects();
        IInternalRoleBean selectedRole = new RoleBeanImpl(roleSubject, roleData, allRoles);
        this.roleBeanCache.put(this.selectedRoleId, selectedRole);
    }

    /**
     * Retrieve the Role Service Facade
     * 
     * @return the role service facade
     */
    private IRoleServiceFacade getRoleServiceFacade() {
        return ComponentManagerFactory.getComponentManager().getComponent(RoleServiceFacadeImpl.class);
    }

    /**
     * A data model which translates SubjectDTO instances to
     * IInternalRoleItemMenuBean instances
     * 
     * @author sgoldstein
     */
    private class SubjectToRoleMenuItemTranslatingDataModel extends ProxyingDataModel {

        private Map<Long, IInternalRoleMenuItemBean> viewedRoleMenuItems = new HashMap<Long, IInternalRoleMenuItemBean>();

        private SubjectToRoleMenuItemTranslatingDataModel(SubjectDTO[] roleList) {
            super(new ArrayDataModel(roleList));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            if (rawData == null) {
                throw new NullPointerException("rawData cannot be null.");
            }

            RoleMenuItemBeanImpl roleMenuItemToReturn = new RoleMenuItemBeanImpl((SubjectDTO) rawData);
            this.viewedRoleMenuItems.put(new Long(roleMenuItemToReturn.getRoleId()), roleMenuItemToReturn);

            return roleMenuItemToReturn;
        }

        /**
         * Retrieve the role menu item with the specified id. Note that this
         * role menu item must have already been viewed
         * 
         * @param roleID
         *            the id of the role associated with the viewed menu item
         * @return the viewed menu item associated with the role of the
         *         specified id
         */
        private IInternalRoleMenuItemBean getRoleMenuItem(Long roleID) {
            if (!this.viewedRoleMenuItems.containsKey(roleID)) {
                throw new IllegalArgumentException("Role with ID, " + roleID + ", has not been viewed.");
            }

            return (IInternalRoleMenuItemBean) this.viewedRoleMenuItems.get(roleID);
        }

        /**
         * Retrieve the role menu item in the first row
         * 
         * @return the role menu item in the first row
         */
        public IInternalRoleMenuItemBean getFirstMenuItem() {
            this.setRowIndex(0);
            return (IInternalRoleMenuItemBean) this.getRowData();
        }

        /**
         * Retrieve the wrapped SubjectDTO[] representing the roles in the
         * system
         * 
         * @return the wrapped SubjectDTO[] representing the roles in the system
         */
        public SubjectDTO[] getWrappedRoleSubjects() {
            return (SubjectDTO[]) super.getWrappedDataModel().getWrappedData();
        }
    }

    /**
     * Default implementation of the IInternalRoleMenuItemBean
     * 
     * @author sgoldstein
     */
    private class RoleMenuItemBeanImpl implements IInternalRoleMenuItemBean {

        private SubjectDTO subjectDTO;

        /**
         * Create an instance of RoleMenuItemBeanImpl
         * 
         * @param subjectDTO
         */
        public RoleMenuItemBeanImpl(SubjectDTO subjectDTO) {
            if (subjectDTO == null) {
                throw new NullPointerException("subjectDTO cannot be null.");
            }

            this.subjectDTO = subjectDTO;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRoleMenuItemBean#getRoleId()
         */
        public long getRoleId() {
            return this.subjectDTO.getId().getID().longValue();
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRoleMenuItemBean#getRoleTitle()
         */
        public String getRoleTitle() {
            return this.subjectDTO.getName();
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IInternalRoleMenuItemBean#getWrappedSubjectDTO()
         */
        public SubjectDTO getWrappedSubjectDTO() {
            return this.subjectDTO;
        }
    }
}
