/*
 * Created on Mar 24, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.customapps.mapping;

import org.apache.commons.digester.Digester;

import com.nextlabs.destiny.container.shared.customapps.mapping.IamJO;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/customapps/mapping/ParameterTextBoxJO.java#1 $
 */

public class ParameterTextBoxJO extends IamJO{
    private String name;
    private String label;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    protected String getCurrentNodeName() {
        return "text-box";
    }
    
    @Override
    protected void addRule(Digester digester, String parent, String current){
        digester.addObjectCreate(current, ParameterTextBoxJO.class);
        digester.addBeanPropertySetter(current + "/name", "name");
        digester.addBeanPropertySetter(current + "/label", "label");
    }
    
    @Override
    protected String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append("ParameterTextBoxJO").append("\n")
          .append(prefix).append(" name = ").append(name).append("\n")
          .append(prefix).append(" label = ").append(label).append("\n");

        return sb.toString();
    }
}
