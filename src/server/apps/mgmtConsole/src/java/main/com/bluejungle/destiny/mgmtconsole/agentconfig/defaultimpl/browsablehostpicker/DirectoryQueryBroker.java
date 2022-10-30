/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import com.bluejungle.dictionary.Dictionary;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.DictionaryPath;
import com.bluejungle.dictionary.IDictionary;
import com.bluejungle.dictionary.IDictionaryIterator;
import com.bluejungle.dictionary.IElementType;
import com.bluejungle.dictionary.IMElement;
import com.bluejungle.dictionary.IMGroup;
import com.bluejungle.domain.enrollment.ElementTypeEnumType;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.IManagerEnabled;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.expressions.CompositePredicate;
import com.bluejungle.framework.expressions.BooleanOp;
import com.bluejungle.framework.expressions.IPredicate;

/**
 * The Directory Query Broker is a facade (change the name of the class?) for
 * accessing the Dictionary. Contained methods are built specifically for
 * support the browse hosts view utilized within the agent configuration view of
 * the management console
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/DirectoryQueryBroker.java#2 $
 */
public class DirectoryQueryBroker implements IHasComponentInfo<DirectoryQueryBroker>, IInitializable, IManagerEnabled {

    static final String COMPONENT_NAME = DirectoryQueryBroker.class.getName();

    private static final ComponentInfo<DirectoryQueryBroker> COMP_INFO = 
        new ComponentInfo<DirectoryQueryBroker>(
                COMPONENT_NAME, 
                DirectoryQueryBroker.class, 
                LifestyleType.SINGLETON_TYPE);

    private static IDictionary dictionary;
    private IComponentManager manager;

    /**
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    public void init() {
        dictionary = manager.getComponent(Dictionary.COMP_INFO);
    }

    /**
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    public ComponentInfo<DirectoryQueryBroker> getComponentInfo() {
        return COMP_INFO;
    }

    /**
     * Retrieve the Set of all host groups from the directory
     * 
     * @return the Set of all host groups from the directory
     */
    public Set<IMGroup> getAllHostGroups() throws DictionaryException {
        Set<IMGroup> allHostGroups = new HashSet<IMGroup>();
        IElementType hostType = dictionary.getType(ElementTypeEnumType.COMPUTER.getName());
        IDictionaryIterator<IMGroup> iter = dictionary.getStructuralGroups( DictionaryPath.ROOT, "%", hostType, new Date(), null);
        try {
            while( iter.hasNext() ) {
                IMGroup group = iter.next();
                allHostGroups.add(group);
            }
        } finally {
            iter.close();
        }
        iter = dictionary.getEnumeratedGroups("%", hostType, new Date(), null);
        try {
            while( iter.hasNext() ) {
                IMGroup group = iter.next();
                allHostGroups.add(group);
            }
        } finally {
            iter.close();
        }
        return allHostGroups;
    }

    /**
     * Retrieve the set of hosts for a particular dictionary Aggregate group
     * 
     * @param group
     * @return the set of hosts for a particular Aggregate group
     */
    Set<IMElement> getHostsForGroup(IMGroup group) throws DictionaryException {
        Set<IMElement> hosts = new HashSet<IMElement>();
        IElementType hostType = dictionary.getType(ElementTypeEnumType.COMPUTER.getName());

        ArrayList<IPredicate> preds = new ArrayList<IPredicate>();
        preds.add(group.getTransitiveMembershipPredicate());
        preds.add(dictionary.condition(hostType));

        IDictionaryIterator<IMElement> hostsIter =
                dictionary.query(new CompositePredicate(BooleanOp.AND, preds), dictionary
                        .getLatestConsistentTime(), null, null);

        try {
            while (hostsIter.hasNext()) {
                hosts.add(hostsIter.next());
            }
        } finally {
            hostsIter.close();
        }

        return hosts;
    }

    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#setManager(com.bluejungle.framework.comp.IComponentManager)
     */
    public void setManager(IComponentManager manager) {
        this.manager = manager;
    }

    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#getManager()
     */
    public IComponentManager getManager() {
        return this.manager;
    }
}
