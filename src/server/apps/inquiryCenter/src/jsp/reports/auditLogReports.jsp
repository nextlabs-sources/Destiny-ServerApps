<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define Audit Logs Tiles Definition --%>
  <tiles:definition id="auditLogsDefinition" extends="inquiryCenterMainWithoutContentHeaderDefinition">
    <tiles:put name="applicationTitle" value="${bundle.inquiry_center_title}" />
    <tiles:put name="pageTitle" value="${bundle.audit_log_page_title}" />
    <tiles:put name="secondaryNav" value="/WEB-INF/jspf/tiles/reports/reportSecondaryNav.jspf" />
    <tiles:put name="content" value="/WEB-INF/jspf/tiles/reports/auditLogReportsListTable.jspf" />
	<tiles:put name="helpURL"><h:outputText value="#{helpBundle.reportcreation_help_url}" /></tiles:put>
  </tiles:definition>

  <%-- Insert Audit Logs Definition --%>
  <f:subview id="auditLogsDefinitionSubview">
    <tiles:insert beanName="auditLogsDefinition" flush="false" />
  </f:subview>

</f:view>