/*
 * Created on Apr 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import java.util.Calendar;

import com.bluejungle.destiny.container.shared.inquirymgr.IInquiry;
import com.bluejungle.destiny.container.shared.inquirymgr.IReport;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportExecutionMgr;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportMgr;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportResultReader;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportTimePeriod;
import com.bluejungle.destiny.container.shared.inquirymgr.InquiryTargetDataType;
import com.bluejungle.destiny.container.shared.inquirymgr.InvalidReportArgumentException;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.ReportExecutionMgrCountOnlyImpl;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.ReportMgrImpl;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticSet;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatisticSet;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.TimedPullStatisticProvider;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.framework.datastore.hibernate.exceptions.DataSourceException;

/**
 * LogCountStaticProvider is a concrete implementation of
 * ILogCountStatisticProvider
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/LogCountStatisticsProvider.java#1 $
 */
public class LogCountStatisticsProvider extends TimedPullStatisticProvider implements ILogCountStatisticsProvider {

    private static final String REPORT_MANAGER_COMP_NAME = "ReportManagerComp";
    private static final String REPORT_EXECUTION_MANAGER_COMP_NAME = "ReportExecutionManagerComp";
    private IReportMgr reportManager;
    private IReportExecutionMgr reportExecutionManager;

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.statistic.TimedPullStatisticProvider#pullStatistic()
     */
    public IStatisticSet pullStatistic() {
        SimpleStatisticSet setToReturn = new SimpleStatisticSet();

        IStatistic trackingActivityLogCountStat = getTrackingActivityLogCount();
        setToReturn.setStatistic(TRACKING_ACTIVITY_LOG_COUNT_STAT_KEY, trackingActivityLogCountStat);

        IStatistic policyActivityLogCountStat = getPolicyActivityLogCount();
        setToReturn.setStatistic(POLICY_ACTIVITY_LOG_COUNT_STAT_KEY, policyActivityLogCountStat);

        return setToReturn;
    }

    /**
     * Retrieve the tracking activity log count
     * 
     * @return the tracking activity log count
     */
    private IStatistic getTrackingActivityLogCount() {
        return getActivityLogCount(InquiryTargetDataType.ACTIVITY);
    }

    /**
     * Retrieve the tracking activity log count
     * 
     * @return the tracking activity log count
     */
    private IStatistic getPolicyActivityLogCount() {
        return getActivityLogCount(InquiryTargetDataType.POLICY);
    }

    /**
     * Retrieve the activity log count of the specified type
     * 
     * @param targetDataType
     *            the target log type
     * @return the activity log count of the specified type
     */
    private IStatistic getActivityLogCount(InquiryTargetDataType targetDataType) {
        IReportMgr reportManager = getReportMgr();

        IReport report = reportManager.createReport();

        IReportTimePeriod timePeriod = report.getTimePeriod();
        Calendar beginTime = Calendar.getInstance();
        beginTime.add(Calendar.DATE, -1);
        timePeriod.setBeginDate(beginTime);
        Calendar endTime = Calendar.getInstance();
        timePeriod.setEndDate(endTime);

        IInquiry inquiry = report.getInquiry();
        inquiry.setTargetData(targetDataType);

        IReportExecutionMgr reportExecutionManager = getReportExecutionMgr();
        IReportResultReader reader = null;
        IStatistic activityLogCountStat = null;
        try {
            reader = reportExecutionManager.executeReport(report);
            Long rowCount = reader.getStatistics().getTotalRowCount();
            activityLogCountStat = new SimpleStatistic(rowCount, endTime);
        } catch (DataSourceException exception) {
            getLog().warn("Failed to activity log statistic", exception);
            activityLogCountStat = getErrorStatistic();
        } catch (InvalidReportArgumentException exception) {
            getLog().warn("Failed to activity log statistic", exception);
            activityLogCountStat = getErrorStatistic();
        }

        return activityLogCountStat;
    }

    /**
     * Retrieve a statistic representing an error condition
     * 
     * @return a statistic representing an error condition
     */
    private SimpleStatistic getErrorStatistic() {
        return new SimpleStatistic(new Long(-1), Calendar.getInstance());
    }

    /**
     * Returns an instance of the report manager
     * 
     * @return an instance of the report manager
     */
    private IReportMgr getReportMgr() {
        if (this.reportManager == null) {
            IComponentManager compMgr = getManager();
            ComponentInfo<IReportMgr> compInfo = new ComponentInfo<IReportMgr>(
                    REPORT_MANAGER_COMP_NAME, 
                    ReportMgrImpl.class, 
                    IReportMgr.class, 
                    LifestyleType.SINGLETON_TYPE);
            this.reportManager = compMgr.getComponent(compInfo);
        }

        return this.reportManager;
    }

    /**
     * Returns an instance of the report execution manager
     * 
     * @return an instance of the report execution manager
     */
    private IReportExecutionMgr getReportExecutionMgr() {
        if (this.reportExecutionManager == null) {
            IComponentManager compMgr = getManager();
            HashMapConfiguration config = new HashMapConfiguration();
            config.setProperty(IReportExecutionMgr.DATA_SOURCE_CONFIG_PARAM, getActivityDateSource());
            ComponentInfo<IReportExecutionMgr> compInfo = 
                new ComponentInfo<IReportExecutionMgr>(
                    REPORT_EXECUTION_MANAGER_COMP_NAME, 
                    ReportExecutionMgrCountOnlyImpl.class, 
                    IReportExecutionMgr.class, 
                    LifestyleType.SINGLETON_TYPE, 
                    config);
            this.reportExecutionManager = compMgr.getComponent(compInfo);
        }

        return this.reportExecutionManager;
    }

    /**
     * Retrieve the Destiny Activity Datasource
     * 
     * @return the Destiny Activity Datasource
     */
    private IHibernateRepository getActivityDateSource() {
        IComponentManager componentManager = getManager();
        IHibernateRepository dataSource = (IHibernateRepository) componentManager.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());

        if (dataSource == null) {
            throw new IllegalStateException("Required datasource not initialized for LogCountStatisticProvider.");
        }

        return dataSource;
    }

}
