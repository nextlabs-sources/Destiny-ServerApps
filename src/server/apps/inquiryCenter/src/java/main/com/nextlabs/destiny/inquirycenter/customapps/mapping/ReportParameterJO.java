/*
 * Created on Mar 2, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.customapps.mapping;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;

import com.nextlabs.destiny.container.shared.customapps.mapping.IamJO;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/customapps/mapping/ReportParameterJO.java#1 $
 */

public class ReportParameterJO extends IamJO {
    private String refReportTitle;
    private String headerSuffix;
    
    private List<ParameterTextBoxJO> textBoxes;
    
    private LinkedHashMap<String, String> textBoxNameLabelMap;
    
    public ReportParameterJO(){
        textBoxes = new LinkedList<ParameterTextBoxJO>();
    }
    
    public String getRefReportTitle() {
        return refReportTitle;
    }
    public void setRefReportTitle(String refReportTitle) {
        this.refReportTitle = refReportTitle;
    }
    public String getHeaderSuffix() {
        return headerSuffix;
    }
    public void setHeaderSuffix(String headerSuffix) {
        this.headerSuffix = headerSuffix;
    }
    public void addTextBox(ParameterTextBoxJO parameterTextBoxJO){
        textBoxes.add(parameterTextBoxJO);
    }
    public List<ParameterTextBoxJO> getTextBoxes() {
        return textBoxes;
    }
    public Map<String, String> getTextBoxNameLabelMap() {
        if (textBoxNameLabelMap == null) {
            textBoxNameLabelMap = new LinkedHashMap<String, String>();
            for(ParameterTextBoxJO textBox : textBoxes){
                textBoxNameLabelMap.put(textBox.getName(), textBox.getLabel());
            }
        }
        return textBoxNameLabelMap;
    }

    @Override
    protected String getCurrentNodeName() {
        return "report-list/report-parameters";
    }
    
    @Override
    protected void addRule(Digester digester, String parent, String current){
        digester.addObjectCreate(current, ReportParameterJO.class);
        digester.addSetProperties(current, "title", "refReportTitle");
        digester.addBeanPropertySetter(current + "/parameter-header-suffix", "headerSuffix");
        digester.addSetNext(new ParameterTextBoxJO().accept(digester, current), "addTextBox");
    }
    
    @Override
    protected String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append("ReportParameterJO").append("\n")
          .append(prefix).append(" refTitle = ").append(refReportTitle).append("\n")
          .append(prefix).append(" headerSuffix = ").append(headerSuffix).append("\n");
        for (ParameterTextBoxJO p : textBoxes) {
            sb.append(p.toString(prefix + "    "));
        }

        return sb.toString();
    }
}
