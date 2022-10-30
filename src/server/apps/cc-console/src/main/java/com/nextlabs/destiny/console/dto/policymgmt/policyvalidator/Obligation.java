package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public class Obligation implements java.io.Serializable {
    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    private java.lang.String id;

    private java.util.List<ObligationParameter> attributeAssignment = new java.util.ArrayList<>();

    /**
     * Returns the value of property "ID". The id of this obligation
     */
    public java.lang.String getId() {
        return id;
    }

    /**
     * Updates the value of property "ID".
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }

    /**
     * Returns the value of property "AttributeAssignment".
     * 
     */
    public java.util.List<ObligationParameter> getAttributeAssignment() {
        return attributeAssignment;
    }

    /**
     * Updates the value of property "AttributeAssignment".
     */
    public void setAttributeAssignment(java.util.List<ObligationParameter> attributeAssignment) {
        this.attributeAssignment = attributeAssignment;
    }

}
