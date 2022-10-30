/*
 * Created on Mar 16, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bluejungle.destiny.webui.framework.context.AppContextImpl;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.nextlabs.destiny.inquirycenter.customapps.ExternalReportAppManager;
import com.nextlabs.destiny.inquirycenter.customapps.IExternalReportApplication;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/context/ReportAppContext.java#1 $
 */

public class ReportAppContext extends AppContextImpl{
    protected final String sessionId;

    public ReportAppContext(HttpServletRequest request) {
        super(request);
        HttpSession session = request.getSession(); 
        sessionId = session.getId();
        
        getExternalReportApplication().hold(sessionId, session.getMaxInactiveInterval());
    }

    @Override
    public void releaseContext() {
        super.releaseContext();
        getExternalReportApplication().release(sessionId);
    }

    @Override
    public void releaseContext(HttpServletRequest httpServletRequest) {
        super.releaseContext(httpServletRequest);
        getExternalReportApplication().release(sessionId);
    }
    
    protected IExternalReportApplication getExternalReportApplication(){
        return ComponentManagerFactory.getComponentManager().getComponent(ExternalReportAppManager.class);
    }
}
