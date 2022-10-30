package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment;

/**
 * IInternalDefaultAccessAssignment is an internal extension of
 * IDefefaultAccessAssignment
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/IInternalDefaultAccessAssignmentBean.java#1 $
 * 
 */
public interface IInternalDefaultAccessAssignmentBean extends IDefaultAccessAssignmentBean {

    /**
     * Retrieve the wrapped Default Access Assignment for this Default Access
     * Assignment Bean
     * 
     * @return the wrapped Default Access Assignment for this Default Access
     *         Assignment Bean
     */
    public abstract DefaultAccessAssignment getWrappedDefaultAccessAssignment();
}
