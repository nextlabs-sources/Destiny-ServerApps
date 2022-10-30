<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define add-monitor Tiles Definition --%>
  <tiles:definition id="addMonitorsDefinition" extends="inquiryCenterMainWithoutContentHeaderDefinition">
    <tiles:put name="applicationTitle" value="${bundle.inquiry_center_title}" />
    <tiles:put name="pageTitle" value="${bundle.my_monitors_page_title}" />
    <tiles:put name="content" value="/WEB-INF/jspf/tiles/monitors/myMonitorsContent.jspf" />
	<tiles:put name="helpURL"><h:outputText value="#{helpBundle.reportcreation_help_url}" /></tiles:put>
  </tiles:definition>

  <%-- Insert add-monitors Definition --%>
  <f:subview id="addMonitorsDefinitionSubview">
    <tiles:insert beanName="addMonitorsDefinition" flush="false" />
  </f:subview>

</f:view>