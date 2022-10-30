/*
 * Created on Mar 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import org.apache.myfaces.custom.sortheader.HtmlCommandSortHeader;
import org.apache.myfaces.custom.sortheader.HtmlCommandSortHeaderTag;

/**
 * This is the test class for the sort header tag
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/SortHeaderTagTest.java#1 $
 */

public class SortHeaderTagTest extends BaseJSFTest {

    /**
     * This test verifies the basic characteristics of the sort header tag class
     */
    public void testSortHeaderTagBasics() {
        SortHeaderTag tag = new SortHeaderTag();
        assertTrue("Sort header tag should extends the approprirate myFaces tag", tag instanceof HtmlCommandSortHeaderTag);
        HtmlCommandSortHeaderTag parent = new HtmlCommandSortHeaderTag();
        assertEquals("Sort header tag should have the same component type as the parent myFaces tag", parent.getComponentType(), tag.getComponentType());
        assertEquals("Sort header tag should have the correct renderer", "com.bluejungle.destiny.SortHeaderRenderer", tag.getRendererType());
    }

    /**
     * This test verifies that the tag stores property values properly and
     * passes them appropriately to the component object.
     */
    public void testSortHeaderProperties() {
        //Prepare the tag
        final String downClassName = "myDownClass";
        final String upClassName = "myUpClass";
        SortHeaderTag tag = new SortHeaderTag();
        tag.setSortDownClassName(downClassName);
        tag.setSortUpClassName(upClassName);

        assertEquals("Sort header tag should remember the down class name property", downClassName, tag.getSortDownClassName());
        assertEquals("Sort header tag should remember the up class name property", upClassName, tag.getSortUpClassName());

        //Pass on the values to the component
        HtmlCommandSortHeader component = new HtmlCommandSortHeader();
        tag.setProperties(component);
        assertEquals("sort header tag should set the arrow of the UI component to true", new Boolean(true), component.getAttributes().get("arrow"));
        assertEquals("sort header tag should pass the sort down class name to the UI component", downClassName, component.getAttributes().get("sortDownClassName"));
        assertEquals("sort header tag should pass the sort up class name to the UI component", upClassName, component.getAttributes().get("sortUpClassName"));
    }

}