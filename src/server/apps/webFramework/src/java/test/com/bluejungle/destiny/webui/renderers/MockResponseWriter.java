/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

/**
 * This is a mock response writer object
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/MockResponseWriter.java#1 $
 */

public class MockResponseWriter extends ResponseWriter {

    private boolean isInElement;
    private String response = "";

    /**
     * @see javax.faces.context.ResponseWriter#getContentType()
     */
    public String getContentType() {
        return null;
    }

    /**
     * @see javax.faces.context.ResponseWriter#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return null;
    }

    /**
     * Returns the response object
     * 
     * @return the response object
     */
    public String getResponse() {
        return this.response;
    }

    /**
     * @see javax.faces.context.ResponseWriter#flush()
     */
    public void flush() throws IOException {
    }

    /**
     * Resets the response object
     */
    public void reset() {
        this.response = "";
        this.isInElement = false;
    }

    /**
     * @see javax.faces.context.ResponseWriter#startDocument()
     */
    public void startDocument() throws IOException {
    }

    /**
     * @see javax.faces.context.ResponseWriter#endDocument()
     */
    public void endDocument() throws IOException {
    }

    /**
     * @see javax.faces.context.ResponseWriter#startElement(java.lang.String,
     *      javax.faces.component.UIComponent)
     */
    public void startElement(String arg0, UIComponent arg1) throws IOException {
        if (this.isInElement) {
            this.response += ">";
        }
        this.response += "<" + arg0;
        this.isInElement = true;
    }

    /**
     * @see javax.faces.context.ResponseWriter#endElement(java.lang.String)
     */
    public void endElement(String arg0) throws IOException {
        if (this.isInElement) {
            this.response += ">";
        } else {
            this.response += "</" + arg0 + ">";
        }

    }

    /**
     * @see javax.faces.context.ResponseWriter#writeAttribute(java.lang.String,
     *      java.lang.Object, java.lang.String)
     */
    public void writeAttribute(String arg0, Object arg1, String arg2) throws IOException {
        this.response += " " + arg0 + "=\"" + arg1.toString() + "\"";
    }

    /**
     * @see javax.faces.context.ResponseWriter#writeURIAttribute(java.lang.String,
     *      java.lang.Object, java.lang.String)
     */
    public void writeURIAttribute(String arg0, Object arg1, String arg2) throws IOException {
    }

    /**
     * @see javax.faces.context.ResponseWriter#writeComment(java.lang.Object)
     */
    public void writeComment(Object arg0) throws IOException {
    }

    /**
     * @see javax.faces.context.ResponseWriter#writeText(java.lang.Object,
     *      java.lang.String)
     */
    public void writeText(Object arg0, String arg1) throws IOException {
    }

    /**
     * @see javax.faces.context.ResponseWriter#writeText(char[], int, int)
     */
    public void writeText(char[] arg0, int arg1, int arg2) throws IOException {
    }

    /**
     * @see javax.faces.context.ResponseWriter#cloneWithWriter(java.io.Writer)
     */
    public ResponseWriter cloneWithWriter(Writer arg0) {
        return null;
    }

    /**
     * @see java.io.Writer#close()
     */
    public void close() throws IOException {
    }

    /**
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] cbuf, int off, int len) throws IOException {
    }

}