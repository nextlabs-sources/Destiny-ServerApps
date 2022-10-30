package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

import java.util.List;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public class Attribute implements java.io.Serializable {
    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    private java.lang.String attributeId;

    private java.lang.String attributeValue;
    
    private List<String> listValue;

    /**
     * Returns the value of property "attributeId". attribute id, e.g. file_ext
     */
    public java.lang.String getAttributeId() {
        return attributeId;
    }

    /**
     * Updates the value of property "attributeId".
     */
    public void setAttributeId(java.lang.String attributeId) {
        this.attributeId = attributeId;
    }

    /**
     * Returns the value of property "attributeValue". attribute value, e.g. PDF
     */
    public java.lang.String getAttributeValue() {
        return attributeValue;
    }

    /**
     * Updates the value of property "attributeValue".
     */
    public void setAttributeValue(java.lang.String attributeValue) {
        this.attributeValue = attributeValue;
    }

    /**
     * @return the listValue
     */
    public List<String> getListValue() {
        return listValue;
    }

    /**
     * @param listValue the listValue to set
     */
    public void setListValue(List<String> listValue) {
        this.listValue = listValue;
    }

}