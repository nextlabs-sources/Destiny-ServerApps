/*
 * Created on May 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.math.BigInteger;
import java.util.Calendar;

import com.bluejungle.destiny.inquirycenter.report.IReportDetailResult;
import com.bluejungle.destiny.types.effects.v1.EffectType;
import com.bluejungle.destiny.types.report_result.v1.DocumentActivityDetailResult;
import com.bluejungle.destiny.types.report_result.v1.PolicyActivityDetailResult;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;
import com.bluejungle.domain.action.ActionEnumType;

/**
 * This is the report detail result test class
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportDetailResultTest.java#1 $
 */

public class ReportDetailResultTest extends BaseJSFTest {

    /**
     * This test verifies the basic aspects of the class
     */
    public void testReportDetailResultClassBasics() {
        boolean exThrown = false;
        try {
            ReportDetailResultImpl result = new ReportDetailResultImpl((PolicyActivityDetailResult) null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("The ReportDetailResultImpl constructor should not accept null parameter", exThrown);

        exThrown = false;
        try {
            ReportDetailResultImpl result = new ReportDetailResultImpl((DocumentActivityDetailResult) null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("The ReportDetailResultImpl constructor should not accept null parameter", exThrown);

        PolicyActivityDetailResult detailResult = new PolicyActivityDetailResult();
        detailResult.setAction(ActionEnumType.ACTION_ACCESS_AGENT_LOGS.getName());
        detailResult.setTimestamp(Calendar.getInstance());
        detailResult.setEffect(EffectType.allow);
        detailResult.setFromResourceName("FromResourceName");
        detailResult.setHostName("MyHostName");
        detailResult.setId(BigInteger.ONE);
        detailResult.setPolicyName("myPolicy");
        detailResult.setToResourceName("ToResourceName");
        detailResult.setUserName("myUserName");
        detailResult.setLoggingLevel(1);
        ReportDetailResultImpl result = new ReportDetailResultImpl(detailResult);
        assertTrue("ReportDetailResultImpl should implement the right interface", result instanceof IReportDetailResult);
    }

    /**
     * This test verifies that the set / get properties are working for policy
     * activity
     */
    public void testReportDetailResultPropertiesForPolicyActivity() {
        PolicyActivityDetailResult wsDetailResult = new PolicyActivityDetailResult();
        final String action = ActionEnumType.ACTION_DELETE.getName();
        wsDetailResult.setAction(action);
        final Calendar now = Calendar.getInstance();
        wsDetailResult.setTimestamp(now);
        final EffectType effect = EffectType.allow;
        wsDetailResult.setEffect(effect);
        final String fromName = "file:///c/fromtest/from.txt";
        wsDetailResult.setFromResourceName(fromName);
        final String toName = "file:///c/totest/to.txt";
        wsDetailResult.setToResourceName(toName);
        final String host = "host";
        wsDetailResult.setHostName(host);
        final BigInteger id = new BigInteger("78");
        wsDetailResult.setId(id);
        final String policyFolderName = "/folderName";
        final String policyName = "myPolicyName";
        final String fullPolicyName = policyFolderName + "/" + policyName;
        wsDetailResult.setPolicyName(fullPolicyName);
        final String userName = "userNam";
        wsDetailResult.setUserName(userName);
        final int loggingLevel = 1;
        wsDetailResult.setLoggingLevel(loggingLevel);
        ReportDetailResultImpl result = new ReportDetailResultImpl(wsDetailResult);
        
        assertEquals("Date property should match", now.getTime(), result.getDate());
        assertEquals("Action property should match", "Delete", result.getAction());
        assertEquals("Enforcement property should match", "Allow/Monitor", result.getEnforcement());
        assertEquals("From Resource property should match", "/localhost/c/fromtest/from.txt", result.getFromResource());
        assertEquals("From Resource filename property should match", "from.txt", result.getFromResourceFilename());
        assertEquals("From Resource path property should match", "/localhost/c/fromtest", result.getFromResourcePath());
        assertEquals("To Resource property should match", "/localhost/c/totest/to.txt", result.getToResource());
        assertEquals("To Resource filename property should match", "to.txt", result.getToResourceFilename());
        assertEquals("To Resource path property should match", "/localhost/c/totest", result.getToResourcePath());
        assertEquals("Host property should match", host, result.getHost());
        assertEquals("Policy name property should be calculated properly", policyName, result.getPolicyName());
        assertEquals("Policy folder property should be calculated properly", policyFolderName, result.getPolicyFolderName());
        assertEquals("Full policy name property should be calculated properly", "/folderName/myPolicyName", result.getPolicyFullName());        
        assertEquals("User property should match", userName, result.getUser());
        assertEquals("Logging level should match", loggingLevel, result.getLoggingLevel());
    }

    /**
     * This test verifies that the set / get properties are working for tracking
     * activity
     */
    public void testReportDetailResultPropertiesForDocumentActivity() {
        DocumentActivityDetailResult wsDetailResult = new DocumentActivityDetailResult();
        final String action = ActionEnumType.ACTION_DELETE.getName();
        wsDetailResult.setAction(action);
        final Calendar now = Calendar.getInstance();
        wsDetailResult.setTimestamp(now);
        final String fromName = "file:///c/fromtest/from.txt";
        wsDetailResult.setFromResourceName(fromName);
        final String toName = "file:///c/totest/to.txt";
        wsDetailResult.setToResourceName(toName);
        final String host = "host";
        wsDetailResult.setHostName(host);
        final BigInteger id = new BigInteger("78");
        wsDetailResult.setId(id);
        final String userName = "userNam";
        wsDetailResult.setUserName(userName);
        final int loggingLevel = 1;
        wsDetailResult.setLoggingLevel(loggingLevel);
        ReportDetailResultImpl result = new ReportDetailResultImpl(wsDetailResult);
        
        assertEquals("Date property should match", now.getTime(), result.getDate());
        assertEquals("Action property should match", "Delete", result.getAction());
        assertEquals("From Resource property should match", "/localhost/c/fromtest/from.txt", result.getFromResource());
        assertEquals("From Resource filename property should match", "from.txt", result.getFromResourceFilename());
        assertEquals("From Resource path property should match", "/localhost/c/fromtest", result.getFromResourcePath());
        assertEquals("To Resource property should match", "/localhost/c/totest/to.txt", result.getToResource());
        assertEquals("To Resource filename property should match", "to.txt", result.getToResourceFilename());
        assertEquals("To Resource path property should match", "/localhost/c/totest", result.getToResourcePath());
        assertEquals("Host property should match", host, result.getHost());
        assertEquals("User property should match", userName, result.getUser());
        assertNull("Enforcement property should be null", result.getEnforcement());
        assertNull("Policy property should be null", result.getPolicyName());
        assertNull("Policy property should be null", result.getPolicyFolderName());
        assertNull("Policy property should be null", result.getPolicyFullName());
        assertEquals("Logging level should match", loggingLevel, result.getLoggingLevel());
    }
    
    /**
     * This test case tests the display of a very long from resource name
     * of a policy activity log
     */
    public void testLongFromResourceNamesPolicyActivity(){
        PolicyActivityDetailResult wsDetailResult = new PolicyActivityDetailResult();
        final String action = ActionEnumType.ACTION_DELETE.getName();
        wsDetailResult.setAction(action);
        final Calendar now = Calendar.getInstance();
        wsDetailResult.setTimestamp(now);
        final EffectType effect = EffectType.allow;
        wsDetailResult.setEffect(effect);
        final String fromName = "file:///c/fromaverylongtesttoasuperlongtest/someverylongfoldernameandevenlonger/" +
                                "aneventlongersubfoldernameandobscenelylongfile/superlongfilenameandsomemorechars.txt";
        wsDetailResult.setFromResourceName(fromName);
        final String toName = "file:///c/totest/to.txt";
        wsDetailResult.setToResourceName(toName);
        final String host = "host";
        wsDetailResult.setHostName(host);
        final BigInteger id = new BigInteger("78");
        wsDetailResult.setId(id);
        final String policyFolderName = "/folderName";
        final String policyName = "myPolicyName";
        final String fullPolicyName = policyFolderName + "/" + policyName;
        wsDetailResult.setPolicyName(fullPolicyName);
        final String userName = "userNam";
        wsDetailResult.setUserName(userName);
        final int loggingLevel = 1;
        wsDetailResult.setLoggingLevel(loggingLevel);
        ReportDetailResultImpl result = new ReportDetailResultImpl(wsDetailResult);
        
        assertEquals("Shortened from resource name should match", 
                     "/localhost/c/fromaverylongtesttoasuperlongtest/so ... /superlongfilenameandsomemorechars.txt", 
                     result.getFromResourceShortened());
    }
    
   /**
    * This test case tests the display of a very long from resource name
    * of a document activity log
    */
   public void testLongFromResourceNamesDocumentActivity(){
       DocumentActivityDetailResult wsDetailResult = new DocumentActivityDetailResult();
       final String action = ActionEnumType.ACTION_DELETE.getName();
       wsDetailResult.setAction(action);
       final Calendar now = Calendar.getInstance();
       wsDetailResult.setTimestamp(now);
       final String fromName = "file:///c/fromaverylongtesttoasuperlongtest/someverylongfoldernameandevenlonger/" +
                               "aneventlongersubfoldernameandobscenelylongfile/superlongfilenameandsomemorechars.txt";
       wsDetailResult.setFromResourceName(fromName);
       final String toName = "file:///c/totest/to.txt";
       wsDetailResult.setToResourceName(toName);
       final String host = "host";
       wsDetailResult.setHostName(host);
       final BigInteger id = new BigInteger("78");
       wsDetailResult.setId(id);
       final String userName = "userNam";
       wsDetailResult.setUserName(userName);
       final int loggingLevel = 1;
       wsDetailResult.setLoggingLevel(loggingLevel);
       ReportDetailResultImpl result = new ReportDetailResultImpl(wsDetailResult);
       
       assertEquals("Shortened from resource name should match", 
                    "/localhost/c/fromaverylongtesttoasuperlongtest/so ... /superlongfilenameandsomemorechars.txt", 
                    result.getFromResourceShortened());
   }
}
