/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 30, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import org.json.simple.JSONObject;

import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 * Dashboard service to produce data for dashboard components.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DashboardService {

    /**
     * Get system details, such as OS name, OS version, application version and
     * license information
     * 
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getSystemDetails() throws ConsoleException;

    /**
     * Get enrollment information, such as No of enrollments, last sync status,
     * last sync time
     * 
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getEnrollmentDetails() throws ConsoleException;

    /**
     * Get system configuration details. Such as No of ICEnets, PDPs and their
     * heart beats
     * 
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getSystemConfigDetails() throws ConsoleException;

    /**
     * Get PDP throughput details
     * 
     * @param fromDate
     * @param toDate
     * @param groupBy
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getPDPThroughputDetails(Long fromDate, Long toDate,
            String groupBy) throws ConsoleException;

    /**
     * Get Alert summary details
     * 
     * @param fromDate
     * @param toDate
     * @param groupBy
     *            group by time unit
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getAlertSummary(Long fromDate, Long toDate, String groupBy)
            throws ConsoleException;

    /**
     * Get activities by user
     * 
     * @param fromDate
     * @param toDate
     * @param decision
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getActivitySummaryByUser(Long fromDate, Long toDate,
            String decision, int pageSize) throws ConsoleException;

    /**
     * Get activities summary by resource
     * 
     * @param fromDate
     * @param toDate
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getActivitySummaryByResource(Long fromDate, Long toDate,
            int pageSize) throws ConsoleException;

    /**
     * Get activities summary by policies
     * 
     * @param fromDate
     * @param toDate
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getActivitySummaryByPolicy(Long fromDate, Long toDate,
            int pageSize) throws ConsoleException;

    /**
     * Get list of not matching policies for the given time period
     * 
     * @param fromDate
     * @param toDate
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getNotMatchingPolicies(Long fromDate, Long toDate, int pageSize)
            throws ConsoleException;

    /**
     * Policy summary details by status
     * 
     * 
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject policySummaryByStatus() throws ConsoleException;

    /**
     * Policy summary details by status for given period
     * 
     * @param fromDate
     *            fromdate
     * @param toDate
     *            toDate
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject policySummaryByStatus(long fromDate, long toDate)
            throws ConsoleException;

    /**
     * Policy summary by tags
     * 
     * @param dataSize
     *            result data size
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject policySummaryByTags(int dataSize) throws ConsoleException;

    /**
     * Get activity stream
     * 
     * @param dataSize
     *            result data size
     * @return {@link JSONObject}
     * @throws ConsoleException
     *             throws at any error
     */
    JSONObject getActivityStream(int dataSize) throws ConsoleException;
}
