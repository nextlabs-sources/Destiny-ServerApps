<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define My Reports Tiles Definition --%>
  <tiles:definition id="statusOverviewDefinition" extends="mgmtConsoleMainDefinition">
    <tiles:put name="applicationTitle" value="${bundle.mgmt_console_title}" />
    <tiles:put name="pageTitle" value="${bundle.status_overview_page_title}" />
    <tiles:put name="secondaryNav" value="/WEB-INF/jspf/tiles/status/statusSecondaryNav.jspf" />
    <tiles:put name="content" value="/WEB-INF/jspf/tiles/status/statusOverviewContent.jspf" />
    <tiles:put name="helpURL"><h:outputText value="#{helpBundle.status_help_url}" /></tiles:put>
  </tiles:definition>

  <%-- Insert My Reports Definition --%>
  <f:subview id="statusOverviewDefinitionSubview">
    <tiles:insert beanName="statusOverviewDefinition" flush="false" />
  </f:subview>

</f:view>