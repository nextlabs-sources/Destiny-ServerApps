package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.rmi.RemoteException;

import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean;
import com.bluejungle.destiny.services.management.types.CommitFault;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.UserGroupDTO;

/**
 * Internal extension of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupsViewBean}
 * 
 * @author sgoldstein
 */
public interface IInternalUserGroupsViewBean extends IUserGroupsViewBean {

    /**
     * Reset the state of this User Groups View Bean
     */
    void reset();

    /**
     * Reset the state of this User Groups View Bean and, after reset, set the
     * specified group as the selected group
     * 
     * @param groupToSelect
     * @throws ServiceException 
     * @throws RemoteException 
     * @throws UnauthorizedCallerFault 
     * @throws ServiceNotReadyFault 
     */
    void resetAndSelectUserGroup(UserGroupDTO groupToSelect) throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, CommitFault;
}
