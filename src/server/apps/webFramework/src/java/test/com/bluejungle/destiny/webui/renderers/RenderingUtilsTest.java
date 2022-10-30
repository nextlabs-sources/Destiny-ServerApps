/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * This is the test class for the rendering utility classes. All the utility
 * classes are tested with this test class.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/RenderingUtilsTest.java#1 $
 */

public class RenderingUtilsTest extends BaseJSFTest {

    /**
     * This test verifies that the weekdays are properly mapped in the date
     * utility.
     */
    public void testRenderUtilDateMapping() {
        assertEquals("Day mapping to int should work", 0, DateUtil.mapCalendarDayToCommonDay(Calendar.MONDAY));
        assertEquals("Day mapping to int should work", 1, DateUtil.mapCalendarDayToCommonDay(Calendar.TUESDAY));
        assertEquals("Day mapping to int should work", 2, DateUtil.mapCalendarDayToCommonDay(Calendar.WEDNESDAY));
        assertEquals("Day mapping to int should work", 3, DateUtil.mapCalendarDayToCommonDay(Calendar.THURSDAY));
        assertEquals("Day mapping to int should work", 4, DateUtil.mapCalendarDayToCommonDay(Calendar.FRIDAY));
        assertEquals("Day mapping to int should work", 5, DateUtil.mapCalendarDayToCommonDay(Calendar.SATURDAY));
        assertEquals("Day mapping to int should work", 6, DateUtil.mapCalendarDayToCommonDay(Calendar.SUNDAY));
        assertEquals("Day mapping to int should work", 0, DateUtil.mapCalendarDayToCommonDay(25));
    }

    /**
     * This test verifies that the weekdays are properly mapped in the date
     * utility.
     */
    public void testRenderUtilWeekDayMapping() {
        //Tpday, french lesson
        final String monday = "Lundi";
        final String tuesday = "Mardi";
        final String wednesday = "Mercredi";
        final String thursday = "Jeudi";
        final String friday = "Vendredi";
        final String saturday = "Samedi";
        final String sunday = "Dimanche";

        DateFormatSymbols sym = new DateFormatSymbols();
        final String[] frenchDays = { "", "Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi" };
        sym.setShortWeekdays(frenchDays);
        String[] result = DateUtil.mapWeekdays(sym);
        assertEquals("Array size should be the number of days in a week", 7, result.length);
        assertEquals("Array should have localized week days", monday, result[0]);
        assertEquals("Array should have localized week days", tuesday, result[1]);
        assertEquals("Array should have localized week days", wednesday, result[2]);
        assertEquals("Array should have localized week days", thursday, result[3]);
        assertEquals("Array should have localized week days", friday, result[4]);
        assertEquals("Array should have localized week days", saturday, result[5]);
        assertEquals("Array should have localized week days", sunday, result[6]);
    }

    /**
     * This test verifies that the context path is retrieved properly
     */
    public void testContextUtilPath() {
        final String ctxPath = "/path";
        MockFacesContext facesContext = new MockFacesContext();
        MockExternalContext extContext = new MockExternalContext(ctxPath);
        facesContext.setExternalContext(extContext);
        String result = ContextUtil.getContextPath(facesContext);
        assertEquals("Context path should be extracted from the JSF external context", ctxPath, result);
    }

    /**
     * This test verifies that the context path is taken into account when
     * building URL for resources.
     */
    public void testContextUtilResourcePath() {
        final String ctxPath = "/path";
        final String resPath = "/res";
        final String resPathNoSlash = "res";
        MockFacesContext facesContext = new MockFacesContext();
        MockExternalContext extContext = new MockExternalContext(ctxPath);
        facesContext.setExternalContext(extContext);

        //Check the case where the resource name has a slash
        String result = ContextUtil.getFullContextLocation(facesContext, resPath);
        assertEquals("Context path should be extracted from the JSF external context", ctxPath + resPath, result);

        //Check the case where the resource name has no slash
        result = ContextUtil.getFullContextLocation(facesContext, resPathNoSlash);
        assertEquals("Context path should deal with the case where the resource name has no slash", ctxPath + resPath, result);
    }
}