<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define My Alerts Tiles Definition --%>
  <tiles:definition id="myAlertsDefinition" extends="inquiryCenterMainWithoutContentHeaderDefinition">
    <tiles:put name="applicationTitle" value="${bundle.inquiry_center_title}" />
    <tiles:put name="pageTitle" value="${bundle.my_monitors_page_title}" />
    <tiles:put name="secondaryNav" value="/WEB-INF/jspf/tiles/monitors/monitoringSecondaryNav.jspf" />
    <tiles:put name="content" value="/WEB-INF/jspf/tiles/monitors/myAlertsListTable.jspf" />
	<tiles:put name="helpURL"><h:outputText value="#{helpBundle.reportcreation_help_url}" /></tiles:put>
  </tiles:definition>

  <%-- Insert My Alerts Definition --%>
  <f:subview id="myAlertsDefinitionSubview">
    <tiles:insert beanName="myAlertsDefinition" flush="false" />
  </f:subview>

</f:view>