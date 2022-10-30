/*
 * Created on May 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers.datagrid;

import java.util.Collections;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.model.ListDataModel;

import com.bluejungle.destiny.webui.tags.BaseJSFTest;
import com.bluejungle.framework.comp.ComponentManagerFactory;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/datagrid/DefaultGridLayoutStrategyFactoryTest.java#1 $
 */

public class DefaultGridLayoutStrategyFactoryTest extends BaseJSFTest {
    private DefaultGridLayoutStrategyFactory factoryToTest;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultGridLayoutStrategyFactoryTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        this.factoryToTest = (DefaultGridLayoutStrategyFactory) ComponentManagerFactory.getComponentManager().getComponent(DefaultGridLayoutStrategyFactory.class);
    }

    public void testGetGridLayoutStrategy() {
        UIData component = new UIData();
        List data = Collections.nCopies(10, new Object());
        component.setValue(new ListDataModel(data));
        IGridLayoutStrategy strategy = this.factoryToTest.getGridLayoutStrategy(facesContext, component);
        assertNotNull("Ensure strategy is not null one", strategy);
        assertTrue("Ensure strategy is of right type one", strategy instanceof SingleColumnGridLayoutStrategy);
        
        data = Collections.nCopies(11, new Object());
        component.setValue(new ListDataModel(data));
        strategy = this.factoryToTest.getGridLayoutStrategy(facesContext, component);
        assertNotNull("Ensure strategy is not null two", strategy);
        assertTrue("Ensure strategy is of right type two", strategy instanceof FixedColumnSizeGridLayoutStrategy);
        
        data = Collections.nCopies(15, new Object());
        component.setValue(new ListDataModel(data));
        strategy = this.factoryToTest.getGridLayoutStrategy(facesContext, component);
        assertNotNull("Ensure strategy is not null three", strategy);
        assertTrue("Ensure strategy is of right type three", strategy instanceof FixedColumnSizeGridLayoutStrategy);
        
        data = Collections.nCopies(16, new Object());
        component.setValue(new ListDataModel(data));
        strategy = this.factoryToTest.getGridLayoutStrategy(facesContext, component);
        assertNotNull("Ensure strategy is not null four", strategy);
        assertTrue("Ensure strategy is of right type four", strategy instanceof FixedColumnNumberGridLayoutStrategy);
    }

}
