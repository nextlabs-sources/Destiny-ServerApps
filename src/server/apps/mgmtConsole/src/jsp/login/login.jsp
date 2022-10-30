<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:view>

  <%-- Load Resource Bundles --%>
  <f:loadBundle basename="MgmtConsoleMessages" var="bundle" />

  <%-- Define Home Page Tiles Definition --%>
  <tiles:definition id="loginDefinition" extends="mgmtConsoleLoginDefinition">
	<tiles:put name="applicationTitle" value="${bundle.mgmt_console_title}" />
  </tiles:definition>
 
  <%-- Insert Home Definition --%>
  <f:subview id="loginDefinitionSubview">
    <tiles:insert beanName="loginDefinition" flush="false" />
  </f:subview>

</f:view>