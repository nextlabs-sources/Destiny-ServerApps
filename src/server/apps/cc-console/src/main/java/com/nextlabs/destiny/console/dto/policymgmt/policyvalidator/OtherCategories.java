package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public class OtherCategories implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4791381721365253390L;

    private java.lang.String category;

    private java.util.List<Attribute> attributes = new java.util.ArrayList<>();

    /**
     * Returns the value of property "category". e.g. my-attributes
     */
    public java.lang.String getCategory() {
        return category;
    }

    /**
     * Updates the value of property "category".
     */
    public void setCategory(java.lang.String category) {
        this.category = category;
    }

    /**
     * Returns the value of property "attributes".
     * 
     */
    public java.util.List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Updates the value of property "attributes".
     */
    public void setAttributes(java.util.List<Attribute> attributes) {
        this.attributes = attributes;
    }

}