/*
 * Created on May 18, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean;
import com.bluejungle.destiny.services.policy.types.Component;

/**
 * IInternalPolicyAuthorComponentAccessBean is an internal extension of
 * IPolicyAuthorComponentAccessBean
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/IInternalPolicyAuthorComponentAccessBean.java#1 $
 */
public interface IInternalPolicyAuthorComponentAccessBean extends IPolicyAuthorComponentAccessBean {

    /**
     * Retrieve the accessible components
     * 
     * @return the components accessible to the associated role as a Component[]
     *         instance
     */
    public Component[] getAccessibleComponents();
}