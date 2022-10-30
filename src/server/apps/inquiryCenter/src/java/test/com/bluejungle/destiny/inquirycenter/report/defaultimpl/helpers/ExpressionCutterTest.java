/*
 * Created on Jul 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers;

import com.bluejungle.destiny.types.basic.v1.StringList;
import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * This is the expression cutter test class.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/helpers/ExpressionCutterTest.java#1 $
 */

public class ExpressionCutterTest extends BaseDestinyTestCase {

    /**
     * This test verifies that the conversion from StringList to String works
     * properly.
     */
    public void testConvertFromStringListToString() {
        //Try null value
        assertNull("Expression cutter should handle null input", ExpressionCutter.convertFromStringList(null));
        //Try empty list
        StringList sl = new StringList();
        sl.setValues(null);
        assertNull("Expression cutter should handle empty StringList", ExpressionCutter.convertFromStringList(null));
        //Try real list
        final String STRING1 = "foo";
        final String STRING2 = "bar";
        final String STRING3 = "baz";
        sl.setValues(new String[] { STRING1, STRING2, STRING3 });
        String result = ExpressionCutter.convertFromStringList(sl);
        assertEquals("Expression cutter should format the list properly", STRING1 + ", " + STRING2 + ", " + STRING3, result);

        //Try empty list element
        final String EMPTYSTRING = "";
        sl.setValues(new String[] { STRING1, STRING2, EMPTYSTRING, STRING3 });
        result = ExpressionCutter.convertFromStringList(sl);
        assertEquals("Expression cutter should handle empty strings properly", STRING1 + ", " + STRING2 + ", " + EMPTYSTRING + ", " + STRING3, result);
    }

    /**
     * This test verifies that the conversion from String to StringList works
     * properly
     */
    public void testConvertStringToStringList() {
        //Try null input
        assertNull("Expression cutter should handle null input", ExpressionCutter.convertToStringList(null));
        //Try empty input
        StringList result = ExpressionCutter.convertToStringList("");
        assertNotNull("Expression cutter should handle empty string", result);
        assertNotNull("Empty string should still return an empty array", result.getValues());
        assertEquals("Empty string should still return an empty array", 0, result.getValues().length);

        //Try Various expressions
        result = ExpressionCutter.convertToStringList("A, B, C, D");
        assertEquals("Conversion to StringList should work properly", 4, result.getValues().length);
        assertEquals("Conversion to StringList should work properly", "A", result.getValues()[0]);
        assertEquals("Conversion to StringList should work properly", "B", result.getValues()[1]);
        assertEquals("Conversion to StringList should work properly", "C", result.getValues()[2]);
        assertEquals("Conversion to StringList should work properly", "D", result.getValues()[3]);

        result = ExpressionCutter.convertToStringList("AAAA");
        assertEquals("Conversion to StringList should work properly", 1, result.getValues().length);
        assertEquals("Conversion to StringList should work properly", "AAAA", result.getValues()[0]);

        result = ExpressionCutter.convertToStringList("AAAA,,,,");
        assertEquals("Conversion to StringList should work properly", 1, result.getValues().length);
        assertEquals("Conversion to StringList should work properly", "AAAA", result.getValues()[0]);

        result = ExpressionCutter.convertToStringList("AAAA,,,BBB");
        assertEquals("Conversion to StringList should work properly", 2, result.getValues().length);
        assertEquals("Conversion to StringList should work properly", "AAAA", result.getValues()[0]);
        assertEquals("Conversion to StringList should work properly", "BBB", result.getValues()[1]);

        result = ExpressionCutter.convertToStringList("AAAA,,,BBB,C");
        assertEquals("Conversion to StringList should work properly", 3, result.getValues().length);
        assertEquals("Conversion to StringList should work properly", "AAAA", result.getValues()[0]);
        assertEquals("Conversion to StringList should work properly", "BBB", result.getValues()[1]);
        assertEquals("Conversion to StringList should work properly", "C", result.getValues()[2]);
    }
}