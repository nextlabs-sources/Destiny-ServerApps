package com.bluejungle.destiny.webui.browsabledatapicker.converters;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the token converter
 *
 * @author hchan
 * @date Apr 19, 2007
 */
public class BrowsableDataPickerConverterTestSuite {
	public static void main(String[] args) {
        junit.textui.TestRunner.run(BrowsableDataPickerConverterTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.bluejungle.destiny.webui.browsabledatapicker.converter");
        //$JUnit-BEGIN$
        suite.addTestSuite(TokenConverterTest.class);
        //$JUnit-END$
        return suite;
    }
}
