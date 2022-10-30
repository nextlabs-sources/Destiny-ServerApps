<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define Agent Config  Tiles Definition --%>
  <tiles:definition id="agentConfigDefinition" extends="mgmtConsoleMainWithoutContentHeaderDefinition">
    <tiles:put name="applicationTitle" value="${bundle.mgmt_console_title}" />
    <tiles:put name="pageTitle" value="${bundle.agent_config_desktop_page_title}" />
    <tiles:put name="secondaryNav" value="/WEB-INF/jspf/tiles/agentconfig/agentConfigSecondaryNav.jspf" />
    <tiles:put name="content" value="/WEB-INF/jspf/tiles/agentconfig/agentConfigContent.jspf" />
    <tiles:put name="helpURL"><h:outputText value="#{helpBundle.agentconfiguration_help_url}" /></tiles:put>
  </tiles:definition>

  <%-- Insert Agent Config  Definition --%>
  <f:subview id="agentConfigDefinitionSubview">
    <tiles:insert beanName="agentConfigDefinition" flush="false" />
  </f:subview>
  
</f:view>