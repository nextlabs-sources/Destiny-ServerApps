/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.bluejungle.destiny.webui.browsabledatapicker.converters.BrowsableDataPickerConverterTestSuite;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BrowsableDataPickerTestSuite;
import com.bluejungle.destiny.webui.controls.JSFControlsTestSuite;
import com.bluejungle.destiny.webui.framework.data.DataModelTestSuite;
import com.bluejungle.destiny.webui.framework.flip.DataFlipperTestSuite;
import com.bluejungle.destiny.webui.framework.sort.SortStateMgrTestSuite;
import com.bluejungle.destiny.webui.renderers.RenderersTestSuite;
import com.bluejungle.destiny.webui.tags.TagLibTestSuite;
import com.nextlabs.destiny.webui.validators.ValidatorsTestSuite;

/**
 * This is the test suite for the Web Framework
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/test/WebFrameworkTestSuite.java#2 $
 */

public class WebFrameworkTestSuite {

    /**
     * Returns the suite of tests
     * 
     * @return the suite of tests
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Web Framework");
        suite.addTest(TagLibTestSuite.suite());
        suite.addTest(JSFControlsTestSuite.suite());
        suite.addTest(DataFlipperTestSuite.suite());
        suite.addTest(DataModelTestSuite.suite());
        suite.addTest(RenderersTestSuite.suite());
      	suite.addTest(SortStateMgrTestSuite.suite());
        suite.addTest(BrowsableDataPickerTestSuite.suite());
        suite.addTest(BrowsableDataPickerConverterTestSuite.suite());
        suite.addTest(ValidatorsTestSuite.suite());
        return suite;
    }
}
