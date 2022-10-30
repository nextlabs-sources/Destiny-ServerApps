/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 8 Sep 2016
 *
 */
package com.nextlabs.destiny.console.dto.authentication.activedirectory;

import javax.naming.ldap.LdapContext;

/**
 *
 *
 *
 * @author aishwarya
 * @since   8.0
 *
 */
public class LdapTimedContext {
	
	private LdapContext ldapCtx;
	private long lastAccessTime;
	
	public LdapContext getLdapCtx() {
		return ldapCtx;
	}
	public void setLdapCtx(LdapContext ldapCtx) {
		this.ldapCtx = ldapCtx;
	}
	/**
	 * @return the lastAccessTime
	 */
	public long getLastAccessTime() {
		return lastAccessTime;
	}
	/**
	 * @param lastAccessTime the lastAccessTime to set
	 */
	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

}
