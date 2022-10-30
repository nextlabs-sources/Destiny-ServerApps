package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.bluejungle.destiny.services.policy.types.Access;
import com.bluejungle.destiny.services.policy.types.AccessList;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment;
import com.bluejungle.destiny.services.policy.types.Principal;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalDefaultAccessAssignmentBean}
 * 
 * @author sgoldstein
 */
public class DefaultAccessAssignmentBeanImpl implements IInternalDefaultAccessAssignmentBean {

    private Principal principal;
    private Set accessData;

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#getReceivingPrincipalId()
     */
    public String getReceivingPrincipalId() {
        return this.principal.getID().toString();
    }

    /**
     * Create an instance of DefaultAccessAssignmentBeanImpl
     * 
     * @param wrappedDefaultAccessAssignment
     */
    public DefaultAccessAssignmentBeanImpl(DefaultAccessAssignment wrappedDefaultAccessAssignment) {
        if (wrappedDefaultAccessAssignment == null) {
            throw new NullPointerException("wrappedDefaultAccessAssignment cannot be null.");
        }

        this.principal = wrappedDefaultAccessAssignment.getPrinciapl();

        Access[] accessDataArray = wrappedDefaultAccessAssignment.getDefaultAccess().getAccess();
        this.accessData = new HashSet();
        if (accessDataArray != null) {
            for (int i = 0; i < accessDataArray.length; i++) {
                this.accessData.add(accessDataArray[i]);
            }
        }
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#getReceivingPrincipalTitle()
     */
    public String getReceivingPrincipalTitle() {
        return this.principal.getDisplayName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#isPrincipalGivenDeployAccess()
     */
    public boolean isPrincipalGivenDeployAccess() {
        return this.isPrincipalGivenAccess(Access.DEPLOY);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#isPrincipalGivenEditPermissionAccess()
     */
    public boolean isPrincipalGivenEditPermissionAccess() {
        return this.isPrincipalGivenAccess(Access.ADMIN);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#isPrincipalGivenReadAccess()
     */
    public boolean isPrincipalGivenReadAccess() {
        return this.isPrincipalGivenAccess(Access.READ);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#isPrincipalGivenSubmitAccess()
     */
    public boolean isPrincipalGivenSubmitAccess() {
        return this.isPrincipalGivenAccess(Access.APPROVE);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#isPrincipalGivenWriteAccess()
     */
    public boolean isPrincipalGivenWriteAccess() {
        return this.isPrincipalGivenAccess(Access.WRITE);
    }

    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#isPrincipalGivenDeleteAccess()
     */
    public boolean isPrincipalGivenDeleteAccess() {
        return this.isPrincipalGivenAccess(Access.DELETE);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#setPrincipalGivenDeployAccess(boolean)
     */
    public void setPrincipalGivenDeployAccess(boolean giveAccess) {
        setPrincipalGivenAccess(Access.DEPLOY, giveAccess);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#setPrincipalGivenEditPermissionAccess(boolean)
     */
    public void setPrincipalGivenEditPermissionAccess(boolean giveAccess) {
        setPrincipalGivenAccess(Access.ADMIN, giveAccess);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#setPrincipalGivenReadAccess(boolean)
     */
    public void setPrincipalGivenReadAccess(boolean giveAccess) {
        setPrincipalGivenAccess(Access.READ, giveAccess);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#setPrincipalGivenSubmitAccess(boolean)
     */
    public void setPrincipalGivenSubmitAccess(boolean giveAccess) {
        setPrincipalGivenAccess(Access.APPROVE, giveAccess);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#setPrincipalGivenWriteAccess(boolean)
     */
    public void setPrincipalGivenWriteAccess(boolean giveAccess) {
        setPrincipalGivenAccess(Access.WRITE, giveAccess);
    }

    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IDefaultAccessAssignmentBean#setPrincipalGivenDeleteAccess(boolean)
     */
    public void setPrincipalGivenDeleteAccess(boolean giveAccess) {
        setPrincipalGivenAccess(Access.DELETE, giveAccess);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalDefaultAccessAssignmentBean#getWrappedDefaultAccessAssignment()
     */
    public DefaultAccessAssignment getWrappedDefaultAccessAssignment() {
        Access[] allowedAccess = new Access[this.accessData.size()];
        Iterator accessDataIterator = this.accessData.iterator();
        for (int i=0; accessDataIterator.hasNext(); i++) {
            Access nextAccess = (Access) accessDataIterator.next();
            allowedAccess[i] = nextAccess;
        }

        AccessList allowedAccessList = new AccessList();
        allowedAccessList.setAccess(allowedAccess);

        DefaultAccessAssignment accessAssignment = new DefaultAccessAssignment();
        accessAssignment.setPrinciapl(this.principal);
        accessAssignment.setDefaultAccess(allowedAccessList);

        return accessAssignment;
    }

    /**
     * @param write
     * @return
     */
    private boolean isPrincipalGivenAccess(Access accessToCheck) {
        if (accessToCheck == null) {
            throw new NullPointerException("accessToCheck cannot be null.");
        }

        return this.accessData.contains(accessToCheck);
    }

    /**
     * @param write
     * @param giveAccess
     */
    private void setPrincipalGivenAccess(Access access, boolean giveAccess) {
        if (access == null) {
            throw new NullPointerException("access cannot be null.");
        }

        if (giveAccess) {
            this.accessData.add(access);
        } else {
            this.accessData.remove(access);
        }
    }
}

