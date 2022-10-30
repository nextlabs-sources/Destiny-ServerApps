/*
 * Created on Jun 23, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.IBirtConstants;
import org.eclipse.birt.report.context.BirtContext;
import org.eclipse.birt.report.context.ViewerAttributeBean;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/birt/DestinyBirtContext.java#1 $
 */

public class DestinyBirtContext extends BirtContext {

    public DestinyBirtContext(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }
    
    /**
     * Local init.
     * 
     * @return
     */
    protected void __init( )
    {
        this.bean = (DestinyViewerAttributeBean) request
                .getAttribute( IBirtConstants.ATTRIBUTE_BEAN );
        if ( bean == null )
        {
            bean = new DestinyViewerAttributeBean( request );
        }
        request.setAttribute( IBirtConstants.ATTRIBUTE_BEAN, bean );
    }

}
