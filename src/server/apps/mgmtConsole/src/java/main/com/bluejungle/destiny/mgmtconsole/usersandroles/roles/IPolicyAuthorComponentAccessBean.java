/*
 * Created on May 18, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

/**
 * IPolicyAuthorComponentAccessBean provides information about access to Policy
 * Author components for a particular role
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/IPolicyAuthorComponentAccessBean.java#2 $
 */
public interface IPolicyAuthorComponentAccessBean {

    /**
     * Determine if the associated role can edit policies
     * 
     * @return true if the associated role can edit policies, false otherwise
     */
    public boolean isPolicyEditViewAccessibleToRole();

    /**
     * Set whether or not the associated role is given access to edit policies
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setPolicyEditViewAccessibleToRole(boolean grantAccess);

    /**
     * Determine if the associated role can edit user components
     * 
     * @return true if the associated role can edit user components, false
     *         otherwise
     */
    public boolean isUserComponentEditViewAccessibleToRole();

    /**
     * Set whether or not the associated role is given access to edit user components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setUserComponentEditViewAccessibleToRole(boolean grantAccess);
    
    /**
     * Determine if the associated role can edit device components
     * 
     * @return true if the associated role can edit device components, false
     *         otherwise
     */
    public boolean isDeviceComponentEditViewAccessibleToRole();

    /**
     * Set whether or not the associated role is given access to edit device components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setDeviceComponentEditViewAccessibleToRole(boolean grantAccess);
    
    /**
     * Determine if the associated role can edit SAP components
     * 
     * @return true if the associated role can edit SAP components, false
     *         otherwise
     */
    public boolean isSAPComponentEditViewAccessibleToRole();

    /**
     * Set whether or not the associated role is given access to edit SAP components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setSAPComponentEditViewAccessibleToRole(boolean grantAccess);
    
    /**
     * Determine if the associated role can edit enovia components
     * 
     * @return true if the associated role can edit enovia components, false
     *         otherwise
     */
    public boolean isEnoviaComponentEditViewAccessibleToRole();

    /**
     * Set whether or not the associated role is given access to edit enovia components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setEnoviaComponentEditViewAccessibleToRole(boolean grantAccess);
    
    /**
     * Determine if the associated role can edit resource components
     * 
     * @return true if the associated role can edit resource components, false
     *         otherwise
     */
    public boolean isResourceComponentEditViewAccessibleToRole();
    
    /**
     * Determine if the associated role can edit portal components
     * 
     * @return true if the associated role can edit portal components, false
     *         otherwise
     */
    public boolean isPortalComponentEditViewAccessibleToRole();

    /**
     * Set whether or not the associated role is given access to edit resource components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setResourceComponentEditViewAccessibleToRole(boolean grantAccess);
    
    /**
     * Set whether or not the associated role is given access to edit portal components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setPortalComponentEditViewAccessibleToRole(boolean grantAccess);
    
    /**
     * Determine if the associated role can edit desktop components
     * 
     * @return true if the associated role can edit desktop components, false
     *         otherwise
     */
    public boolean isDesktopComponentEditViewAccessibleToRole();

    /**
     * Set whether or not the associated role is given access to edit desktop components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setDesktopComponentEditViewAccessibleToRole(boolean grantAccess);
    
    /**
     * Determine if the associated role can edit application components
     * 
     * @return true if the associated role can edit application components, false
     *         otherwise
     */
    public boolean isApplicationComponentEditViewAccessibleToRole();

    /**
     * Set whether or not the associated role is given access to edit application components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setApplicationComponentEditViewAccessibleToRole(boolean grantAccess);
    
    /**
     * Determine if the associated role can edit action components
     * 
     * @return true if the associated role can edit action components, false
     *         otherwise
     */
    public boolean isActionComponentEditViewAccessibleToRole();
    
    /**
     * Set whether or not the associated role is given access to edit action components
     * 
     * @param grantAccess
     *            true to grant access; false otherwise
     */
    public void setActionComponentEditViewAccessibleToRole(boolean grantAccess);
}