<%-----------------------------------------------------------------------------
	Copyright (c) 2004 Actuate Corporation and others.
	All rights reserved. This program and the accompanying materials 
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html
	
	Contributors:
		Actuate Corporation - Initial implementation.
-----------------------------------------------------------------------------%>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ page session="false" buffer="none" %>
<%@ page import="org.eclipse.birt.report.presentation.aggregation.IFragment,
				 org.eclipse.birt.report.resource.BirtResources,
				 org.eclipse.birt.report.utility.ParameterAccessor,
				 org.eclipse.birt.report.servlet.ViewerServlet" %>

<%-----------------------------------------------------------------------------
	Expected java beans
-----------------------------------------------------------------------------%>
<jsp:useBean id="fragment" type="org.eclipse.birt.report.presentation.aggregation.IFragment" scope="request" />
<jsp:useBean id="attributeBean" type="org.eclipse.birt.report.context.BaseAttributeBean" scope="request" />

<style>
.custom_toolbar
{
    height:26px;
    background-color:rgb(77,109,34);
    font-family:Verdana;
    font-size:8pt;
    border-top-width:1px;
    border-top-style:solid;
    border-top-color:rgb(77,109,34);
    border-bottom-width:1px;
    border-bottom-style:solid;
    border-bottom-color:rgb(77,109,34);
}
</style>
<%-----------------------------------------------------------------------------
	Toolbar fragment
-----------------------------------------------------------------------------%>
<TR HEIGHT="20px">
	<TD COLSPAN='2'>
		<DIV ID="toolbar">
			<TABLE CELLSPACING="1px" CELLPADDING="1px" WIDTH="100%" CLASS="custom_toolbar" >
				<TR><TD></TD></TR>
				<TR>
					<TD WIDTH="6px"/>
					<TD WIDTH="15px">
                       <INPUT TYPE="image" NAME='export' SRC="birt/images/Export.gif"
                            TITLE="<%= BirtResources.getHtmlMessage( "birt.viewer.toolbar.export" )%>"
                            ALT="<%= BirtResources.getHtmlMessage( "birt.viewer.toolbar.export" )%>" CLASS="birtviewer_clickable">
                    </TD>
                    <TD WIDTH="6px"/>
					<TD WIDTH="15px">
					   <INPUT TYPE="image" NAME='print' SRC="birt/images/Print.gif"
					   		TITLE="<%= BirtResources.getHtmlMessage( "birt.viewer.toolbar.print" )%>"
					   		ALT="<%= BirtResources.getHtmlMessage( "birt.viewer.toolbar.print" )%>" CLASS="birtviewer_clickable">
					</TD>
					<%
					if( ParameterAccessor.isSupportedPrintOnServer )
					{
					%>					
					<TD WIDTH="6px"/>
					<TD WIDTH="15px">
					   <INPUT TYPE="image" NAME='printServer' SRC="birt/images/PrintServer.gif"
					   		TITLE="<%= BirtResources.getHtmlMessage( "birt.viewer.toolbar.printserver" )%>"
					   		ALT="<%= BirtResources.getHtmlMessage( "birt.viewer.toolbar.printserver" )%>" CLASS="birtviewer_clickable">
					</TD>
					<%
					}
					%>										
					<TD ALIGN='right'>
					</TD>
					<TD WIDTH="6px"/>
				</TR>
			</TABLE>
		</DIV>
	</TD>
</TR>
