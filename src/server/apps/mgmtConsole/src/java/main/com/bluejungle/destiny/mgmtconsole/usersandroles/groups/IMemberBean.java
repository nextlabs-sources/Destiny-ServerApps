package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

/**
 * IMemberBean represents a member of a user group
 * 
 * @author sgoldstein
 */
public interface IMemberBean {

    /**
     * Retrieve the ID of the represented user group member
     * 
     * @return the ID of the represented user group member
     */
    public String getMemberId();

    /**
     * Retrieve the display name of the represented user group member
     * 
     * @return the display name of the represented user group member
     */
    public String getDisplayName();

    /**
     * Retrieve the unique name of the represented user group member
     * 
     * @return the unique name of the represented user group member
     */
    public String getMemberUniqueName();
}

