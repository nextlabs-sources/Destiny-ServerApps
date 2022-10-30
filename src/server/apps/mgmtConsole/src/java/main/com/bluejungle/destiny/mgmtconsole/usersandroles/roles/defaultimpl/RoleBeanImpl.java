/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.mgmtconsole.usersandroles.roles.ApplicationResourceBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean;
import com.bluejungle.destiny.services.policy.types.Component;
import com.bluejungle.destiny.services.policy.types.DMSRoleData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;

/**
 * Default implementation of the IInternalRole interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/RoleBeanImpl.java#2 $
 */

class RoleBeanImpl implements IInternalRoleBean {
    
    private final SubjectDTO roleSubject;
    private final DMSRoleData roleData;
    
    // sort by the display name, case-insensitive
    private final SortedSet<ApplicationResourceBean> applications;
    
    // look up map from internal name to bean
    private final Map<String, ApplicationResourceBean> nameToAppMap;
    
    // look up map from id to bean
    private final Map<String, ApplicationResourceBean> idToAppMap;
    
    private final IInternalPolicyAuthorComponentAccessBean policyAuthroComponentAccessBean;
    private DataModel defaultAccessAssignmentDataModel;        

    /**
     * Create an instance of RoleBeanImpl
     * 
     * @param roleSubject
     * @param roleData
     * @param allRoles
     */
    public RoleBeanImpl(SubjectDTO roleSubject, DMSRoleData roleData, SubjectDTO[] allRoles) {
        // FIX ME - Refactor into methods
        if (roleSubject == null) {
            throw new NullPointerException("roleSubject cannot be null.");
        }

        if (roleData == null) {
            throw new NullPointerException("roleData cannot be null.");
        }

        if (allRoles == null) {
            throw new NullPointerException("allRoles cannot be null.");
        }

        this.roleSubject = roleSubject;
        this.roleData = roleData;
        
        String[] allAppNames = this.roleData.getAllApps();
        if (allAppNames == null) {
            allAppNames = new String[] {};
        }
        
        String[] allowApps = this.roleData.getAllowApps();
        Set<String> allowAppsSet = new HashSet<String>();
        if (allowApps != null) {
            Collections.addAll(allowAppsSet, allowApps);
        }
        
        applications = new TreeSet<ApplicationResourceBean>(
                new Comparator<ApplicationResourceBean>() {
                    @Override
                    public int compare(ApplicationResourceBean o1, ApplicationResourceBean o2) {
                        return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
                    }
        });
        
        nameToAppMap = new HashMap<String, ApplicationResourceBean>(allAppNames.length);
        idToAppMap = new HashMap<String, ApplicationResourceBean>(allAppNames.length);
        
        
        for (String appName : allAppNames) {
            ApplicationResourceBean appBean = new ApplicationResourceBean(appName, allowAppsSet.contains(appName));
            applications.add(appBean);
            nameToAppMap.put(appName, appBean);
            idToAppMap.put(appBean.getId(), appBean);
        }
        

        Component[] accessiblePolicyAuthorComponents = this.roleData.getComponents();
        if (accessiblePolicyAuthorComponents == null) {
            accessiblePolicyAuthorComponents = new Component[] {};
        }

        this.policyAuthroComponentAccessBean = new PolicyAuthorComponentAccessBeanImpl(accessiblePolicyAuthorComponents);        
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRoleBean#getRoleId()
     */
    public long getRoleId() {
        return this.roleSubject.getId().getID().longValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRole#getRoleTitle()
     */
    public String getRoleTitle() {
        return roleSubject.getName();
    }
    
    @Override
    public Collection<ApplicationResourceBean> getAllResource() {
        return applications;
    }
    
    @Override
    public ApplicationResourceBean getResourceByName(String name) {
        return nameToAppMap.get(name);
    }
    
    @Override
    public ApplicationResourceBean getResourceById(String id) {
        return idToAppMap.get(id);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRole#getRolePolicyAuthorComponentAccess()
     */
    public IPolicyAuthorComponentAccessBean getRolePolicyAuthorComponentAccess() {
        return this.policyAuthroComponentAccessBean;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRole#getDefaultObjectSecurityAssignments()
     */
    public DataModel getDefaultObjectSecurityAssignments() {
        return this.defaultAccessAssignmentDataModel;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IInternalRoleBean#getWrappedDMSRoleData()
     */
    public DMSRoleData getWrappedDMSRoleData() {
        Collection<String> accessibleAppResourcesSet = new ArrayList<String>(applications.size());
        for (ApplicationResourceBean a : applications) {
            if (a.isAccessible()) {
                accessibleAppResourcesSet.add(a.getInternalName());
            }
        }
        this.roleData.setAllowApps(accessibleAppResourcesSet.toArray(new String[accessibleAppResourcesSet.size()]));
        // I don't need to set the all app because the data is not used on persist.
        // this.roleData.setAllApps(  );
        this.roleData.setComponents(this.policyAuthroComponentAccessBean.getAccessibleComponents());
                
        return this.roleData;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IInternalRoleBean#getWrappedSubjectDTO()
     */
    public SubjectDTO getWrappedSubjectDTO() {
        return this.roleSubject;
    }
    
}
