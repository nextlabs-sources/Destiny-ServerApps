package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public class ObligationParameter implements java.io.Serializable {
    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    private java.lang.String attributeId;

    private java.util.List<java.lang.String> value = new java.util.ArrayList<>();

    private java.lang.String dataType;

    /**
     * Returns the value of property "AttributeId".
     * 
     */
    public java.lang.String getAttributeId() {
        return attributeId;
    }

    /**
     * Updates the value of property "AttributeId".
     */
    public void setAttributeId(java.lang.String attributeId) {
        this.attributeId = attributeId;
    }

    /**
     * Returns the value of property "Value".
     * 
     */
    public java.util.List<java.lang.String> getValue() {
        return value;
    }

    /**
     * Updates the value of property "Value".
     */
    public void setValue(java.util.List<java.lang.String> value) {
        this.value = value;
    }

    /**
     * Returns the value of property "DataType".
     * 
     */
    public java.lang.String getDataType() {
        return dataType;
    }

    /**
     * Updates the value of property "DataType".
     */
    public void setDataType(java.lang.String dataType) {
        this.dataType = dataType;
    }

}
