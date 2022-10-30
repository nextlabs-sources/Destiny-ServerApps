package com.nextlabs.destiny.console.dto.policymgmt.policyvalidator;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public class PdpResponse implements java.io.Serializable {
    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    private java.lang.String format;

    private java.lang.String content;

    /**
     * Returns the value of property "format". response format, e.g. xml
     */
    public java.lang.String getFormat() {
        return format;
    }

    /**
     * Updates the value of property "format".
     */
    public void setFormat(java.lang.String format) {
        this.format = format;
    }

    /**
     * Returns the value of property "content". response content
     */
    public java.lang.String getContent() {
        return content;
    }

    /**
     * Updates the value of property "content".
     */
    public void setContent(java.lang.String content) {
        this.content = content;
    }

}