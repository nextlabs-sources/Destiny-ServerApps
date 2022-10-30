/*
 * Created on Jun 23, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.IBirtConstants;
import org.eclipse.birt.report.context.ViewerAttributeBean;
import org.eclipse.birt.report.utility.ParameterAccessor;

import com.bluejungle.framework.utils.Pair;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/birt/DestinyViewerAttributeBean.java#1 $
 */

public class DestinyViewerAttributeBean extends ViewerAttributeBean {
    //24 hours, unit in milliseconds
    private static final long DEFAULT_TIMEOUT = 24 * 60 * 60 * 1000L;
    
    /**
     * The table that contains our we assign report name
     * the key is session id + report name
     * the values are assigned report name and the insertion time in milliseconds.
     */
    private static final Map<String, Pair<String, Long>> map = 
        Collections.synchronizedMap(new HashMap<String, Pair<String, Long>>());
    

    private static final Log log = LogFactory.getLog(DestinyViewerAttributeBean.class.getName());

    public DestinyViewerAttributeBean(HttpServletRequest request) {
        super(request);
        logLocation();
    }

    /*
     * A legal file name is one that is just a file name.  No relative or absolute paths.  Externally
     * supplied file name can include .. and we want to ensure that the invoker can't access files in
     * other parts of the file system.
     */
    private boolean isAcceptableFileName(String name) {
        if (name == null || name.equals("..") || name.equals(".")) {
            return false;
        }

        File fname = new File(name);
        return name.equals(fname.getName());
    }

    private void logLocation() {
        StringBuilder sb = new StringBuilder("DestinyViewerAttributeBean called\n");
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace(); 

        for (int i=1 ; i<stackTraceElements.length; i++) 
        { 
            StackTraceElement ste = stackTraceElements[i]; 
            String classname = ste.getClassName(); 
            String methodName = ste.getMethodName(); 
            int lineNumber = ste.getLineNumber(); 

            sb.append("  " + classname + "." + methodName + ":" + lineNumber + "\n"); 
        } 

        log.info(sb.toString());
    }

    /**
     * Determine the report design and doc 's timestamp
     * 
     * @param request
     * @throws Exception
     */
    protected void processReport( HttpServletRequest request ) throws Exception {
        String orginalReportDocName = this.reportDocumentName;
        
        logLocation();

        // try to get the assigned report name from the map
        Pair<String, Long> newReportDocumentPair  = map.get(request.getSession().getId() + orginalReportDocName);
        String newReportDocumentName;
        // if the name is not in the map, we use the orginal one.
        if(newReportDocumentPair != null && (newReportDocumentName = newReportDocumentPair.first()) != null ){
            //we have to set the report name before any return. Otherwise it will be too late.
            super.setReportDocumentName(newReportDocumentName);
        }else{
            newReportDocumentName = orginalReportDocName;
        }

        log.info("New report doc name = " + newReportDocumentName);

        if ( !isAcceptableFileName(newReportDocumentName)) {
            log.warn("Exiting early.  Filename " + newReportDocumentName + " is actually a path");
        }

        if ( ParameterAccessor.HEADER_REQUEST_TYPE_SOAP
                .equalsIgnoreCase( request
                        .getHeader( ParameterAccessor.HEADER_REQUEST_TYPE ) )
                || IBirtConstants.SERVLET_PATH_DOWNLOAD
                        .equalsIgnoreCase( request.getServletPath( ) ) )
            return;

        if ( this.reportDocumentName == null )
            return;

        //continue looking for a new name until the new name is ok.
        boolean isContinue;
        do{
            isContinue=false;
            File reportDocFile = new File( newReportDocumentName );
            long lastModifiedOfDesign = getLastModifiedOfDesign(request);
    
            if (reportDocFile != null && reportDocFile.exists() && reportDocFile.isFile()){
                if (lastModifiedOfDesign > reportDocFile.lastModified() || ParameterAccessor.isOverwrite(request)) {
                    // we only facing a problem with reportDocFile can't be deleted. 
                    // The file was locked by the same report in the same session.
                    if (!reportDocFile.delete()) {
                        isContinue = true;
                        String currentNumberString = newReportDocumentName.substring(orginalReportDocName.length());

                        //we start the number from 0 but will immediate increase it to 1.
                        int currentNumber = currentNumberString.length() == 0 
                            ? 0 
                            : Integer.parseInt(currentNumberString);
                        
                        //the new report document name is the previous name plus one.
                        newReportDocumentName = orginalReportDocName + (currentNumber +1);
                    }
                }
            }
        } while(isContinue);
        
        //clean up the map before we put a new entry
        purgeReportNameMap(DEFAULT_TIMEOUT);
        
        map.put(request.getSession().getId() + orginalReportDocName, 
                new Pair<String,Long>(newReportDocumentName, System.currentTimeMillis()));
            
        super.setReportDocumentName(newReportDocumentName);
    }
    
    /**
     * remove all report name that is longer than the timeout
     * @param timeout
     */
    protected void purgeReportNameMap(long timeout){
        logLocation();

        final long currentTime = System.currentTimeMillis();
        Set<String> removeEntries = new HashSet<String>();
        for(Map.Entry<String, Pair<String, Long>> entry : map.entrySet()){
            if(entry.getValue().second() + timeout < currentTime ){
                removeEntries.add(entry.getKey());
            }
        }
        for(String removeEntry :  removeEntries){
            map.remove(removeEntry);
        }
    }

    /**
     * Returns lastModified of report design file. If file doesn't exist, return
     * -1L;
     * 
     * @param request
     * @return
     */
    protected long getLastModifiedOfDesign( HttpServletRequest request )
    {
        String designFile = ParameterAccessor.getParameter( request,
                                                            ParameterAccessor.PARAM_REPORT );
        if ( designFile == null )
            return -1L;

        if (!isAcceptableFileName(this.reportDesignName)) {
            log.warn("File " + this.reportDesignName + " is a path, but it should be a file");
            return -1;
        }

        try
        {
            // according to the working folder
            File file = new File( this.reportDesignName);
            if ( file != null && file.exists( ) )
            {
                if ( file.isFile( ) )
                    return file.lastModified( );
            }
            else
            {
                // try URL resource
                if ( !designFile.startsWith( "/" ) ) //$NON-NLS-1$
                    designFile = "/" + designFile; //$NON-NLS-1$
                
                if (!isAcceptableFileName(designFile.substring(1))) {
                    log.warn("File " + designFile.substring(1) + " is a path, but it should be a file");
                    return -1;
                }

                URL url = request.getSession( ).getServletContext( ).getResource( designFile );
                if ( url != null )
                    return url.openConnection( ).getLastModified( );
            }
        }
        catch ( Exception e )
        {
        }

        return -1L;
    }
}
