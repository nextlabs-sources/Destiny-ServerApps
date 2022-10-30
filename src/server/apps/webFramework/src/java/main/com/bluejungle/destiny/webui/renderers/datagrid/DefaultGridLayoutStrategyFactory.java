/*
 * Created on May 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers.datagrid;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.IManagerEnabled;
import com.bluejungle.framework.comp.LifestyleType;

/**
 * Default implementation of the IGridLayoutStrategoryFactory interface.
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/DefaultGridLayoutStrategyFactory.java#2 $
 */
public class DefaultGridLayoutStrategyFactory implements IGridLayoutStrategoryFactory,
		IHasComponentInfo<DefaultGridLayoutStrategyFactory>, IManagerEnabled, IInitializable {

	private static final String EMPTY_GRID_LAYOUT_STRATEGY_COMP_NAME =
			EmptyGridLayoutStrategy.class.getName();
	private static final String SINGLE_COLUMN_LAYOUT_STRATEGY_COMP_NAME =
			SingleColumnGridLayoutStrategy.class.getName();
	private static final String FIXED_COLUMN_SIZE_LAYOUT_STRATEGY_COMP_NAME =
			FixedColumnSizeGridLayoutStrategy.class.getName();
	private static final String FIXED_COLUMN_NUMBER_LAYOUT_STRATEGY_COMP_NAME =
			FixedColumnNumberGridLayoutStrategy.class.getName();

	private static final int FIXED_COLUMN_SIZE_LAYOUT_STRATEGY_THRESHOLD = 10;
	private static final int FIXED_COLUMN_NUMBER_LAYOUT_STRATEGY_THRESHOLD = 15;

    private ComponentInfo<DefaultGridLayoutStrategyFactory> componentInfo =
			new ComponentInfo<DefaultGridLayoutStrategyFactory>(
					DefaultGridLayoutStrategyFactory.class.getName(),
					DefaultGridLayoutStrategyFactory.class,
					IGridLayoutStrategoryFactory.class, 
					LifestyleType.SINGLETON_TYPE);
    private IComponentManager componentManager;

    private IGridLayoutStrategy singleColumnStrategy;
    private IGridLayoutStrategy fixedColumnSizeStrategy;
    private IGridLayoutStrategy fixedColumnNumberStrategy;
    private IGridLayoutStrategy emptyGridStrategy;

    /**
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    public ComponentInfo<DefaultGridLayoutStrategyFactory> getComponentInfo() {
        return componentInfo;
    }

    /**
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    public void init() {
        IComponentManager componentManager = getManager();

        ComponentInfo<EmptyGridLayoutStrategy> emptyGridStrategyCompInfo =
				new ComponentInfo<EmptyGridLayoutStrategy>(
						EMPTY_GRID_LAYOUT_STRATEGY_COMP_NAME,
						EmptyGridLayoutStrategy.class, 
						IGridLayoutStrategy.class, 
						LifestyleType.SINGLETON_TYPE);
		this.emptyGridStrategy = componentManager.getComponent(emptyGridStrategyCompInfo);

		ComponentInfo<SingleColumnGridLayoutStrategy> singleColumnStrategyCompInfo = 
				new ComponentInfo<SingleColumnGridLayoutStrategy>(
						SINGLE_COLUMN_LAYOUT_STRATEGY_COMP_NAME,
						SingleColumnGridLayoutStrategy.class, 
						IGridLayoutStrategy.class, 
						LifestyleType.SINGLETON_TYPE);
		this.singleColumnStrategy =	componentManager.getComponent(singleColumnStrategyCompInfo);

		ComponentInfo<FixedColumnSizeGridLayoutStrategy> fixedColumnSizeStrategyCompInfo =
				new ComponentInfo<FixedColumnSizeGridLayoutStrategy>(
						FIXED_COLUMN_SIZE_LAYOUT_STRATEGY_COMP_NAME,
						FixedColumnSizeGridLayoutStrategy.class,
						IGridLayoutStrategy.class, 
						LifestyleType.SINGLETON_TYPE);
		this.fixedColumnSizeStrategy = componentManager.getComponent(fixedColumnSizeStrategyCompInfo);

		ComponentInfo<FixedColumnNumberGridLayoutStrategy> fixedColumnNumberStrategyCompInfo =
				new ComponentInfo<FixedColumnNumberGridLayoutStrategy>(
						FIXED_COLUMN_NUMBER_LAYOUT_STRATEGY_COMP_NAME,
						FixedColumnNumberGridLayoutStrategy.class,
						IGridLayoutStrategy.class, 
						LifestyleType.SINGLETON_TYPE);
		this.fixedColumnNumberStrategy = componentManager.getComponent(fixedColumnNumberStrategyCompInfo);
	}

    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategoryFactory#getGridLayoutStrategy(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public IGridLayoutStrategy getGridLayoutStrategy(FacesContext context, UIComponent component) {
        if (!(component instanceof UIData)) {
			throw new IllegalArgumentException("Component type not supported: "
					+ component.getClass().getName());
		}

        IGridLayoutStrategy strategyToReturn = null;

        UIData uiData = (UIData) component;

        int firstRowToDisplay = uiData.getFirst();
        int numRowsToDisplay = uiData.getRows();
        if (numRowsToDisplay <= 0) {
            int totalRowCount = uiData.getRowCount();
            numRowsToDisplay = totalRowCount - firstRowToDisplay;
        }

        if (numRowsToDisplay > FIXED_COLUMN_NUMBER_LAYOUT_STRATEGY_THRESHOLD) {
            strategyToReturn = this.fixedColumnNumberStrategy;
        } else if (numRowsToDisplay > FIXED_COLUMN_SIZE_LAYOUT_STRATEGY_THRESHOLD) {
            strategyToReturn = this.fixedColumnSizeStrategy;
        } else if (numRowsToDisplay > 0) {
            strategyToReturn = this.singleColumnStrategy;
        } else {
            strategyToReturn = this.emptyGridStrategy; 
        }

        return strategyToReturn;
    }

    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#setManager(com.bluejungle.framework.comp.IComponentManager)
     */
    public void setManager(IComponentManager manager) {
        if (manager == null) {
            throw new NullPointerException("manager cannot be null.");
        }

        this.componentManager = manager;
    }

    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#getManager()
     */
    public IComponentManager getManager() {
        return this.componentManager;
    }
}