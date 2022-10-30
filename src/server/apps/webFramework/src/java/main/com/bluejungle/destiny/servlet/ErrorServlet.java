/*
 * Created on Sep 29, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Error handling servlet. This servlet is also used for jsps, as the Tomcat
 * error page does not work properly (currently throws an NPE during the forward
 * to the error page)
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/servlet/ErrorServlet.java#1 $
 */

public class ErrorServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog("com.bluejungle.destiny.servlet.ErrorServlet");
    private static final String DEFAULT_ERROR_DISPLAY_URI = "/error/errorDisplay.jsp";
    private static final String ERROR_DISPLAY_URI_PARAM_NAME = "errorDisplayURI";

    private static final String META_REFRESH_BEGIN_STRING = "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL=";
    private static final String META_REFRESH_END_STRING = "\" />";
    private static final String INCLUDE_REQUEST_URI_ATT_NAME = "javax.servlet.include.request_uri";

    private String errorDisplayURI;

    /**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        this.errorDisplayURI = servletConfig.getInitParameter(ERROR_DISPLAY_URI_PARAM_NAME);
        if (this.errorDisplayURI == null) {
            this.errorDisplayURI = DEFAULT_ERROR_DISPLAY_URI;
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        // Log
        logErrors(servletRequest);

        // Redirect to the proper error page
        redirectToErrorDisplay(servletRequest, servletResponse);
    }

    /**
     * Perform the redirect to the error page display
     * 
     * @param servletRequest
     * @param servletResponse
     * @throws IOException
     */
    private void redirectToErrorDisplay(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        // Build redirect URL
        String contextRoot = servletRequest.getContextPath();
        String encodedRedirectURL = servletResponse.encodeRedirectURL(contextRoot + this.errorDisplayURI);

        if (!servletResponse.isCommitted()) {
            // all content may still be in the buffer. Reset
            try {
                servletResponse.resetBuffer();
            } catch (IllegalStateException exception) {
                /**
                 * Hate to catch a Runtime here, but there's no way to determine
                 * if the Illegal State will be thrown. If we've come here from
                 * an error in a jsp which has a buffer of size 0, the
                 * IllegalStateException will be thrown. No way of knowing this
                 */
                printRedirectAfterCommit(servletResponse, encodedRedirectURL);

                /**
                 * Another cardinal sin. Returning from a catch block. I don't
                 * want to include the rest of the if block within the try due
                 * to the impact (i.e. don't want to catch IllegalStateException
                 * from anything other than the buffer reset
                 */
                return;
            }

            if (!inInclude(servletRequest)) {
                servletResponse.sendRedirect(encodedRedirectURL);
            } else {
                printRedirect(servletResponse, encodedRedirectURL);
            }
        } else {
            // some content already returned to client.
            printRedirectAfterCommit(servletResponse, encodedRedirectURL);
        }
    }

    /**
     * Print redirect after content has already been returned to the client
     * 
     * @param servletResponse
     * @param encodedRedirectURL
     * @throws IOException
     */
    private void printRedirectAfterCommit(HttpServletResponse servletResponse, String encodedRedirectURL) throws IOException {
        PrintWriter responseWriter = servletResponse.getWriter();
        responseWriter.write(">"); // attempt to close any open html tag
        printRedirect(servletResponse, encodedRedirectURL);
    }

    /**
     * Print redirect to the client
     * 
     * @param servletResponse
     * @param encodedRedirectURL
     * @throws IOException
     */
    private void printRedirect(HttpServletResponse servletResponse, String encodedRedirectURL) throws IOException {
        PrintWriter responseWriter = servletResponse.getWriter();
        responseWriter.write(META_REFRESH_BEGIN_STRING);
        responseWriter.write(encodedRedirectURL);
        responseWriter.write(META_REFRESH_END_STRING);
    }

    /**
     * Determine if this error servlet was reached by way of an include
     * 
     * @param servletRequest
     * @return true if the servlet was included; false otherwise
     */
    private boolean inInclude(HttpServletRequest servletRequest) {
        return (servletRequest.getAttribute(INCLUDE_REQUEST_URI_ATT_NAME) != null);
    }

    /**
     * Log the error which caused the servler container to invoke this error
     * servlet
     * 
     * @param servletRequest
     */
    private void logErrors(HttpServletRequest servletRequest) {
        Throwable generatedJspException = (Throwable) servletRequest.getAttribute(PageContext.EXCEPTION);
        Throwable generatedServletException = (Throwable) servletRequest.getAttribute("javax.servlet.error.exception");
        String attemptedURI = (String) servletRequest.getAttribute("javax.servlet.error.request_uri");

        if (generatedJspException != null) {
            LOG.error("Error occured while trying to access web resource, " + attemptedURI + ",  Exception in jsp:", generatedJspException);
        }

        if (generatedServletException != null) {
            LOG.error("Error occured while trying to access web resource, " + attemptedURI + ",  Exception in servlet:", generatedServletException);
        }

        if ((generatedJspException == null) && (generatedServletException == null)) {
            LOG.error("Error occured while trying to access web resource, " + attemptedURI);
        }
    }
}