/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIComponent;

import org.apache.myfaces.component.UIColumns;

import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * This is the test class for the horizontal column control
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UIHorColumnTest.java#1 $
 */

public class UIHorColumnTest extends BaseDestinyTestCase {

    /**
     * Tests the basic features of the horizontal column control class
     */
    public void testHorColumnControlBasics() {
        UIHorColumn horColumn = new UIHorColumn();
        assertTrue("Horizontal Column control should be a JSF component", horColumn instanceof UIComponent);
        assertTrue("Horizontal Column control should extends the myfaces horizontal column component", horColumn instanceof UIColumns);
    }

    /**
     * This test verifies that the client id of the children gets reset properly
     * upon setting the row index of the component.
     */
    public void testClientIdResetting() {
        UIHorColumn horColumn = new UIHorColumn();
        horColumn.setId("id");
        UIHorColumn headerOutput = new UIHorColumn();
        headerOutput.setId("headerText");
        UIHorColumn footerOutput = new UIHorColumn();
        footerOutput.setId("footerText");
        horColumn.getFacets().put("header", headerOutput);
        horColumn.getFacets().put("footer", footerOutput);
        UIHorColumn link = new UIHorColumn();
        link.setId("idLink");
        horColumn.getChildren().add(link);
        UIHorColumn linkText = new UIHorColumn();
        linkText.setId("linkText");
        link.getChildren().add(linkText);

        assertEquals("Default row index should be set to -1", -1, horColumn.getRowIndex());
        MockFacesContext facesContext = new MockFacesContext();
        final String columnId = horColumn.getClientId(facesContext);
        final String headerId = headerOutput.getClientId(facesContext);
        final String footerId = footerOutput.getClientId(facesContext);
        final String linkId = link.getClientId(facesContext);
        final String linkTextId = linkText.getClientId(facesContext);

        horColumn.setRowIndex(0);
        assertNotSame("Client id for the hor column should change with row index", columnId, horColumn.getClientId(facesContext));
        assertNotSame("Client id for the hor column header should change with row index", headerId, headerOutput.getClientId(facesContext));
        assertNotSame("Client id for the hor column footer should change with row index", footerId, footerOutput.getClientId(facesContext));
        assertNotSame("Client id for the children of the hor column should change with row index", linkId, link.getClientId(facesContext));
        assertNotSame("Client id for the hor column footer should change with row index", linkTextId, linkText.getClientId(facesContext));
        
        horColumn.setRowIndex(-1);
        assertEquals("Client id for the hor column should change with row index", columnId, horColumn.getClientId(facesContext));
        assertEquals("Client id for the hor column header should change with row index", headerId, headerOutput.getClientId(facesContext));
        assertEquals("Client id for the hor column footer should change with row index", footerId, footerOutput.getClientId(facesContext));
        assertEquals("Client id for the children of the hor column should change with row index", linkId, link.getClientId(facesContext));
        assertEquals("Client id for the hor column footer should change with row index", linkTextId, linkText.getClientId(facesContext));
    }
}