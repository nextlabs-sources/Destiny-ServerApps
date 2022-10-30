/*
 * Created on Sep 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserManagementMetadata;
import com.bluejungle.destiny.services.policy.types.DMSUserData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.services.policy.types.SubjectDTOList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;

/**
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/UserCreationBeanImplTest.java#1 $:
 */

public class UserCreationBeanImplTest extends TestCase {
    
    private IComponentManager cm;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        ComponentInfo compInfo = new ComponentInfo(IUserServiceFacade.COMPONENT_NAME, MockUserServiceFacade.class.getName(), IUserServiceFacade.class.getName(), LifestyleType.SINGLETON_TYPE);
        cm = ComponentManagerFactory.getComponentManager();
        cm.registerComponent(compInfo, true);
    }

    /**
     * Constructor for UserCreationBeanImplTest.
     * @param arg0
     */
    public UserCreationBeanImplTest(String arg0) {
        super(arg0);
    }

    /*
    public final void testSaveCreatedUser() {
        UserCreationBeanImpl bean = new UserCreationBeanImpl();
        bean.setFirstName(FIRST_NAME);
        bean.setLastName(LAST_NAME);
        bean.setLoginName(LOGIN);
        bean.setDisplayName(DISPLAY_NAME);
        bean.setPassword(PASSWORD);
        bean.setConfirmPassword(CONFIRM_PASSWORD);
        
        try {
            bean.saveCreatedUser();
        } catch (UsersException e) {
            fail(e.getMessage());
        }
        
        MockUserServiceFacade sf = (MockUserServiceFacade) cm.getComponent(IUserServiceFacade.COMPONENT_NAME);
        UserDTO newUser = sf.newUser;
        
        assertNotNull("newUser should not be null", newUser);
        
        String firstName = newUser.getFirstName();
        assertNotNull("first name should not be null", firstName);
        assertEquals("first name should match", FIRST_NAME, firstName);
        
        String lastName = newUser.getLastName();
        assertNotNull("last name should not be null", lastName);
        assertEquals("last name should match", LAST_NAME, lastName);

        String displayName = newUser.getName();
        assertNotNull("display name should not be null", displayName);
        assertEquals("display name should match", DISPLAY_NAME, displayName);

        String loginName = newUser.getUid();
        assertNotNull("login name should not be null", loginName);
        assertEquals("login name should match", LOGIN, loginName);

        String password = newUser.getPassword();
        assertNotNull("password should not be null", password);
        assertEquals("password should match", PASSWORD, password);
        
    }
    
*/
    
    public static final class MockUserServiceFacade implements IUserServiceFacade {

        private UserDTO newUser;
        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getAvailableUsersForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
         */
        public SubjectDTOList getAvailableUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws ServiceNotReadyFault, RemoteException, ServiceException {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getAvailableUsersForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
         */
        public SubjectDTOList getAvailableUsersForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ServiceNotReadyFault, RemoteException, ServiceException {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getAllUsers()
         */
        public UserDTOList getAllUsers() throws RemoteException, ServiceException {
            return null;
        }
        
        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUser(java.lang.Long)
         */
        public UserDTO getUser(Long userId) throws RemoteException, ServiceException {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUsersForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
         */
        public UserDTOList getUsersForFreeFormSearchSpec(IFreeFormSearchSpec searchSpec) throws ServiceNotReadyFault, RemoteException, ServiceException {
            return null;
        }
        
        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUsersForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
         */
        public UserDTOList getUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws ServiceNotReadyFault, RemoteException, ServiceException {
            return null;
        }
        
        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUserData(com.bluejungle.destiny.services.policy.types.SubjectDTO)
         */
        public DMSUserData getUserData(SubjectDTO userSubject) throws RemoteException, ServiceException {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#updateUser(com.bluejungle.destiny.services.policy.types.SubjectDTO, com.bluejungle.destiny.services.policy.types.DMSUserData)
         */
        public void updateUser(UserDTO userSubject, DMSUserData userData) throws RemoteException, ServiceException {
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#deleteUser(com.bluejungle.destiny.services.policy.types.SubjectDTO)
         */
        public void deleteUser(SubjectDTO userToDelete) throws RemoteException, ServiceException {
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#importAvailableUsers(com.bluejungle.destiny.services.policy.types.SubjectDTO[])
         */
        public UserDTOList importAvailableUsers(SubjectDTO[] usersToImport) throws RemoteException, ServiceException {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#createUser(com.bluejungle.destiny.services.management.types.NewUser)
         */
        public UserDTO createUser(UserDTO newUser, DMSUserData userData) throws RemoteException, ServiceException {
            this.newUser = newUser;
            
            return this.newUser;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUserManagementMetadata()
         */
        public UserManagementMetadata getUserManagementMetadata() throws ServiceException, RemoteException {
            // TODO Auto-generated method stub
            return null;
        }
    }


    
    private static final String FIRST_NAME = "Joe";
    private static final String LAST_NAME = "Shmoe";
    private static final String LOGIN = "Joe.Shmoe";    
    private static final String DISPLAY_NAME = "Joe Francis Shmoe the III, PhD";
    private static final String PASSWORD = "DeadRatsFloat";
    private static final String CONFIRM_PASSWORD = "EatYourSoup";    
    

}
