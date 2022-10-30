<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%@ include file="/WEB-INF/jspf/agentconfig/browseHostsSelectedItemListSpec.jspf" %>
  
  <%-- Define Agent Config  Tiles Definition --%>
  <tiles:definition id="agentConfigBrowseHostsDefinition" extends="mgmtConsoleBrowseDataPickerDefinition">
    <tiles:put name="applicationTitle">
      <h:outputText value="#{bundle.mgmt_console_title}" />
    </tiles:put>
    <tiles:put name="pageTitle">
      <h:outputFormat value="#{bundle.agent_config_browse_hosts_page_title}" >
        <f:param id="profTtle" value="#{desktopAgentConfigurationBean.selectedProfile.profileTitle}" />
      </h:outputFormat>
    </tiles:put>
    <tiles:put name="selectedItemBoxLabel">
      <h:outputText id="slctItmBoxLbl" value="#{bundle.agent_config_browse_selected_items_box_header}" />
    </tiles:put>
    <tiles:put name="selectedItemListSpec" beanName="selectedItemListSpecBean" />
    <tiles:put name="helpURL"><h:outputText value="#{helpBundle.agentconfiguration_help_url}" /></tiles:put>
  </tiles:definition>
  
  <%-- Set styles for selectable items --%>
  <c:set var="boldStyleClassId" value="selectableItemGroup" scope="request"/>
  <c:set var="defaultStyleClassId" value="selectableItem" scope="request" />
  <c:set var="disabledStyleClassId" value="selectableItemAlreadySelected" scope="request" />
  
  <%-- Insert Agent Config  Definition --%>
  <f:subview id="agentConfigBrowseHostsDefinitionSubview">
    <tiles:insert beanName="agentConfigBrowseHostsDefinition" flush="false" />
  </f:subview>    
    
</f:view>