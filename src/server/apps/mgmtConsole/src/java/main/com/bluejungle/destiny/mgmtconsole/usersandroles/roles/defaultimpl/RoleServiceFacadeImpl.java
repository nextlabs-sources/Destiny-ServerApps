/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl;

import java.rmi.RemoteException;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.UserRoleServiceStub;
import com.bluejungle.destiny.services.policy.types.DMSRoleData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.services.policy.types.SubjectDTOList;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;
import org.apache.axis2.AxisFault;

/**
 * Default implementation of the IRoleServiceFacade
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/RoleServiceFacadeImpl.java#1 $
 */

public class RoleServiceFacadeImpl implements IRoleServiceFacade, IHasComponentInfo<RoleServiceFacadeImpl> {

    private static final ComponentInfo<RoleServiceFacadeImpl> COMPONENT_INFO = 
    	new ComponentInfo<RoleServiceFacadeImpl>(
    			COMPONENT_NAME, 
    			RoleServiceFacadeImpl.class, 
    			IRoleServiceFacade.class, 
    			LifestyleType.SINGLETON_TYPE);
    private static final String ROLE_SERVICE_LOCATION_SERVLET_PATH = "/services/UserRoleService";

    private UserRoleServiceStub roleService;

    /**
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    public ComponentInfo<RoleServiceFacadeImpl> getComponentInfo() {
        return COMPONENT_INFO;
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IRoleServiceFacade#getAllRoles()
     */
    public SubjectDTOList getAllRoles() throws RemoteException, UserRoleServiceException {
        return getRoleService().getAllRoles();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IRoleServiceFacade#getRoleData(com.bluejungle.destiny.services.policy.types.SubjectDTO)
     */
    public DMSRoleData getRoleData(SubjectDTO roleSubject) throws RemoteException, UserRoleServiceException {
        if (roleSubject == null) {
            throw new NullPointerException("roleSubject cannot be null.");
        }
        
        return getRoleService().getRoleData(roleSubject);
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IRoleServiceFacade#updateRole(com.bluejungle.destiny.services.policy.types.SubjectDTO, com.bluejungle.destiny.services.policy.types.DMSRoleData)
     */
    public void updateRole(SubjectDTO roleSubject, DMSRoleData roleData) throws RemoteException, UserRoleServiceException {
        if (roleSubject == null) {
            throw new NullPointerException("roleSubject cannot be null.");
        }
        
        if (roleData == null) {
            throw new NullPointerException("roleData cannot be null.");
        }
        
        getRoleService().setRoleData(roleSubject, roleData);        
    }
    
    /**
     * Retrieve the User and Role Service port
     * 
     * @return the User and Role Service port
     * @throws ServiceException
     *             if the service port retrieval fails
     */
    private UserRoleServiceStub getRoleService() throws AxisFault {
        if (this.roleService == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += ROLE_SERVICE_LOCATION_SERVLET_PATH;

            this.roleService = new UserRoleServiceStub(location);
        }

        return this.roleService;
    }
}