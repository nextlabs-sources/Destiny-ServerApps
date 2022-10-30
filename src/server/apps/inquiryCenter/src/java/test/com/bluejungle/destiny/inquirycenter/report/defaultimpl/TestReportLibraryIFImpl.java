/*
 * Created on Jun 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;

import com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF;
import com.bluejungle.destiny.types.basic_faults.v1.AccessDeniedFault;
import com.bluejungle.destiny.types.basic_faults.v1.PersistenceFault;
import com.bluejungle.destiny.types.basic_faults.v1.ServiceNotReadyFault;
import com.bluejungle.destiny.types.basic_faults.v1.UnknownEntryFault;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportList;
import com.bluejungle.destiny.types.report.v1.ReportQuerySpec;

/**
 * Test utility service for testing report display layer classes
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/TestReportLibraryIFImpl.java#1 $
 */

class TestReportLibraryIFImpl implements ReportLibraryIF {

    private Report[] Reports;

    private Report lastReportInserted = null;
    private BigInteger lastReportDeleted = null;
    private Report lastReportUpdated = null;

    /**
     * Create an instance of TestReportLibraryIFImpl
     * 
     * @param test
     */
    TestReportLibraryIFImpl(Report[] Reports) {
        this.Reports = Reports;
    }

    /**
     * @see com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF#insertReport(com.bluejungle.destiny.types.report.v1.Report,
     *      com.bluejungle.destiny.types.report.v1.InsertReportInfo)
     */
    public Report insertReport(Report newReport) throws RemoteException, AccessDeniedFault, ServiceNotReadyFault, PersistenceFault {
        this.lastReportInserted = newReport;

        Report insertedReport = new Report();
        insertedReport.setId(new BigInteger("444"));
        insertedReport.setDescription(newReport.getDescription());
        insertedReport.setShared(newReport.isShared());
        insertedReport.setTitle(newReport.getTitle());
        insertedReport.setOwned(true);

        Report[] newReports = new Report[this.Reports.length + 1];
        System.arraycopy(this.Reports, 0, newReports, 0, this.Reports.length);
        newReports[this.Reports.length] = insertedReport;
        this.Reports = newReports;

        return insertedReport;
    }

    /**
     * @see com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF#deleteReport(java.math.BigInteger)
     */
    public void deleteReport(BigInteger reportId) throws RemoteException, UnknownEntryFault, AccessDeniedFault, ServiceNotReadyFault, PersistenceFault {
        this.lastReportDeleted = reportId;
    }

    /**
     * @see com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF#getReportById(java.math.BigInteger)
     */
    public Report getReportById(BigInteger reportId) throws RemoteException, UnknownEntryFault, AccessDeniedFault, ServiceNotReadyFault {
        return null;
    }

    /**
     * @see com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF#getReports(com.bluejungle.destiny.types.report.v1.ReportQuerySpec)
     */
    public ReportList getReports(ReportQuerySpec querySpec) throws RemoteException, AccessDeniedFault, ServiceNotReadyFault {
        Report[] clonedReports = new Report[this.Reports.length];
        for (int i = 0; i < clonedReports.length; i++) {
            clonedReports[i] = cloneReport(this.Reports[i]);
        }

        return new ReportList(clonedReports);
    }

    /**
     * @see com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF#updateReport(com.bluejungle.destiny.types.report.v1.Report)
     */
    public Report updateReport(Report report) throws RemoteException, UnknownEntryFault, AccessDeniedFault, ServiceNotReadyFault, PersistenceFault {
        this.lastReportUpdated = report;
        return report;
    }

    /**
     * Retrieve the lastReportDeleted.
     * 
     * @return the lastReportDeleted.
     */
    public BigInteger getLastReportDeleted() {
        return this.lastReportDeleted;
    }

    /**
     * Retrieve the lastReportInserted.
     * 
     * @return the lastReportInserted.
     */
    public Report getLastReportInserted() {
        return this.lastReportInserted;
    }

    /**
     * Retrieve the lastReportUpdated.
     * 
     * @return the lastReportUpdated.
     */
    public Report getLastReportUpdated() {
        return this.lastReportUpdated;
    }

    /**
     * Retrieve the Reports.
     * 
     * @return the Reports.
     */
    public Report[] getReports() {
        return this.Reports;
    }

    /**
     * Clone a saved report
     * 
     * @param report
     *            the report to clone
     * @return a cloned saved report
     */
    private Report cloneReport(Report report) {
        Report clonedReport = new Report();
        clonedReport.setId(report.getId());
        clonedReport.setDescription(report.getDescription());
        clonedReport.setShared(report.isShared());
        clonedReport.setTitle(report.getTitle());
        clonedReport.setOwned(report.isOwned());
        return clonedReport;
    }
}