package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

/**
 * IUserGroupMenuItemBean represents a User Group menu item
 * 
 * @author sgoldstein
 */
public interface IUserGroupMenuItemBean {

    /**
     * Retrieve the ID of the represented User Group
     * 
     * @return the ID of the represented User Group
     */
    public String getUserGroupId();

    /**
     * Retrieve the title of the represented User Group
     * 
     * @return the title of the represented User Group
     */
    public String getUserGroupTitle();

    /**
     * Determine if this is an externally linked group
     * 
     * @return true if linked; false otherwise
     */
    public boolean isExternallyManaged();

    /**
     * Determine if the represented group was linked to an external group which
     * has been deleted
     * 
     * @return true if the group was linked to an external group which has been
     *         deleted; false otherwise
     */
    public boolean isOrphaned();
}
