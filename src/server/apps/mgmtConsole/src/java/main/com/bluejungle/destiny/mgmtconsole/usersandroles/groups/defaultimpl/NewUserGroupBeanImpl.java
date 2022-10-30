/*
 * Created on Sep 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.framework.types.Title;
import org.apache.axis2.databinding.types.Token;

import com.bluejungle.destiny.services.management.types.UserGroupInfo;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalNewUserGroupBean}
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/NewUserGroupBeanImpl.java#3 $
 */

public class NewUserGroupBeanImpl implements IInternalNewUserGroupBean {
    private static final Token INITIAL_TITLE = new Token("New Group");
    private static final String INITIAL_DESCRIPTION = "New Group";
    
    private UserGroupInfo wrappedUserGroupInfo;

    /**
     * Create an instance of NewUserGroupBeanImpl
     *  
     */
    public NewUserGroupBeanImpl() {
        UserGroupInfo userGroupInfo = new UserGroupInfo();
        Title title = new Title();
        title.setTitle(INITIAL_TITLE);

        userGroupInfo.setTitle(title);
        userGroupInfo.setDescription(INITIAL_DESCRIPTION);

        this.wrappedUserGroupInfo = userGroupInfo;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupId()
     */
    public String getUserGroupId() {
        throw new UnsupportedOperationException("New user groups do not have Ids");
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupTitle()
     */
    public String getUserGroupTitle() {
        return this.wrappedUserGroupInfo.getTitle().toString();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupDescription()
     */
    public String getUserGroupDescription() {
        return this.wrappedUserGroupInfo.getDescription();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getMembers()
     */
    public DataModel getMembers() {
        throw new UnsupportedOperationException("New user groups do not members");
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#setUserGroupTitle(java.lang.String)
     */
    public void setUserGroupTitle(String titleToSet) {
        if (titleToSet == null) {
            throw new NullPointerException("titleToSet cannot be null.");
        }

        Title title = new Title();
        title.setTitle(new Token(titleToSet.trim()));

        this.wrappedUserGroupInfo.setTitle(title);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupDescription()
     */
    public void setUserGroupDescription(String description) {
        if (description == null) {
            throw new NullPointerException("description cannot be null.");
        }

        this.wrappedUserGroupInfo.setDescription(description);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getDefaultAccessAssignments()
     */
    public DataModel getDefaultAccessAssignments() {
        throw new UnsupportedOperationException("New user groups do have access assignments");
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#isExternallyManaged()
     */
    public boolean isExternallyManaged() {
        return false;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#isNew()
     */
    public boolean isNew() {
        return true;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean#getUserGroupQualifiedExternalName()
     */
    public String getUserGroupQualifiedExternalName() {
        throw new UnsupportedOperationException("New user groups do not have externally qualified names");
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalNewUserGroupBean#getWrappedUserGroupInfo()
     */
    public UserGroupInfo getWrappedUserGroupInfo() {
        return this.wrappedUserGroupInfo;
    }
}