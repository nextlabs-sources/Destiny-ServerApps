<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define SharePoint Reports Tiles Definition --%>
  <tiles:definition id="sharePointReportsDefinition" extends="inquiryCenterMainWithoutContentHeaderDefinition">
    <tiles:put name="applicationTitle" value="${bundle.inquiry_center_title}" />
    <tiles:put name="pageTitle" value="${bundle.sharepoint_reports_page_title}" />
    <tiles:put name="secondaryNav" value="/WEB-INF/jspf/tiles/reports/reportSecondaryNav.jspf" />
    <tiles:put name="content" value="/WEB-INF/jspf/tiles/reports/sharePointReportsContent.jspf" />
	<tiles:put name="helpURL"><h:outputText value="#{helpBundle.reportcreation_help_url}" /></tiles:put>
  </tiles:definition>

  <%-- Insert SharePoint Reports Definition --%>
  <f:subview id="sharePointReportsDefinitionSubview">
    <tiles:insert beanName="sharePointReportsDefinition" flush="false" />
  </f:subview>

</f:view>