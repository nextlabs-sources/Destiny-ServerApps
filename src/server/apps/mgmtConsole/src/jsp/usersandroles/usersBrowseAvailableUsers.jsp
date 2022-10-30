<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define Available Users Browse View Tiles Definition --%>
  <tiles:definition id="usersAndRolesUsersBrowseAvailableUsersDefinition" extends="mgmtConsoleBrowseDataPickerDefinition">
    <tiles:put name="applicationTitle">
      <h:outputText value="#{bundle.mgmt_console_title}" />
    </tiles:put>
    <tiles:put name="pageTitle">
      <h:outputText value="#{bundle.users_and_roles_users_browse_available_users_page_title}" />
    </tiles:put>
    <tiles:put name="selectedItemBoxLabel">
      <h:outputText id="slctItmBoxLbl" value="#{bundle.users_and_roles_users_browse_available_users_selected_items_box_header}" />
    </tiles:put>
	<tiles:put name="helpURL"><h:outputText value="#{helpBundle.usermanagement_help_url}" /></tiles:put>
  </tiles:definition>
  
  <%-- Set styles for selectable items --%>
  <c:set var="boldStyleClassId" value="selectableItemGroup" scope="request"/>
  <c:set var="defaultStyleClassId" value="selectableItem" scope="request" />
  <c:set var="disabledStyleClassId" value="selectableItemAlreadySelected" scope="request" />
  
  <%-- Insert Agent Config Desktop Definition --%>
  <f:subview id="usersAndRolesUsersBrowseAvailableUsersDefinitionSubview">
    <tiles:insert beanName="usersAndRolesUsersBrowseAvailableUsersDefinition" flush="false" />
  </f:subview>    
    
</f:view>