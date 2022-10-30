/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/HtmlDatePickerRendererTest.java#1 $
 */

public class HtmlDatePickerRendererTest extends BaseJSFTest {

    /**
     * This test verifies the basic aspects of the date picker renderer class.
     */
    public void testHtmlDatePickerRendererClassBasics() {
        HTMLDatePickerRenderer renderer = new HTMLDatePickerRenderer();
        assertTrue ("date picker renderer should extend myFaces date picker renderer", renderer instanceof HTMLDatePickerRenderer);
        assertTrue ("date picker renderer should render its children components", renderer.getRendersChildren());
    }

    /**
     * This test verifies that the renderer can extract the input component
     * within a calendar component.
     */
    public void testHtmlDatePickerRendererGetInputComponent() {

    }
}