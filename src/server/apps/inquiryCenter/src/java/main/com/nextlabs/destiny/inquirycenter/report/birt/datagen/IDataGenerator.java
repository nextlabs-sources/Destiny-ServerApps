/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

import java.sql.Timestamp;
import java.util.List;

import com.nextlabs.destiny.inquirycenter.report.ReportCriteriaJSONModel;
import com.nextlabs.destiny.inquirycenter.report.ReportResultData;
import com.nextlabs.report.datagen.ResultData;

/**
 * This is the interface that is used for generating all the data that is sent
 * back to the BIRT reports. Reports are configured with Scripted Data Sets
 * that call implementations of this interface to obtain the data.
 * 
 * All the database specific methods throw Exceptions - none of them catch
 * it and log it. It is the responsibility of the caller to catch the exceptions
 * and log appropriate messages and rethrow the exceptions as appropriate.
 * 
 * @author ssen
 */
public interface IDataGenerator {

    void setBeginDate(Timestamp beginDate);
    Timestamp getBeginDate();
    void setEndDate(Timestamp endDate);
    Timestamp getEndDate();
    void setEnforcement(String enforcement);
    String getEnforcement();
    void setEventLevel(String eventLevel);
    String getEventLevel();
    void setResource(String resource);
    String getResource();
    void setPolicy(String policy);
    String getPolicy();
    void setAction(String action) ;
    String getAction();
    void setUserName(String userName);
    String getUserName();
    void setJSONData(String sData);
    String getJSONData();
    void setGroupByDimension(String groupByDimension);
    void setTimeDimension(String timeDimension);
    void setIdForDetailsLog(String idForDetailsLog);
    
    void setReportCriteriaJSONModel(ReportCriteriaJSONModel jsonModel);
    ReportCriteriaJSONModel getReportCriteriaJSONModel();
    
    /**
     * Must be called by the callee to cleanup the connections. This should
     * be called after one or a set of the data generation methods have been
     * called and the instance will not be used any more.  
     */
    void cleanup() throws Exception;
    
    List<GroupByData> generatePAGroupByData() throws Exception;
    List<PADetailsTableData> generatePADetailsTableData() throws Exception;
    ReportResultData generatePADetailsTableDataV2() throws Exception;
    int generatePADetailsTableDataTotalV2() throws Exception;
    PALogDetailsData generatePALogDetailsData() throws Exception;
    PALogDetailsData generatePALogDetailsDataV2() throws Exception;
    
    List<GroupByData> generateTRGroupByData() throws Exception;
    List<DetailsTableData> generateTRDetailsTableData() throws Exception;
    TRLogDetailsData generateTRLogDetailsData() throws Exception;

}
