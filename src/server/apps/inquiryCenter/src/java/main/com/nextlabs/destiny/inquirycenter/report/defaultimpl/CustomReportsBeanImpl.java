package com.nextlabs.destiny.inquirycenter.report.defaultimpl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.event.ActionEvent;

import com.bluejungle.destiny.types.basic.v1.Id;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportImpl;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.webui.framework.faces.ILoadable;
import com.nextlabs.destiny.container.shared.customapps.mapping.CustomReportJO;
import com.nextlabs.destiny.inquirycenter.SharedUtils;
import  com.nextlabs.destiny.inquirycenter.customapps.mapping.ReportParameterJO;
import com.nextlabs.destiny.inquirycenter.customapps.CustomAppJO;
import com.nextlabs.destiny.inquirycenter.report.ICustomReportPageBean;
import com.nextlabs.report.datagen.IReportDataManager;
import com.nextlabs.report.datagen.ReportDataManagerFactory;

/**
 * This is the backing bean for Custom Reports Page. This is responsible for 
 * serving the correct application reports based on the application selected. The
 * ReportNavigatorBean indirectly invokes this with the correct application information
 * when the user selects a specific application that is netiher the MyReports nor  
 * the SharepointReports.
 * 
 * @author ssen
 *
 */
public class CustomReportsBeanImpl
implements ICustomReportPageBean,  ILoadable {

    private static final Log LOG = LogFactory.getLog(
            CustomReportsBeanImpl.class.getName());

    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    private boolean reportResultsSelected = true;

    private boolean reportDefinitionSelected = false;

    private List<IReport> reportList = new ArrayList<IReport>();

    private Map<Long, CustomReportJO> reportMap = 
        new HashMap<Long, CustomReportJO>();
    private Map<Long, ReportParameterJO> paramMap = 
        new  HashMap<Long, ReportParameterJO>();

    private Map<String, String> invokeReportParams =  
        new HashMap<String, String>();

    private Date beginDate;

    private Date endDate;

    private Long selectedReportId = Long.valueOf(0);

    private Collection<CustomAppJO> customReportApps;

    private CustomAppJO currentCustomReportApp;

    private boolean loaded;

    private HtmlPanelGrid customParamPanel ;

    /**
     *  UI Strings obtained from the currently selected report app
     */
    private String reportPanelHeaderLabel;
    private String descriptionPanelHeaderLabel;
    private String parameterPanelHeaderPrefixLabel;

    public CustomAppJO getCurrentCustomReportApp() {
        return currentCustomReportApp;
    }

    public void setCurrentCustomReportApp(CustomAppJO currentApp) {
        if (currentApp == null)
            throw new IllegalArgumentException("Null Custom Report App supplied");

        if (this.currentCustomReportApp != null && 
                this.currentCustomReportApp.getCustomAppId() ==
                    currentApp.getCustomAppId()) { 
            // same app - no need to reset anything
            return;
        }
        reset();
        this.currentCustomReportApp = currentApp;
        load();
    }

    public Collection<CustomAppJO> getCustomReportApps() {
        return customReportApps;
    }

    public void setCustomReportApps(Collection<CustomAppJO> customReportApps) {
        this.customReportApps  = customReportApps;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * This is responsible for getting all the custom report application information
     */
    public void load() {
        if (!isLoaded()) {

            // list panel header label
            setReportPanelHeaderLabel(SharedUtils.limitStringWithEllipsis( 
                    currentCustomReportApp.getCustomReportUIJO().getListHeader(),  
                    ICustomReportPageBean.MAX_DISPLAYABLE_CHARS));

            // header label of the report description panel 
            setDescriptionPanelHeaderLabel(SharedUtils.limitStringWithEllipsis( 
                    currentCustomReportApp.getCustomReportUIJO().getDescriptionHeader(),  
                    ICustomReportPageBean.MAX_DISPLAYABLE_CHARS));

            // parameter panel header prefix
            setParameterPanelHeaderPrefixLabel(
                    currentCustomReportApp.getCustomReportUIJO().getParameterHeader());

            // parameters
            List<ReportParameterJO> curParamList= 
                currentCustomReportApp.getCustomReportUIJO().getReportParameters();

            // reportlist
            List<CustomReportJO> curReportList = 
                currentCustomReportApp.getPolicyApplicationJO().getCustomReports();
            int i = 0; 
            for (CustomReportJO thisReport : curReportList) {
                Report report = new Report();
                Id reportId = new Id();
                reportId.setId(i++);
                report.setId(reportId);
                report.setTitle(thisReport.getTitle());
                report.setDescription(thisReport.getDescription());
                IReport iReport = new ReportImpl(report);
                reportList.add(iReport);
                reportMap.put(reportId.getId(), thisReport);
                ReportParameterJO thisReportParams = null;
                for (ReportParameterJO reportParam : curParamList) {
                    if (reportParam.getRefReportTitle().equals(thisReport.getTitle())) {
                        thisReportParams = reportParam;
                        break;
                    }
                }
                if (thisReportParams != null) {
                    paramMap.put(reportId.getId(), thisReportParams);
                }
            }
            loaded = true;
        }
    }

    public void reset() {
        setReportDefinitionSelected(false);
        reportList.clear();
        reportMap.clear();
        paramMap.clear();
        invokeReportParams.clear();
        clearCustomParamPanel();
        loaded = false;
    }

    public void setReportResultsSelected(boolean reportResultsSelected) {
        this.reportResultsSelected = reportResultsSelected;        
    }

    public boolean isReportResultsSelected() {
        return reportResultsSelected;
    }

    public String getSelectedReportTitle() {
        return getSelectedReport().getTitle();
    }

    public String getSelectedReportDescription() {
        return getSelectedReport().getDescription();
    }

    public boolean isReportDefinitionSelected() {
        return reportDefinitionSelected;
    }

    public void setReportDefinitionSelected(boolean reportDefinitionSelected) {
        this.reportDefinitionSelected = reportDefinitionSelected;
    }

    public void setSelectedReportId(Long id) {
        this.selectedReportId = id;
        setReportResultsSelected(true);

        // set the default value
        beginDate = getDefaultBeginDate();
        endDate = getDefaultEndDate();

        // clear the  map that holds the invoke parameters  
        invokeReportParams.clear();

        // cleanup param panel and populate if needed
        clearCustomParamPanel();
        populateCustomParamPanel();

        setReportDefinitionSelected(false);
    }

    public Long getSelectedReportId() {
        return this.selectedReportId;
    }

    public Map<String, String>getSelectedReportParams() {
        Map<String, String> reportParams = null;
        ReportParameterJO selectedReportParams =  
            paramMap.get(getSelectedReportId());
        if (selectedReportParams != null) {
            reportParams =selectedReportParams.getTextBoxNameLabelMap(); 
        }
        return reportParams;
    }

    public List<IReport> getReportList() {
        return reportList;
    }

    public IReport getSelectedReport() {
        return getReportList().get(getSelectedReportId().intValue());
    }

    /**
     * This is invoked when the user wants to run a report. The report parameters
     * must be assembled at this point and the report invoked.
     */
    public void onExecuteReport(ActionEvent event) {
        setReportDefinitionSelected(true);
        updateInvokeParams();
        // at this point all parameters are set in the invokeReportParams 
        // and the report is ready to be run
    }


    public Map<String, String> getInvokeReportParams() {
        return invokeReportParams;
    }

    @SuppressWarnings("unchecked")
    private void updateInvokeParams() {
        invokeReportParams.clear();
        String timeStampStr = null;

        /**
         * The begin and end dates
         */
        Timestamp timestamp = new Timestamp(getBeginDate().getTime());
        timeStampStr = dateFormatter.format(timestamp);
        invokeReportParams.put(
                ICustomReportPageBean.BEGIN_DATE_PARAM_NAME,
                timeStampStr);

        timestamp = new Timestamp(getEndDate().getTime());
        timeStampStr = dateFormatter.format(timestamp);
        invokeReportParams.put(
                ICustomReportPageBean.END_DATE_PARAM_NAME,
                timeStampStr);

        /**
         * User specified parameters - assumption is that the panel maintains the
         * order
         */
        String paramName = null;
        String paramValue = null;
        List children = customParamPanel.getChildren();
        int childCount = children.size();
        if (childCount % 2 > 0) {
            throw new RuntimeException(
                    "Incorrect child cound of custom parameter panel : " + childCount );
        }
        for (int i = 0; i < childCount; i = i + 2) {
            Object child = children.get(i);
            if  (child instanceof HtmlOutputLabel) {
                paramName= (String) ((HtmlOutputLabel) child).getTitle();
            }
            child = children.get(i+1);
            if (child instanceof HtmlInputText) {
                paramValue= (String)  ((HtmlInputText) child).getValue();
            }
            if (paramName == null || paramValue == null) {
                throw new RuntimeException(
                        "Error in determining the custom parameter list for report: " 
                        + this.getSelectedReportTitle());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("CustomReportsBeanImpl: Updating parameters");
                LOG.debug(" param name:  " + paramName + 
                        " param value: " + paramValue);
            }
            invokeReportParams.put(paramName, paramValue);
            paramName = paramValue = null;
        }

        if (getLog().isDebugEnabled()) {
            getLog().debug(("CustomReportsBeanImpl: Report Parameters before invocation"));
            for (Map.Entry<String,String> entry : invokeReportParams.entrySet()) {
                getLog().debug("ParamName: " + entry.getKey());
                getLog().debug("ParamValue: " + entry.getValue());
            }
        }
    }

    public void setReportPanelHeaderLabel(
            String currentReportPanelHeaderLabel) {
        this.reportPanelHeaderLabel = currentReportPanelHeaderLabel;
    }

    public String getReportPanelHeaderLabel() {
        return reportPanelHeaderLabel;
    }

    public void setDescriptionPanelHeaderLabel(
            String descriptionPanelHeaderLabel) {
        this.descriptionPanelHeaderLabel = descriptionPanelHeaderLabel;
    }

    public String getDescriptionPanelHeaderLabel() {
        return descriptionPanelHeaderLabel;
    }

    public void setBeginDate(Date beginDate) {
        if (beginDate != null) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTimeInMillis(beginDate.getTime());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            this.beginDate = cal.getTime();
        }
    }

    public Date getBeginDate() {
        if (beginDate == null) {
            return getDefaultBeginDate();
        }
        return beginDate;
    }

    public void setEndDate(Date endDate) {
        if (endDate != null) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTimeInMillis(endDate.getTime());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            this.endDate = cal.getTime();
        }
    }

    public Date getEndDate() {
        if (endDate == null) {
            return getDefaultEndDate();
        }
        return endDate;
    }

    public Date getDefaultBeginDate() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getDefaultEndDate() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public void setParameterPanelHeaderPrefixLabel(
            String parameterPanelHeaderPrefixLabel) {
        this.parameterPanelHeaderPrefixLabel = parameterPanelHeaderPrefixLabel;
    }

    public String getParameterPanelHeaderPrefixLabel() {
        return parameterPanelHeaderPrefixLabel;
    }

    public String getSelectedReportDesignFileName() {
        String reportName  = "";

        if (isReportDefinitionSelected()) {
            Long curRepId = getSelectedReport().getId();
            CustomReportJO thisReport = reportMap.get(curRepId);
            if (thisReport == null) throw new RuntimeException("Cannot find selected report"); 
            reportName = thisReport.getDesignFiles().get(0); // top one 
        }
        return reportName;
    }


    public HtmlPanelGrid  getCustomParamPanel() {   
        customParamPanel = new HtmlPanelGrid();
        customParamPanel.setColumns(4);
        populateCustomParamPanel();
        return customParamPanel;  
    }

    public void setCustomParamPanel(HtmlPanelGrid panel) {
        customParamPanel = panel;
    }

    private void clearCustomParamPanel() {
        if (customParamPanel != null && customParamPanel.getChildren() != null)
            customParamPanel.getChildren().clear();
    }

    @SuppressWarnings("unchecked")
    private void populateCustomParamPanel() {
        if (customParamPanel == null) {
            if (getLog().isWarnEnabled()) {
                getLog().warn("Could not populate null custom params panel - it should not be null");
            }
            return;
        }
        Map<String, String> reportParams = getSelectedReportParams();
        if (reportParams != null) {
            for (Map.Entry<String,String> entry: reportParams.entrySet()) {
                HtmlOutputLabel label = new  HtmlOutputLabel();
                label.setTitle(entry.getKey());
                label.setValue(entry.getValue());
                label.setStyleClass("sharepoint_table_label");
                HtmlInputText text = new HtmlInputText();

                // preserve any value that has been set previously
                text.setValue(invokeReportParams.get(entry.getKey())); 
                customParamPanel.getChildren().add(label);
                customParamPanel.getChildren().add(text);  
            }
        }
    }

    private Log getLog() {
        return LOG;
    }
}
