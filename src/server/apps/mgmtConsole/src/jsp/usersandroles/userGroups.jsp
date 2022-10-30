<%@ page errorPage="/error" %>  

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define Groups Tiles Definition --%>
  <tiles:definition id="userGroupsDefinition" extends="mgmtConsoleMainWithoutContentHeaderDefinition">
    <tiles:put name="applicationTitle" value="${bundle.mgmt_console_title}" />
    <tiles:put name="pageTitle" value="${bundle.users_and_roles_user_groups_page_title}" />
    <tiles:put name="secondaryNav" value="/WEB-INF/jspf/tiles/usersandroles/usersAndRolesSecondaryNav.jspf" />
    <tiles:put name="content" value="/WEB-INF/jspf/tiles/usersandroles/userGroupsContent.jspf" />
    <tiles:put name="helpURL"><h:outputText value="#{helpBundle.usermanagement_help_url}" /></tiles:put>
  </tiles:definition>

  <%-- Insert Groups Definition --%>
  <f:subview id="userGroupsDefinitionSubview">
    <tiles:insert beanName="userGroupsDefinition" flush="false" />
  </f:subview>

</f:view>