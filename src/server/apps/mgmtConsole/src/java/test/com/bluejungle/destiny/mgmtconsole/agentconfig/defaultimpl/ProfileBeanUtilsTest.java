/*
 * Created on May 5, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

import junit.framework.TestCase;

/**
 * JUnit test for ProfileBeanUtils
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/ProfileBeanUtilsTest.java#1 $
 */

public class ProfileBeanUtilsTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ProfileBeanUtils.class);
    }

    public void testGetURLFromAxisURI() throws MalformedURIException {
        URI axisURI = new URI("http://www.testmenow.com");
        URL translatedURL = ProfileBeanUtils.getURLFromAxisURI(axisURI);
        assertEquals("Ensure translated URL is correect", axisURI.toString(), translatedURL.toString());
        
        NullPointerException expectedException = null;
        try {
            ProfileBeanUtils.getURLFromAxisURI(null);
            
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null pointer was thrown for null argument", expectedException);
    }

    public void testGetAxisURIFromURL() throws MalformedURLException {
        URL javaURL = new URL("http://www.testmenow.com");
        URI translatedURI = ProfileBeanUtils.getAxisURIFromURL(javaURL);
        assertEquals("Ensure translated URL is correect", javaURL.toString(), translatedURI.toString());
        
        NullPointerException expectedException = null;
        try {
            ProfileBeanUtils.getAxisURIFromURL(null);
            
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null pointer was thrown for null argument", expectedException);
    }
}
