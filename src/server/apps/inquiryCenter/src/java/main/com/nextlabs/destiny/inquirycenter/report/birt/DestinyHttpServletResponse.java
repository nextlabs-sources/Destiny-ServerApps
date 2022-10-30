/*
 * Created on Aug 4, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/birt/DestinyHttpServletResponse.java#1 $
 */

public class DestinyHttpServletResponse implements HttpServletResponse {
    private final HttpServletResponse response;
    private final DestinyServletOutputStream destinyServletOutputStream;
    
    public DestinyHttpServletResponse(HttpServletResponse response){
        this.response = response;
        destinyServletOutputStream = new DestinyServletOutputStream();
    }
    
    /**
     * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
     */
    public void addCookie(Cookie arg0) {
        response.addCookie(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
     */
    public void addDateHeader(String arg0, long arg1) {
        response.addDateHeader(arg0, arg1);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String arg0, String arg1) {
        response.addHeader(arg0, arg1);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
     */
    public void addIntHeader(String arg0, int arg1) {
        response.addIntHeader(arg0, arg1);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
     */
    public boolean containsHeader(String arg0) {
        return response.containsHeader(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
     */
    public String encodeRedirectURL(String arg0) {
        return response.encodeRedirectURL(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
     */
    public String encodeRedirectUrl(String arg0) {
        return response.encodeRedirectUrl(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
     */
    public String encodeURL(String arg0) {
        return response.encodeURL(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
     */
    public String encodeUrl(String arg0) {
        return response.encodeUrl(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#getHeader(String)
     */
    public String getHeader(String name) {
        return response.getHeader(name);
    }
    /**
     * @see javax.servlet.http.HttpServletResponse#getHeaderNames()
     */
    public Collection<String> getHeaderNames() {
        return response.getHeaderNames();
    }
 
    /**
     * @see javax.servlet.http.HttpServletResponse#getHeaders(String)
     */
    public Collection<String> getHeaders(String name) {
        return response.getHeaders(name);
    }
 
    /**
     * @see javax.servlet.http.HttpServletResponse#getStatus()
     */
    public int getStatus() {
        return response.getStatus();
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    public void sendError(int arg0) throws IOException {
        response.sendError(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
     */
    public void sendError(int arg0, String arg1) throws IOException {
        response.sendError(arg0, arg1);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
     */
    public void sendRedirect(String arg0) throws IOException {
        response.sendRedirect(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setContentLengthLong(long)
     */
    public void setContentLengthLong(long len) {
        response.setContentLengthLong(len);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
     */
    public void setDateHeader(String arg0, long arg1) {
        response.setDateHeader(arg0, arg1);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String arg0, String arg1) {
        response.setHeader(arg0, arg1);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
     */
    public void setIntHeader(String arg0, int arg1) {
        response.setIntHeader(arg0, arg1);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    public void setStatus(int arg0) {
        response.setStatus(arg0);
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
     */
    public void setStatus(int arg0, String arg1) {
        response.setStatus(arg0, arg1);
    }

    /**
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    public void flushBuffer() throws IOException {
        destinyServletOutputStream.printWriter.flush();
        String contend = destinyServletOutputStream.byteArrayOutputStream.toString();
        
        contend = filter(contend);
        
        response.getOutputStream().write(contend.getBytes());
        response.flushBuffer();
        
        destinyServletOutputStream.byteArrayOutputStream.reset();
    }
    
    private String filter(String input){
        return Pattern.compile("&lt;hr style=&quot;color:red&quot;/&gt;\\s*" +
                "&lt;div style=&quot;color:red&quot;&gt;\\s*" +
                "&lt;div&gt;The following items have errors:.+?" +
                "Unable to render too many steps. Please adjust the Scale Step or Unit.+?" +
                "&lt;br&gt;\\s*&lt;/pre&gt;\\s*&lt;/div&gt;\\s*&lt;/div&gt;\\s*&lt;br&gt;", 
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(input).replaceAll("");
    }

    /**
     * @see javax.servlet.ServletResponse#getBufferSize()
     */
    public int getBufferSize() {
        //TODO 
        return response.getBufferSize();
    }

    /**
     * @see javax.servlet.ServletResponse#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    /**
     * @see javax.servlet.ServletResponse#getContentType()
     */
    public String getContentType() {
        return response.getContentType();
    }

    /**
     * @see javax.servlet.ServletResponse#getLocale()
     */
    public Locale getLocale() {
        return response.getLocale();
    }

    /**
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    public ServletOutputStream getOutputStream() throws IOException {
        //return our buffered outputstream
        return destinyServletOutputStream;
    }

    /**
     * @see javax.servlet.ServletResponse#getWriter()
     */
    public PrintWriter getWriter() throws IOException {
        return destinyServletOutputStream.getWriter();
    }

    /**
     * @see javax.servlet.ServletResponse#isCommitted()
     */
    public boolean isCommitted() {
        return response.isCommitted();
    }

    /**
     * @see javax.servlet.ServletResponse#reset()
     */
    public void reset() {
        response.reset();
    }

    /**
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    public void resetBuffer() {
        response.resetBuffer();
    }

    /**
     * @see javax.servlet.ServletResponse#setBufferSize(int)
     */
    public void setBufferSize(int arg0) {
        response.setBufferSize(arg0);
    }

    /**
     * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding(String arg0) {
        response.setCharacterEncoding(arg0);
    }

    /**
     * @see javax.servlet.ServletResponse#setContentLength(int)
     */
    public void setContentLength(int arg0) {
        response.setContentLength(arg0);
    }

    /**
     * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
     */
    public void setContentType(String arg0) {
        response.setContentType(arg0);
    }

    /**
     * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
     */
    public void setLocale(Locale arg0) {
        response.setLocale(arg0);
    }

    private class DestinyServletOutputStream extends ServletOutputStream{
        private final ByteArrayOutputStream byteArrayOutputStream;
        private final PrintWriter printWriter;
        private WriteListener listener = null;

        /**
         * Constructor
         * @param byteArrayOutputStream
         */
        public DestinyServletOutputStream() {
            super();
            this.byteArrayOutputStream = new ByteArrayOutputStream();
            this.printWriter = new PrintWriter(byteArrayOutputStream);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void write(int b) throws IOException {
            byteArrayOutputStream.write(b);

            if (listener != null) {
                listener.notify();
            }
        }
        
        public PrintWriter getWriter() throws IOException {
            return printWriter;
        }
        
        @Override
        public void setWriteListener(WriteListener listener) {
            this.listener = listener;
        }
    }
}
