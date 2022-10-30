<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define Available Users Browse View Tiles Definition --%>
  <tiles:definition id="userGroupsBrowseAvailableExternalGroupsDefinition" extends="mgmtConsoleBrowseDataPickerDefinition">
    <tiles:put name="applicationTitle">
      <h:outputText value="#{bundle.mgmt_console_title}" />
    </tiles:put>
    <tiles:put name="pageTitle">
      <h:outputText value="#{bundle.users_and_roles_user_groups_browse_available_external_groups_page_title}" />
    </tiles:put>
    <tiles:put name="selectedItemBoxLabel">
      <h:outputText id="slctItmBoxLbl" value="#{bundle.users_and_roles_user_groups_browse_available_external_groups_selected_items_box_header}" />
    </tiles:put>
    <tiles:put name="helpURL"><h:outputText value="#{helpBundle.usermanagement_help_url}" /></tiles:put>
  </tiles:definition>
  
  <%-- Set styles for selectable items --%>
  <c:set var="boldStyleClassId" value="selectableItemGroup" scope="request"/>
  <c:set var="defaultStyleClassId" value="selectableItem" scope="request" />
  <c:set var="disabledStyleClassId" value="selectableItemAlreadySelected" scope="request" />
  
  <%-- Insert External User Group Browse Definition --%>
  <f:subview id="userGroupsBrowseAvailableExternalGroupsDefinitionSubview">
    <tiles:insert beanName="userGroupsBrowseAvailableExternalGroupsDefinition" flush="false" />
  </f:subview>    
    
</f:view>