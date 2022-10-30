/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.bluejungle.destiny.webui.renderers.datagrid.DefaultGridLayoutStrategyFactoryTest;

/**
 * This is the test suite for the renderer classes.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/RenderersTestSuite.java#1 $
 */

public class RenderersTestSuite {

    /**
     * Returns the set of tests to be run in the test suite
     * 
     * @return the set of tests to be run in the test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Renderers Tests");
        suite.addTest(new TestSuite(RenderingUtilsTest.class, "Rendering utilities"));
        suite.addTest(new TestSuite(BarItemRendererTest.class, "Bar item Renderer"));
        suite.addTest(new TestSuite(CSSIncludeRendererTest.class, "CSS Renderer"));
        suite.addTest(new TestSuite(JSIncludeRendererTest.class, "JS Renderer"));
        suite.addTest(new TestSuite(HtmlDatePickerRendererTest.class, "Date picker"));
        suite.addTest(new TestSuite(HtmlSortColumnHeaderRendererTest.class, "Scrolling data control"));
        suite.addTest(new TestSuite(HtmlSortColumnHeaderRendererTest.class, "Sort header"));
        suite.addTest(new TestSuite(MessagesRendererTest.class, "Messages Renderer"));
        suite.addTest(new TestSuite(DefaultGridLayoutStrategyFactoryTest.class, "Default Grid Layout Factory"));
        return suite;
    }
}