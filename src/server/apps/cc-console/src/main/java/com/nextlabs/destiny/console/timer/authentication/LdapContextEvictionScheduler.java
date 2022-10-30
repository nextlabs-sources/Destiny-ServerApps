/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 9 Sep 2016
 *
 */
package com.nextlabs.destiny.console.timer.authentication;

import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.console.dto.authentication.activedirectory.LdapTimedContext;

/**
 *
 * Scheduler job that runs every 30 min to evict stale LDAP Contexts
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Component
public class LdapContextEvictionScheduler {

	private static final Logger log = LoggerFactory.getLogger(LdapContextEvictionScheduler.class);

	

}
