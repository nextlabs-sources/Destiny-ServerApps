/*
 * Created on Sep 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.math.BigInteger;

import com.bluejungle.destiny.services.management.types.UserDTO;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/MemberBeanImplTest.java#1 $
 */

public class MemberBeanImplTest extends TestCase {

    private MemberBeanImpl beanToTest;
    private UserDTO wrappedUser;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MemberBeanImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        this.wrappedUser = new UserDTO("First Name", "Last Name", null, true, null);
        this.wrappedUser.setId(new BigInteger("57"));
        this.wrappedUser.setUniqueName("foounique");
        
        this.beanToTest = new MemberBeanImpl(this.wrappedUser);
    }

    public void testGetDisplayName() {
        assertEquals("Ensure display name is as expected", this.wrappedUser.getLastName() + ", " + this.wrappedUser.getFirstName(), this.beanToTest.getDisplayName());
    }

    public void testGetMemberId() {
        assertEquals("Ensure member id as expected", this.wrappedUser.getId().toString(), this.beanToTest.getMemberId());
    }

    public void testGetMemberUniqueName() {
        assertEquals("Ensure member unique name is as expected", this.wrappedUser.getUniqueName(), this.beanToTest.getMemberUniqueName());
    }
}
