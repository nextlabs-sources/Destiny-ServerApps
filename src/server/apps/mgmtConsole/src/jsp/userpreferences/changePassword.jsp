<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define Available Users Browse View Tiles Definition --%>
  <tiles:definition id="changePasswordDefinition" extends="mgmtConsoleMainDefinition">
    <tiles:put name="applicationTitle" value="${bundle.mgmt_console_title}" />
    <tiles:put name="pageTitle" value="${bundle.user_change_password_page_title}" />
    <tiles:put name="content" value="/WEB-INF/jspf/core/tiles/user/changePasswordContent.jspf"/>
	<tiles:put name="helpURL"><h:outputText value="#{helpBundle.administrator_help_url}" /></tiles:put>
  </tiles:definition>
  
  <%-- Insert Agent Config Desktop Definition --%>
  <f:subview id="changePasswordDefinitionSubview">
    <tiles:insert beanName="changePasswordDefinition" flush="false" />
  </f:subview>    
    
</f:view>
