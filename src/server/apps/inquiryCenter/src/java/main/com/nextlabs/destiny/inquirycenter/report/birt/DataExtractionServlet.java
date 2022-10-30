package com.nextlabs.destiny.inquirycenter.report.birt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.servlet.BirtEngineServlet;

import com.bluejungle.destiny.webui.framework.loginmgr.LoginFilterImpl;

/**
 * This is used only for extracting data from a report e.g. when a report data
 * is exported to a CSV file. The web.xml has been modified to trigger this servlet
 * when the /extract URL pattern is used.
 * 
 * @author ssen
 *
 */
public class DataExtractionServlet extends BirtEngineServlet {

    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String HTTP_1_1 = "HTTP/1.1";
    private static final String MAX_AGE_0 = "max-age=0, must-revalidate";
    private static final String PRAGMA = "Pragma";
    private static final String PUBLIC = "public";
    private static final String HTTP_1_0 = "HTTP/1.0";
    
    private static final Log LOG =
        LogFactory.getLog(DataExtractionServlet.class.getName());
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException  {

        /**
         * When extracting documents in csv format, IE has a bug that prevents it 
         * from displayiing the downloaded file if the Cache-Control header is set to
         * no-cache. This resets this. 
         */
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.contains("MSIE")) {
            if (request.getProtocol().compareTo(HTTP_1_0) == 0) {
                response.setHeader(PRAGMA, PUBLIC);
            } else if (request.getProtocol().compareTo(HTTP_1_1) == 0) {
                LOG.debug("User Agent is MSIE: Setting the Cache-Control Heade to: " +
                                MAX_AGE_0);
                response.setHeader(CACHE_CONTROL, MAX_AGE_0);
            }
        }
        super.doPost(request, response);
    }
}
