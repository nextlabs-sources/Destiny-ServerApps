/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 29, 2016
 *
 */
package com.nextlabs.destiny.console.dao;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nextlabs.destiny.console.enums.DateTimeUnit;

/**
 *
 * DAO interface for Dashboard reports
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DashboardDataDao {

    /**
     * Get Enrolled element summary.
     * 
     * @return {@link JSONObject}
     */
    JSONArray getEnrolledElementSummary();

    /**
     * Get Enrollment status information
     * 
     * @return {@link JSONArray}
     */
    JSONArray getEnrollmentStatusInfo();

    /**
     * Get registered ICEnet configurations
     * 
     * @return {@link JSONArray}
     */
    JSONArray getICENetConfiguraions();

    /**
     * Get registered PDP configurations
     * 
     * @return {@link JSONArray}
     */
    JSONArray getPDPConfiguraions();

    /**
     * Get PDP configurations
     * 
     * @return {@link Map<Long, JSONObject>}
     */
    Map<Long, JSONObject> getPDPThroughputDetails(Long fromDate, Long toDate,
            DateTimeUnit groupBy);

    /**
     * Generated alert summary
     * 
     * @param fromDate
     * @param toDate
     * @param groupBy
     * @return {@link Map<Long, JSONObject>}
     */
    Map<Long, JSONArray> generatedAlertSummary(Long fromDate, Long toDate,
            DateTimeUnit groupBy);

    /**
     * Get activities by user
     * 
     * @param fromDate
     * @param toDate
     * @param decision
     * @param pageSize
     * @return {@link JSONArray}
     */
    JSONArray getActivitySummaryByUser(Long fromDate, Long toDate,
            String decision, int pageSize);

    /**
     * Get activities by resource
     * 
     * @param fromDate
     * @param toDate
     * @param pageSize
     * @return {@link JSONArray}
     */
    JSONArray getActivitySummaryByResource(Long fromDate, Long toDate,
            int pageSize);

    /**
     * Get activities by policy
     * 
     * @param fromDate
     * @param toDate
     * @param pageSize
     * @return {@link JSONArray}
     */
    JSONArray getActivitySummaryByPolicy(Long fromDate, Long toDate,
            int pageSize);

    /**
     * Get not matching policies for the given time period
     * 
     * @param fromDate
     * @param toDate
     * @param pageSize
     * @return {@link JSONArray}
     */
    JSONArray getNotMatchingPolicies(Long fromDate, Long toDate, int pageSize);

    Map<Long, JSONObject> rpaLogDataSummary(Long fromDate, Long toDate,
            String decision);

    JSONArray registeredAgents();

    JSONArray dictionaryEnrollments();

    JSONArray createdMonitors();

    /**
     * Policy archive Log table row count
     * 
     * @return row count
     */
    Long getArchiveLogRowCount();

    /**
     * Policy activity Log table row count
     * 
     * @return row count
     */
    Long getActivityLogRowCount();
    
    /**
     * Policy activity Log table row count for givne period
     * 
     * @return row count
     */
    Long getActivityLogRowCountForGivenPeriod(Long fromDay, Long toDay);

}
