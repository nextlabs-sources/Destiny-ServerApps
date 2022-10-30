package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker;

/*
 * Created on May 13, 2005
 *
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentConfigurationBeanImpl;
import com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentQueryBroker;
import com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.MockAgentQueryBroker;
import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.DictionaryKey;
import com.bluejungle.dictionary.DictionaryPath;
import com.bluejungle.dictionary.IDictionaryIterator;
import com.bluejungle.dictionary.IElementBase;
import com.bluejungle.dictionary.IElementField;
import com.bluejungle.dictionary.IElementType;
import com.bluejungle.dictionary.IElementVisitor;
import com.bluejungle.dictionary.IEnrollment;
import com.bluejungle.dictionary.IGroupChanges;
import com.bluejungle.dictionary.IMElement;
import com.bluejungle.dictionary.IMGroup;
import com.bluejungle.dictionary.IReferenceable;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.expressions.IPredicate;

/**
 * JUnit test for HostSelectableItemSourceImpl - TODO There are some tricky methods to test in this class.
 *
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/HostSelectableItemSourceImplTest.java#1 $
 */
public class HostSelectableItemSourceImplTest extends TestCase {

    private HostSelectableItemSourceImpl itemSourceToTest;

    private HostSelectedItem[] selectedItems;
    private AgentDTO[] agents;

    private IMGroup mockGroup;
    private IMElement mockHost;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(HostSelectableItemSourceImpl.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        mockGroup = new MockDictionaryGroup();
        mockHost = new MockDictionaryHost();
        
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();

        ComponentInfo<MockDirectoryQueryBroker> mockDirectoryBrokerInfo = 
            new ComponentInfo<MockDirectoryQueryBroker>(
                DirectoryQueryBroker.COMPONENT_NAME, 
                MockDirectoryQueryBroker.class, 
                LifestyleType.SINGLETON_TYPE);
        MockDirectoryQueryBroker mockDirectoryBroker = compMgr.getComponent(mockDirectoryBrokerInfo);
        mockDirectoryBroker.setEnumGroups(Collections.singleton(mockGroup));
        mockDirectoryBroker.setStructuralGroups(new HashSet());

        agents = new AgentDTO[1];
        agents[0] = new AgentDTO();
        agents[0].setHost("gimmesomesugarbaby.com");

        ComponentInfo<MockAgentQueryBroker> mockAgentBrokerInfo = 
            new ComponentInfo<MockAgentQueryBroker>(
                    AgentQueryBroker.COMPONENT_NAME, 
                    MockAgentQueryBroker.class, 
                    LifestyleType.SINGLETON_TYPE);
        MockAgentQueryBroker mockAgentBroker = compMgr.getComponent(mockAgentBrokerInfo);
        mockAgentBroker.setAgents(agents);

        AgentConfigurationBeanImpl agentConfigBean = new AgentConfigurationBeanImpl();

        this.itemSourceToTest = new HostSelectableItemSourceImpl();
        this.itemSourceToTest.setAgentConfigurationBean(agentConfigBean);
    }

    /*
     * Class under test for DataModel
     * getSelectableItems(ISearchBucketSearchSpec, ISelectedItemList)
     */
    public void testGetSelectableItemsForISearchBucketSearchSpec() {

        //TODO Implement getSelectableItems().
    }

    /*
     * Class under test for DataModel getSelectableItems(IFreeFormSearchSpec,
     * ISelectedItemList)
     */
    public void testGetSelectableItemsIFreeFormSearchSpecISelectedItemList() {
        //TODO Implement getSelectableItems().
    }

    public void testGenerateSelectedItems() {
        //TODO Implement generateSelectedItems().
    }

