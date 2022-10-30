/*
 * Created on May 18, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl;

import java.util.HashSet;
import java.util.Set;

import com.bluejungle.destiny.services.policy.types.Component;

/**
 * Default implementation of
 * 
 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IInternalPolicyAuthorComponentAccessBean
 *      interface
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main
 *          /com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/
 *          PolicyAuthorComponentAccessBeanImpl.java#5 $
 */

class PolicyAuthorComponentAccessBeanImpl implements
		IInternalPolicyAuthorComponentAccessBean {
	private Set accessibleComponents;

	/**
	 * Create an instance of PolicyAuthorComponentAccessBeanImpl
	 * 
	 * @param components
	 */
	public PolicyAuthorComponentAccessBeanImpl(Component[] components) {
		if (components == null) {
			throw new NullPointerException("components cannot be null.");
		}

		this.accessibleComponents = new HashSet();
		for (int i = 0; i < components.length; i++) {
			this.accessibleComponents.add(components[i]);
		}
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isPolicyEditViewAccessibleToRole()
	 */
	public boolean isPolicyEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.Policy);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setPolicyEditViewAccessibleToRole(boolean)
	 */
	public void setPolicyEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.Policy);
		} else {
			this.accessibleComponents.remove(Component.Policy);
		}
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isUserComponentEditViewAccessibleToRole()
	 */
	public boolean isUserComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.User);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setDeviceComponentEditViewAccessibleToRole(boolean)
	 */
	public void setDeviceComponentEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.Device);
		} else {
			this.accessibleComponents.remove(Component.Device);
		}
	}
	
	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isDeviceComponentEditViewAccessibleToRole()
	 */
	public boolean isDeviceComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.Device);
	}
	
	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setSAPComponentEditViewAccessibleToRole(boolean)
	 */
	public void setSAPComponentEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.SAP);
		} else {
			this.accessibleComponents.remove(Component.SAP);
		}
	}
	
	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isSAPComponentEditViewAccessibleToRole()
	 */
	public boolean isSAPComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.SAP);
	}
	
	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setEnoviaComponentEditViewAccessibleToRole(boolean)
	 */
	public void setEnoviaComponentEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.Enovia);
		} else {
			this.accessibleComponents.remove(Component.Enovia);
		}
	}
	
	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isEnoviaComponentEditViewAccessibleToRole()
	 */
	public boolean isEnoviaComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.Enovia);
	}
	

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setUserComponentEditViewAccessibleToRole(boolean)
	 */
	public void setUserComponentEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.User);
		} else {
			this.accessibleComponents.remove(Component.User);
		}
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isResourceComponentEditViewAccessibleToRole()
	 */
	public boolean isResourceComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.Resource);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isPortalComponentEditViewAccessibleToRole()
	 */
	public boolean isPortalComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.Portal);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setResourceComponentEditViewAccessibleToRole(boolean)
	 */
	public void setResourceComponentEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.Resource);
		} else {
			this.accessibleComponents.remove(Component.Resource);
		}
	}
	
	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setPortalComponentEditViewAccessibleToRole(boolean)
	 */
	public void setPortalComponentEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.Portal);
		} else {
			this.accessibleComponents.remove(Component.Portal);
		}
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isDesktopComponentEditViewAccessibleToRole()
	 */
	public boolean isDesktopComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.Desktop);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setDesktopComponentEditViewAccessibleToRole(boolean)
	 */
	public void setDesktopComponentEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.Desktop);
		} else {
			this.accessibleComponents.remove(Component.Desktop);
		}
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isApplicationComponentEditViewAccessibleToRole()
	 */
	public boolean isApplicationComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.Application);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setApplicationComponentEditViewAccessibleToRole(boolean)
	 */
	public void setApplicationComponentEditViewAccessibleToRole(
			boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.Application);
		} else {
			this.accessibleComponents.remove(Component.Application);
		}
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#isActionComponentEditViewAccessibleToRole()
	 */
	public boolean isActionComponentEditViewAccessibleToRole() {
		return this.accessibleComponents.contains(Component.Action);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IPolicyAuthorComponentAccessBean#setActionComponentEditViewAccessibleToRole(boolean)
	 */
	public void setActionComponentEditViewAccessibleToRole(boolean grantAccess) {
		if (grantAccess) {
			this.accessibleComponents.add(Component.Action);
		} else {
			this.accessibleComponents.remove(Component.Action);
		}
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl.IInternalPolicyAuthorComponentAccessBean#getAccessibleComponents()
	 */
	public Component[] getAccessibleComponents() {
		return (Component[]) this.accessibleComponents
				.toArray(new Component[this.accessibleComponents.size()]);
	}
}