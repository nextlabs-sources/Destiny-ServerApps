/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import com.bluejungle.destiny.webui.renderers.HtmlListScrollerRendererTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is the test suite for the tag library
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/TagLibTestSuite.java#2 $
 */

public class TagLibTestSuite {

    /**
     * Returns the set of tests to be run in the test suite
     * 
     * @return the set of tests to be run in the test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("JSF Tag Library");
        suite.addTest(new TestSuite(CSSIncludeTagTest.class, "CSSInclude Tag"));
        suite.addTest(new TestSuite(BarItemTagTest.class, "Bar Item Tag"));
        suite.addTest(new TestSuite(ColumnsTagTest.class, "Columns Tag"));
        suite.addTest(new TestSuite(DataTableTagTest.class, "Data Table Tag"));
        suite.addTest(new TestSuite(DisplayMessagesTagTest.class, "Display Messages Tag"));
        suite.addTest(new TestSuite(HtmlFormTagTest.class, "Form Tag"));
        suite.addTest(new TestSuite(JSIncludeTagTest.class, "JSInclude Tag"));
        suite.addTest(new TestSuite(HtmlListScrollerRendererTest.class, "List scroller Tag"));
        suite.addTest(new TestSuite(RowTagTest.class, "Row Tag"));
        suite.addTest(new TestSuite(SortHeaderTagTest.class, "Sort header Tag"));
        suite.addTest(new TestSuite(DataGridTagTest.class, "Data Grid Tag"));
        suite.addTest(new TestSuite(InputPasswordTagTest.class, "Input Password Tag"));
        suite.addTest(new TestSuite(TabTagTest.class, "Tab Tag"));
        suite.addTest(new TestSuite(SelectedTabChangeListenerTagTest.class, "Selected Tab Change Event Listener Tag"));
        suite.addTest(new TestSuite(TagUtilTest.class, "TagUtil class"));
        suite.addTest(getMenuTagsTestSuite());
        return suite;
    }

    /**
     * Retrieve the test for testing menu related tags
     * 
     * @return the test suite for testing menu related tags
     */
    private static Test getMenuTagsTestSuite() {
        TestSuite testToReturn = new TestSuite("MenuTestSuite");
        testToReturn.addTest(MenuTagTest.suite());
        testToReturn.addTest(MenuItemTagTest.suite());

        return testToReturn;
    }
}