    public void testStoreSelectedItems() throws SelectableItemSourceException {
        // How do I test this?


        NullPointerException expectedException = null;
        try {
            this.itemSourceToTest.storeSelectedItems(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null pointer was thrown", expectedException);
    }

    public void testCancelDataSelection() {
        String returnAction = "foo";
        this.itemSourceToTest.setReturnAction(returnAction);

        String actionReturned = this.itemSourceToTest.cancelDataSelection();
        assertEquals("Ensure return action from cancel is returned as expected", returnAction, actionReturned);
    }

    public void testSetAgentConfigurationBean() {
        // Make sure it doesn't blow up
        AgentConfigurationBeanImpl agentConfigBean = new AgentConfigurationBeanImpl();
        this.itemSourceToTest.setAgentConfigurationBean(agentConfigBean);

        NullPointerException expectedException = null;
        try {
            this.itemSourceToTest.setAgentConfigurationBean(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null pointer was thrown", expectedException);
    }

    public void testSetReturnAction() {
        // Just make it doesn't blow up
        String returnAction = "foo";
        this.itemSourceToTest.setReturnAction(returnAction);

        assertEquals("Ensure return action is returned as expected", returnAction, this.itemSourceToTest.cancelDataSelection());

        NullPointerException expectedException = null;
        try {
            this.itemSourceToTest.setReturnAction(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null pointer was thrown", expectedException);
    }

    public void testReset() {
        // Just make it doesn't blow up
        this.itemSourceToTest.reset();
        // Nothing to test. Method currently does nothing
    }

    class MockElementIter implements IDictionaryIterator<IMElement> {

        boolean hasNext = true;

        public IMElement next() throws DictionaryException {
            hasNext = false;
            return mockHost;
        }

        public void close() throws DictionaryException {
        }

        public boolean hasNext() throws DictionaryException {
            return hasNext;
        }

    }

    private class MockDictionaryGroup implements IMGroup {
        private String name;
        public void setName(String name) {
            this.name = name;
        }
        public IDictionaryIterator<IMGroup> getAllChildGroups() throws DictionaryException {
            return null;
        }
        public IDictionaryIterator<IMGroup> getDirectChildGroups() throws DictionaryException {
            return null;
        }
        public IDictionaryIterator<IMElement> getAllChildElements() throws DictionaryException {
            return new MockElementIter();
        }
        public IDictionaryIterator<IMElement> getDirectChildElements() throws DictionaryException {
            return new MockElementIter();
        }

        public void addChild(IReferenceable element) throws DictionaryException {
        }

        public void removeChild(IReferenceable element) throws DictionaryException {
        }
        public String getName() {
            return name;
        }
        public IPredicate getDirectMembershipPredicate() {
            return null;
        }
        public IPredicate getTransitiveMembershipPredicate() {
            return null;

        }
        public Long getInternalKey() {
            return new Long(1008);
        }
        public DictionaryKey getExternalKey() {
            return null;
        }

        public String getDisplayName() {
            return null;
        }
        public String getUniqueName() {
            return null;
        }
        public DictionaryPath getPath() {
            return new DictionaryPath(new String[] {name} );
        }

        public IEnrollment getEnrollment() {
            return null;
        }

        public boolean setUniqueName(String xxx) {
            return false;
        }

        public boolean setDisplayName(String xxx) {
            return false;
        }

        public boolean setPath(DictionaryPath path) {
            return false;
        }

        public boolean setExternalKey(DictionaryKey key) {
            return false;
        }
        public void accept(IElementVisitor visitor) {
            visitor.visitGroup(this);
        }
        public IGroupChanges getChanges(IElementType type, Date startDate, Date endDate) {
            return null;
        }
        public IElementType getType() {
            throw new UnsupportedOperationException();
        }
        public IDictionaryIterator<DictionaryPath> getAllReferenceMemebers() throws DictionaryException {
            throw new UnsupportedOperationException();
        }
        
    }

    private class MockDictionaryHost implements IMElement {

        /**
         * @see IElementBase#getUniqueName()
         */
        public String getUniqueName() {
            return "foobar.foo.bar.com";
        }

        /**
         * @see IElementBase#getSystemId()
         */
        public String getSystemId() {
            return null;
        }

        /**
         * @see IElementBase#getInternalKey()
         */
        public Long getInternalKey() {
            return new Long(1002);
        }

        /**
         * @see IElementBase#getDisplayName()
         */
        public String getDisplayName() {
            return null;
        }

        public DictionaryKey getExternalKey() {
            return new DictionaryKey("foobar.foo.bar.com".getBytes() );
        }

        public DictionaryPath getPath() {
            return new DictionaryPath( new String[] {"com","bar","foo","foobar"} );
        }

        public Object getValue( IElementField field ) {
            return "foobar.foo.bar.com";
        }

        public IElementType getType() {
            return null;
        }

        public IEnrollment getEnrollment() {
            return null;
        }
        public boolean setValue( IElementField field, Object value ) {
            return false;
        }
        public boolean setUniqueName(String xxx) {
            return false;
        }
        public boolean setDisplayName(String xxx) {
            return false;
        }
        public boolean setPath(DictionaryPath path) {
            return false;
        }
        public boolean setExternalKey(DictionaryKey key) {
            return false;
        }
        public void accept(IElementVisitor visitor) {
            visitor.visitLeaf(this);
        }

        @Override
        public boolean setValue(String externalName, Object value) {
           return false;
        }

        @Override
        public Object getValue(String externalName) {
            return null;
        }
    }

    class MockDictionaryQueryBroker extends DirectoryQueryBroker {
        private Set<IMGroup> enumGroups;
        private Set<IMGroup> structuralGroups;

        public void init() {}

        /**
         * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker.DirectoryQueryBroker#getEnumGroups()
         */
        Set<IMGroup> getEnumGroups() {
            return this.enumGroups;
        }
        /**
         * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker.DirectoryQueryBroker#getHostsForGroup(com.bluejungle.ldap.interfaces.entry.ILDAPAggregate)
         */
        Set<IMElement> getHostsForGroup(IMGroup group) {
            Set<IMElement> results = new HashSet<IMElement>();
            try {
                IDictionaryIterator<IMElement> iter = group.getAllChildElements();
                while( iter.hasNext() ) {
                    results.add( iter.next() );
                }
            }
            catch (DictionaryException e) {
                throw new RuntimeException(e);
            }
            return results;
        }
        /**
         * @see com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker.DirectoryQueryBroker#getStructuralGroups()
         */
        Set<IMGroup> getStructuralGroups() {
            return this.structuralGroups;
        }
        /**
         * @param set
         */
        public void setEnumGroups(Set<IMGroup> enumGroups) {
            this.enumGroups = enumGroups;
        }

        /**
         * @param set
         */
        public void setStructuralGroups(Set<IMGroup> structuralGroups) {
            this.structuralGroups = structuralGroups;
        }
    }
}
