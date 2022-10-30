/*
 * Created on May 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIData;

/**
 * JUnit Test for DataGridTag
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/DataGridTagTest.java#1 $
 */
public class DataGridTagTest extends BaseJSFTest {

    private DataGridTag tagToTest;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DataGridTagTest.class);
    }

    /*
     * @see BaseJSFTest#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        this.tagToTest = new DataGridTag();
    }

    /*
     * Class under test for String getComponentType()
     */
    public void testGetComponentType() {
        assertEquals("Ensure component type is consistent", DataGridTag.COMPONENT_TYPE, tagToTest.getComponentType());
    }

    /*
     * Class under test for String getRendererType()
     */
    public void testGetRendererType() {
        assertEquals("Ensure renderer type is consistent", DataGridTag.RENDERER_TYPE, tagToTest.getRendererType());
    }

    /*
     * Tests all setters and setProperties
     */
    public void testSetProperties() {
        UIData uiDataComponent = new UIData();
        
        String var = "myVar";
        String styleClass = "myStyleClass";
        
        this.tagToTest.setVar(var);
        this.tagToTest.setStyleClass(styleClass);
        
        this.tagToTest.setProperties(uiDataComponent);
        
        assertEquals("Ensure var field was property set.", var, uiDataComponent.getAttributes().get("var"));
        assertEquals("Ensure styleClass field was property set.", styleClass, uiDataComponent.getAttributes().get("styleClass"));        
    }

}
