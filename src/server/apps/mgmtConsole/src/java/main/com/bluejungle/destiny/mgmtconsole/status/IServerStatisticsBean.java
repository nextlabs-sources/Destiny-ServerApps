/*
 * Created on April 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status;

import java.util.Calendar;
import java.util.List;

/**
 * IServerStatisticBean is a java bean used by the display layer to retrieve
 * runtime statistics associated with the server. These statistics are used to
 * indicate the general health of the system
 * 
 * @author sgoldstein
 */
public interface IServerStatisticsBean {

    /**
     * Retrieve the number of agents registered in the system. 
     * 
     * @return the number of agents registered in the system
     */
    public List<IAgentCountBean> getNumAgents();

    /**
     * Retrieve the total number of agents registered in the system (# file
     * server agents + # of dektop agents)
     * 
     * @return the total number of agents registered in the system
     */
    public long getTotalNumAgents();

    /**
     * Retrieve the total number of policies deployed within the system
     * 
     * @return the total number of policies deployed within the system
     */
    public long getNumPolicies();

    /**
     * Retrieve the number of agents which have not sent heartbeats within the
     * last 24 hours
     * 
     * @return the number of agents which have not sent heartbeats within the
     *         last 24 hours
     */
    public long getNumDisconnectedAgentsInLastDay();

    /**
     * Retrieve the number of agents which haven't yet received the latest
     * policies
     * 
     * @return the number of agents which haven't yet received the latest
     *         policies
     */
    public long getNumAgentsWithOutOfDatePolicies();

    /**
     * Retrieve the number of heartbeats sent by agents within the last 24 hours
     * 
     * @return the number of heartbeats sent by agents within the last 24 hours
     */
    public long getNumHeartbeatsInLastDay();

    /**
     * Retrieve the number policy activity log entries made within the last 24
     * hours
     * 
     * @return the number policy activity log entries made within the last 24
     *         hours
     */
    public long getNumPolicyActivityLogEntriesInLastDay();

    /**
     * Retrieve the number tracking activity log entries made within the last 24
     * hours
     * 
     * @return the number tracking activity log entries made within the last 24
     *         hours
     */
    public long getNumTrackingActivityLogEntriesInLastDay();

    /**
     * Retrieve the total number of activity log entries which has been made in
     * the last 24 hours (policy activity log entries + tracking activity log
     * entries)
     * 
     * @return the total number of activity log entries which has been made in
     *         the last
     */
    public long getTotalActivityLogEntriesInLastDay();

    /**
     * Retrieve the time at which any of the statistics were last updated
     * 
     * @return the time at which any of the statistics were last updated
     */
    public Calendar getLastUpdatedTimestamp();
}
