/*
 * Created on Sep 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.math.BigInteger;

import com.bluejungle.destiny.services.policy.types.Access;
import com.bluejungle.destiny.services.policy.types.AccessList;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment;
import com.bluejungle.destiny.services.policy.types.Principal;
import com.bluejungle.destiny.services.policy.types.PrincipalType;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/DefaultAccessAssignmentBeanImplTest.java#1 $
 */

public class DefaultAccessAssignmentBeanImplTest extends TestCase {

    private DefaultAccessAssignmentBeanImpl beanToTest;
    private DefaultAccessAssignment wrappedAccessAssignment;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultAccessAssignmentBeanImplTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        
        Principal principal = new Principal(new BigInteger("41"), "huh", PrincipalType.USER);
        Access[] accessForPrincipal = {Access.READ, Access.DEPLOY};
        AccessList accessListForPrincipal = new AccessList(accessForPrincipal);
        this.wrappedAccessAssignment = new DefaultAccessAssignment(principal, accessListForPrincipal);
        
        this.beanToTest = new DefaultAccessAssignmentBeanImpl(this.wrappedAccessAssignment);
    }
    
    public void testGetReceivingPrincipalId() {
        assertEquals("Ensure principal id is as expected", this.wrappedAccessAssignment.getPrinciapl().getID().toString(), this.beanToTest.getReceivingPrincipalId());
    }

    public void testGetReceivingPrincipalTitle() {
        assertEquals("Ensure principal title is as expected", this.wrappedAccessAssignment.getPrinciapl().getDisplayName(), this.beanToTest.getReceivingPrincipalTitle());
    }

    public void testIsSetPrincipalGivenDeployAccess() {
        assertEquals("Ensure initial value of deploy acess as expected", true, this.beanToTest.isPrincipalGivenDeployAccess());
        this.beanToTest.setPrincipalGivenDeployAccess(false);
        assertEquals("Ensure deploy acess set as expected", false, this.beanToTest.isPrincipalGivenDeployAccess());
    }

    public void testIsSetPrincipalGivenEditPermissionAccess() {
        assertEquals("Ensure initial value of edit permission access as expected", false, this.beanToTest.isPrincipalGivenEditPermissionAccess());
        this.beanToTest.setPrincipalGivenEditPermissionAccess(true);
        assertEquals("Ensure edit permission access set as expected", true, this.beanToTest.isPrincipalGivenEditPermissionAccess());
    }

    public void testIsSetPrincipalGivenReadAccess() {
        assertEquals("Ensure initial value of read acess as expected", true, this.beanToTest.isPrincipalGivenReadAccess());
        this.beanToTest.setPrincipalGivenReadAccess(false);
        assertEquals("Ensure read acess set as expected", false, this.beanToTest.isPrincipalGivenReadAccess());
    }

    public void testIsSetPrincipalGivenSubmitAccess() {
        assertEquals("Ensure initial value of submit access as expected", false, this.beanToTest.isPrincipalGivenSubmitAccess());
        this.beanToTest.setPrincipalGivenSubmitAccess(true);
        assertEquals("Ensure submit access set as expected", true, this.beanToTest.isPrincipalGivenSubmitAccess());
    }

    public void testIsSetPrincipalGivenWriteAccess() {
        assertEquals("Ensure initial value of write access as expected", false, this.beanToTest.isPrincipalGivenWriteAccess());
        this.beanToTest.setPrincipalGivenWriteAccess(true);
        assertEquals("Ensure write access set as expected", true, this.beanToTest.isPrincipalGivenWriteAccess());
    }

    public void testGetWrappedDefaultAccessAssignment() {
        DefaultAccessAssignment defaultAccessAssignment = this.beanToTest.getWrappedDefaultAccessAssignment();
        assertNotNull("testGetWrappedDefaultAccessAssignment - Ensure default access assignment is not null", defaultAccessAssignment);
        Principal wrappedPrincipal = defaultAccessAssignment.getPrinciapl();
        assertEquals("testGetWrappedDefaultAccessAssignment - Ensure principal id as expected", this.beanToTest.getReceivingPrincipalId(), wrappedPrincipal.getID().toString());
        assertEquals("testGetWrappedDefaultAccessAssignment - Ensure principal title as expected", this.beanToTest.getReceivingPrincipalTitle(), wrappedPrincipal.getDisplayName());
        AccessList defaultAccess = defaultAccessAssignment.getDefaultAccess();
        Access[] accessArray = defaultAccess.getAccess();
        // Ensure access matches bean state
        for (int i=0; i<accessArray.length; i++) {
            Access nextAccess = accessArray[i];
            if (nextAccess == Access.WRITE) {
                assertEquals("testGetWrappedDefaultAccessAssignment - Ensure write access set as expected", true, this.beanToTest.isPrincipalGivenWriteAccess());
            } else if (nextAccess == Access.READ) {
                assertEquals("testGetWrappedDefaultAccessAssignment - Ensure read access set as expected", true, this.beanToTest.isPrincipalGivenReadAccess());
            } else if (nextAccess == Access.ADMIN) {
                assertEquals("testGetWrappedDefaultAccessAssignment - Ensure edit permission access set as expected", true, this.beanToTest.isPrincipalGivenEditPermissionAccess());
            } else if (nextAccess == Access.DELETE) {
                assertEquals("testGetWrappedDefaultAccessAssignment - Ensure delete access set as expected", true, this.beanToTest.isPrincipalGivenDeleteAccess());
            } else if (nextAccess == Access.DEPLOY) {
                assertEquals("testGetWrappedDefaultAccessAssignment - Ensure deploy access set as expected", true, this.beanToTest.isPrincipalGivenDeployAccess());
            } else if (nextAccess == Access.APPROVE) {
                assertEquals("testGetWrappedDefaultAccessAssignment - Ensure approve access set as expected", true, this.beanToTest.isPrincipalGivenSubmitAccess());
            }
        }
    }
    
}
