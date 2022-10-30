/*
 * Created on Jul 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers;

import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * This is the test class for the UserComponentEntityResolver. It tests the
 * various helper API for user manipulation.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/helpers/UserComponentEntityResolverTest.java#1 $
 */

public class UserComponentEntityResolverTest extends BaseDestinyTestCase {

    /**
     * This test verifies that the constructor works properly
     */
    public void testClassConstructor() {
        //Try NULL input
        UserComponentEntityResolver u = new UserComponentEntityResolver(null);
        assertNotNull("Constructor should handle NULL entity list", u.getQualifiedUserClasses());
        assertNotNull("Constructor should handle NULL entity list", u.getQualifiedUsers());
        assertNotNull("Constructor should handle NULL entity list", u.getUserQualifier());
        assertNotNull("Constructor should handle NULL entity list", u.getUserClassQualifier());

        //Try empty input
        u = new UserComponentEntityResolver("");
        assertNotNull("Constructor should handle empty entity list", u.getQualifiedUserClasses());
        assertNotNull("Constructor should handle empty entity list", u.getQualifiedUsers());
        assertEquals("Constructor should handle empty entity list", 0, u.getQualifiedUserClasses().length);
        assertEquals("Constructor should handle empty entity list", 0, u.getQualifiedUsers().length);

        //Try with another empty input
        u = new UserComponentEntityResolver(",,,,,,,");
        assertNotNull("Constructor should handle empty entity list", u.getQualifiedUserClasses());
        assertEquals("Constructor should handle empty entity list", 0, u.getQualifiedUserClasses().length);
        assertEquals("Constructor should handle empty entity list", 0, u.getQualifiedUsers().length);
    }

    /**
     * This test verifies that the user and user class separation is done
     * properly.
     */
    public void testUserAndUserClassSeparation() {
        //Try without specifying anything
        UserComponentEntityResolver u = new UserComponentEntityResolver("ABC,BCD,123");
        assertEquals("Separation of user and group should be done properly", 3, u.getQualifiedUserClasses().length);
        assertEquals("Separation of user and group should be done properly", 3, u.getQualifiedUsers().length);

        //Try with 1 explicit user
        u = new UserComponentEntityResolver(u.getUserQualifier() + "ABC,BCD,123");
        assertEquals("Separation of user and group should be done properly", 2, u.getQualifiedUserClasses().length);
        assertEquals("Separation of user and group should be done properly", "BCD", u.getQualifiedUserClasses()[0]);
        assertEquals("Separation of user and group should be done properly", "123", u.getQualifiedUserClasses()[1]);
        assertEquals("Separation of user and group should be done properly", 3, u.getQualifiedUsers().length);

        //Try with 1 explicit group
        u = new UserComponentEntityResolver(u.getUserClassQualifier() + "ABC,BCD,123");
        assertEquals("Separation of user and group should be done properly", 3, u.getQualifiedUserClasses().length);
        assertEquals("Separation of user and group should be done properly", 2, u.getQualifiedUsers().length);
        assertEquals("Separation of user and group should be done properly", "BCD", u.getQualifiedUsers()[0]);
        assertEquals("Separation of user and group should be done properly", "123", u.getQualifiedUsers()[1]);

        //Try with 1 explicit user and group
        u = new UserComponentEntityResolver(u.getUserClassQualifier() + "ABC," + u.getUserQualifier() + "    BCD");
        assertEquals("Separation of user and group should be done properly", 1, u.getQualifiedUserClasses().length);
        assertEquals("Separation of user and group should be done properly", "ABC", u.getQualifiedUserClasses()[0]);
        assertEquals("Separation of user and group should be done properly", 1, u.getQualifiedUsers().length);
        assertEquals("Separation of user and group should be done properly", "BCD", u.getQualifiedUsers()[0]);

        //Try with 1 explicit user, 1 explicit group and one ambiguous
        u = new UserComponentEntityResolver(u.getUserClassQualifier() + "ABC," + u.getUserQualifier() + "BCD, 123   ");
        assertEquals("Separation of user and group should be done properly", 2, u.getQualifiedUserClasses().length);
        assertEquals("Separation of user and group should be done properly", "ABC", u.getQualifiedUserClasses()[0]);
        assertEquals("Separation of user and group should be done properly", "123", u.getQualifiedUserClasses()[1]);
        assertEquals("Separation of user and group should be done properly", 2, u.getQualifiedUsers().length);
        assertEquals("Separation of user and group should be done properly", "BCD", u.getQualifiedUsers()[0]);
        assertEquals("Separation of user and group should be done properly", "123", u.getQualifiedUsers()[1]);
    }

    /**
     * This test verifies that the user qualification works properly
     */
    public void testQualification() {
        assertNull("Null User name should be supported", UserComponentEntityResolver.createUserQualification(null));
        assertEquals("Empty User name should be supported", "", UserComponentEntityResolver.createUserQualification(""));
        assertEquals("User name qualification should work properly", "ABCCCC", UserComponentEntityResolver.createUserQualification("   ABCCCC  "));
        assertEquals("User name qualification should work properly", "ABCCCC", UserComponentEntityResolver.createUserQualification("ABCCCC"));
        assertEquals("User name qualification should work properly", "ABCCCC", UserComponentEntityResolver.createUserQualification(" ABCCCC"));
        assertNull("Null User class name should be supported", UserComponentEntityResolver.createUserClassQualification(null));
        assertEquals("Empty class User name should be supported", "", UserComponentEntityResolver.createUserClassQualification(""));
        assertEquals("User class name qualification should work properly", "ABCCCC", UserComponentEntityResolver.createUserClassQualification("   ABCCCC  "));
        assertEquals("User class name qualification should work properly", "ABCCCC", UserComponentEntityResolver.createUserClassQualification("ABCCCC"));
        assertEquals("User class name qualification should work properly", "ABCCCC", UserComponentEntityResolver.createUserClassQualification(" ABCCCC"));
    }
}