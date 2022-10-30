/*
 * Created on Mar 2, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.customapps.mapping;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.digester.Digester;

import com.nextlabs.destiny.container.shared.customapps.mapping.IamJO;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/customapps/mapping/CustomReportUIJO.java#1 $
 */

public class CustomReportUIJO extends IamJO {
    private String listHeader;
    private String descriptionHeader;
    private String parameterHeader;
    private List<ReportParameterJO> reportList;
    
    public CustomReportUIJO(){
        reportList = new LinkedList<ReportParameterJO>();
    }
    
    public String getListHeader() {
        return listHeader;
    }
    public void setListHeader(String listHeader) {
        this.listHeader = listHeader;
    }
    public String getDescriptionHeader() {
        return descriptionHeader;
    }
    public void setDescriptionHeader(String descriptionHeader) {
        this.descriptionHeader = descriptionHeader;
    }
    public String getParameterHeader() {
        return parameterHeader;
    }
    public void setParameterHeader(String parameterHeader) {
        this.parameterHeader = parameterHeader;
    }
    public List<ReportParameterJO> getReportParameters() {
        return reportList;
    }
    public void addReportParameter(ReportParameterJO reportParameter) {
        this.reportList.add(reportParameter);
    }
    
    @Override
    protected String getCurrentNodeName() {
        return "custom-reports-ui";
    }

    @Override
    protected void addRule(Digester digester, String parent, String current){
        digester.addObjectCreate(current, CustomReportUIJO.class);
        digester.addBeanPropertySetter(current + "/list-header", "listHeader");
        digester.addBeanPropertySetter(current + "/description-header", "descriptionHeader");
        digester.addBeanPropertySetter(current + "/parameter-header", "parameterHeader");
        digester.addSetNext(new ReportParameterJO().accept(digester, current), "addReportParameter");
    }

    @Override
    protected String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append("CustomReportUIJO").append("\n")
          .append(prefix).append(" listHeader = ").append(listHeader).append("\n")
          .append(prefix).append(" descriptionHeader = ").append(descriptionHeader).append("\n")
          .append(prefix).append(" parameterHeader = ").append(parameterHeader).append("\n");
        for (ReportParameterJO r : reportList) {
            sb.append(r.toString(prefix + "    "));
        }
        return sb.toString();
    }
}
