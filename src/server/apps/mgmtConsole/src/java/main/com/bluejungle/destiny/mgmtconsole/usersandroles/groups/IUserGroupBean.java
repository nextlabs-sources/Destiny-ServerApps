package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import javax.faces.model.DataModel;

/**
 * IUserGroupBean represents a User Group in the system
 * 
 * @author sgoldstein
 */
public interface IUserGroupBean {

    /**
     * Retrieve the ID of the represented user group
     * 
     * @return the ID of the represented user group
     */
    public String getUserGroupId();

    /**
     * Retrieve the title of the represented user group
     * 
     * @return the title of the represented user group
     */
    public String getUserGroupTitle();

    /**
     * Retrieve the description of the represented user group
     * 
     * @return the description of the represented user group
     */
    public String getUserGroupDescription();

    /**
     * Retrieve the externally qualified name of the represented user group if
     * it is externally managed.
     * 
     * @throws UnsupportedOperationException
     *             if the represented group is not externally managed
     * @return the externally qualified name of the represented user group if it
     *         is externally managed.
     */
    public String getUserGroupQualifiedExternalName();

    /**
     * Retrieve a DataModel of the represented user group's members
     * 
     * @return a DataModel of the represented user group's members
     */
    public DataModel getMembers();

    /**
     * Set the title of the represented user group
     * 
     * @param titleToSet
     *            the title to set
     */
    public void setUserGroupTitle(String titleToSet);

    /**
     * Retrieve the default access assignments associated with the represented
     * user group
     * 
     * @return the default access assignments associated with the represented
     *         user group
     */
    public DataModel getDefaultAccessAssignments();

    /**
     * Determine if the represented user group is externally linked
     * 
     * @return true if externally linked; false otherwise
     */
    public boolean isExternallyManaged();

    /**
     * Determine if the represented user group is new (has not yet been
     * persisted)
     * 
     * @return true if it is new; false otherwise
     */
    public boolean isNew();
}

