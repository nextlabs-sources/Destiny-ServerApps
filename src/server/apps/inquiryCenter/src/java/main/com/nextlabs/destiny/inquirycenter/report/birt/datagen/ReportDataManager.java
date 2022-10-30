/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.report.ReportCriteriaJSONModel;
import com.nextlabs.destiny.inquirycenter.report.ReportResultData;

import net.sf.hibernate.Session;

/**
 * This is the interface between BIRT reports and the data. All the BIRT reports
 * that use scripted data source invokes this class to get the appropriate data.
 * 
 * Any changes to the APIs here need corresponding changes to each of the BIRT 
 * report design files.
 * 
 * @author ssen
 */
public class ReportDataManager {

    private Log log = LogFactory.getLog(ReportDataManager.class.getName());

    public ReportDataManager() {
    }

    private Session getSession()
    	throws Exception {
    	try {
    		return SharedLib.getActivityDataSource().getSession();
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Could not obtain session", ex);
            }
            throw ex;
        }
    }
    
    private IDataGenerator getDataGenerator(Session session)
    	throws Exception {
    	try {
    		return DataGeneratorFactory.getInstance().getDataGenerator(session);
    	} catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Could not generate dataGen", ex);
            }
            throw ex;
        }
    }
    
    private void cleanup(Session session, IDataGenerator dataGen) throws Exception {
        if (dataGen != null) {
            dataGen.cleanup();
        }
        if (session != null) {
            try {
                session.close();
                session = null;
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Could not close session", ex);
                }
                throw ex;
            }
        }
    }
    
    //TODO : Not used by the code : PA_Single_GroupBy.rptdesign,    PA_Single_GroupByResource.rptdesign,    PA_Single_GroupByTime.rptdesign (2 matches)
    public List<GroupByData> getPAGroupByData(
            String groupByDimension, String beginDate, String endDate,
            String enforcement, String eventLevel, String resource, String policy,
            String action, String userName) throws Exception {
        
        List<GroupByData> data = null;
        Session session = null;
        IDataGenerator dataGen = null;
        
        try {
            session = getSession();
            dataGen = getDataGenerator(session);
            dataGen.setGroupByDimension(groupByDimension);
            dataGen.setBeginDate(Timestamp.valueOf(beginDate));
            dataGen.setEndDate(Timestamp.valueOf(endDate));
            dataGen.setEnforcement(enforcement);
            dataGen.setEventLevel(eventLevel);
            dataGen.setResource(resource);
            dataGen.setPolicy(policy);
            dataGen.setAction(action);
            dataGen.setUserName(userName);

            data = dataGen.generatePAGroupByData();
        } catch (Exception ex) {
            throw ex;
        } finally {
            cleanup(session, dataGen);
        }
        return data;
    }

    //TODO : PA_Details_Table.rptdesign
    public List<PADetailsTableData> getPADetailsTableData(
            String beginDate, String endDate,
            String enforcement, String eventLevel, String resource, String policy,
            String action, String userName, String timeDimension, String groupByDimension) throws Exception {

        List<PADetailsTableData> data = null;
        Session session = null;
        IDataGenerator dataGen = null;
        
        try {
            session = getSession();
            dataGen = getDataGenerator(session);
            dataGen.setBeginDate(Timestamp.valueOf(beginDate));
            dataGen.setEndDate(Timestamp.valueOf(endDate));
            dataGen.setEnforcement(enforcement);
            dataGen.setEventLevel(eventLevel);
            dataGen.setResource(resource);
            dataGen.setPolicy(policy);
            dataGen.setAction(action);
            dataGen.setUserName(userName);
            dataGen.setTimeDimension(timeDimension);
            dataGen.setGroupByDimension(groupByDimension);
            data = dataGen.generatePADetailsTableData();
        } catch (Exception ex) {
            throw ex;
        } finally {
            cleanup(session, dataGen);
        }
        return data;
    }
    
	public ReportResultData getPADetailsTableDataV2(String jSonData) throws Exception {
		ReportResultData data = null;
		Session session = null;
		IDataGenerator dataGen = null;
		
		try {
			log.info("getPADetailsTableDataV2 -->> [Started]");
			session = getSession();
			dataGen = getDataGenerator(session);
			
			ReportCriteriaJSONModel jModel = new ReportCriteriaJSONModel(jSonData);
			dataGen.setBeginDate(Timestamp.valueOf(jModel.getGeneralValue("start_date")));
			dataGen.setEndDate(Timestamp.valueOf(jModel.getGeneralValue("end_date")));
			
			/*
			 * if beginDate is greater than endDate no need to run query 
			 */
			if (dataGen.getBeginDate().getTime() > dataGen.getEndDate().getTime())
			{
				return data;
			}
			
			dataGen.setEventLevel(jModel.getGeneralLogLevelField().getValue());
			dataGen.setEnforcement(jModel.getGeneralDesicionField().getValue());
		
			dataGen.setReportCriteriaJSONModel(jModel);
			data = dataGen.generatePADetailsTableDataV2();
			
			log.info("getPADetailsTableDataV2 -->> [Completed]");
		} catch (Exception e) {
			log.info(e);
			throw e;
		} finally {
			cleanup(session, dataGen);
		}
		return data;
	}
	
	   public int getPADetailsTableDataTotalV2(String jSonData) throws Exception {
		   Session session = null;
		   IDataGenerator dataGen = null;
		   
	        try {
	            log.info("getPADetailsTableDataV2 -->> [Started]");
	            session = getSession();
	            dataGen = getDataGenerator(session);
	            
	            ReportCriteriaJSONModel jModel = new ReportCriteriaJSONModel(jSonData);
	            dataGen.setBeginDate(Timestamp.valueOf(jModel.getGeneralValue("start_date")));
	            dataGen.setEndDate(Timestamp.valueOf(jModel.getGeneralValue("end_date")));
	            
	            /*
	             * if beginDate is greater than endDate no need to run query 
	             */
	            if (dataGen.getBeginDate().getTime() > dataGen.getEndDate().getTime())
	            {
	                return 0;
	            }
	            
	            dataGen.setEventLevel(jModel.getGeneralLogLevelField().getValue());
	            dataGen.setEnforcement(jModel.getGeneralDesicionField().getValue());
	        
	            dataGen.setReportCriteriaJSONModel(jModel);
	            return dataGen.generatePADetailsTableDataTotalV2();
	            
	        } catch (Exception e) {
	            log.info(e);
	            throw e;
	        } finally {
	            log.info("getPADetailsTableDataV2 -->> [Completed]");
	            cleanup(session, dataGen);
	        }
	    }

    public PALogDetailsData getPALogDetailsData(String paLogId) throws Exception  {
        PALogDetailsData data = null;
        Session session = null;
        IDataGenerator dataGen = null;
        
        try {
            session = getSession();
            dataGen = getDataGenerator(session);
            dataGen.setIdForDetailsLog(paLogId);
            data = dataGen.generatePALogDetailsData();
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Severe error while generating Policy Log details data", ex);
            }
            throw ex;
        } finally {
            cleanup(session, dataGen);
        }
        return data;
    }
    
    public PALogDetailsData getPALogDetailsDataV2(String paLogId) throws Exception  {
        PALogDetailsData data = null;
        Session session = null;
        IDataGenerator dataGen = null;
        
        try {
            session = getSession();
            dataGen = getDataGenerator(session);
            dataGen.setIdForDetailsLog(paLogId);
            data = dataGen.generatePALogDetailsDataV2();
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Severe error while generating Policy Log details data", ex);
            }
            throw ex;
        } finally {
            cleanup(session, dataGen);
        }
        return data;
    }
    
    // TODO: TR_Single_GroupBy.rptdesign,    TR_Single_GroupByResource.rptdesign,    TR_Single_GroupByTime.rptdesign (2 matches)
    public List<GroupByData> getTRGroupByData(
            String groupByDimension, String beginDate, String endDate,
            String eventLevel, String resource,
            String action, String userName) throws Exception {

        List<GroupByData> data = null;
        Session session = null;
        IDataGenerator dataGen = null;
        
        try {
            session = getSession();
            dataGen = getDataGenerator(session);
            dataGen.setGroupByDimension(groupByDimension);
            dataGen.setBeginDate(Timestamp.valueOf(beginDate));
            dataGen.setEndDate(Timestamp.valueOf(endDate));
            dataGen.setEventLevel(eventLevel);
            dataGen.setResource(resource);
            dataGen.setAction(action);
            dataGen.setUserName(userName);
            data = dataGen.generateTRGroupByData();
        } catch (Exception ex) {
            throw ex;
        } finally {
            cleanup(session, dataGen);
        }
        return data;
    }
    
    // TODO : TR_Details_Table.rptdesign
    public List<DetailsTableData> getTRDetailsTableData(
            String beginDate, String endDate,
            String eventLevel, String resource,
            String action, String userName, String timeDimension, String groupByDimension) throws Exception {
        
        List<DetailsTableData> data = null;
        Session session = null;
        IDataGenerator dataGen = null;
        
        try {
            session = getSession();
            dataGen = getDataGenerator(session);
            dataGen.setBeginDate(Timestamp.valueOf(beginDate));
            dataGen.setEndDate(Timestamp.valueOf(endDate));
            dataGen.setEventLevel(eventLevel);
            dataGen.setResource(resource);
            dataGen.setAction(action);
            dataGen.setUserName(userName);
            dataGen.setTimeDimension(timeDimension);
            dataGen.setGroupByDimension(groupByDimension);
            data = dataGen.generateTRDetailsTableData();
         } catch (Exception ex) {
             throw ex;
         } finally {
             cleanup(session, dataGen);
         }
         return data;
    }
    
    public TRLogDetailsData getTRLogDetailsData(String taLogId) throws Exception  {
        TRLogDetailsData data = null;
        Session session = null;
        IDataGenerator dataGen = null;
        
        try {
            session = getSession();
            dataGen = getDataGenerator(session);
            dataGen.setIdForDetailsLog(taLogId);
            data = dataGen.generateTRLogDetailsData();
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Severe error while generating Policy Log details data", ex);
            }
        } finally {
            cleanup(session, dataGen);
        }
        return data;
    }
}
