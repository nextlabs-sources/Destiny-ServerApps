package com.nextlabs.destiny.inquirycenter.report;

import java.util.Collection;

import com.bluejungle.destiny.inquirycenter.report.IReportPageBean;
import com.bluejungle.destiny.webui.framework.faces.IResetableBean;
import com.nextlabs.destiny.inquirycenter.customapps.CustomAppJO;


public interface ICustomReportPageBean extends IReportPageBean , IResetableBean {
    /*
     * ISO 8601/SQL standard, for example 2010-02-25 13:47:52-0800
     */
    String DATE_FORMAT = "yyyy-MM-dd hh:mm:ssZ";

    /**
     * These parameter names are always passed to any custom report to run.
     * These are also 'reserved' parameters and should not be used by the report
     * designer.
     */
    String BEGIN_DATE_PARAM_NAME = "BeginDate";
    
    String END_DATE_PARAM_NAME = "EndDate";
    
    final int MAX_DISPLAYABLE_CHARS = 100;

    void setCustomReportApps(Collection<CustomAppJO> customApps);
    
    Collection<CustomAppJO>  getCustomReportApps();
    
    void setCurrentCustomReportApp(CustomAppJO currentApp);
    
    CustomAppJO getCurrentCustomReportApp();
}
