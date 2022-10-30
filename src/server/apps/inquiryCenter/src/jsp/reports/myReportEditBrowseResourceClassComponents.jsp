<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define Browsable User-Component Tiles Definition --%>
  <tiles:definition id="myReportEditBrowseResourceClassComponentsDefinition" extends="inquiryCenterBrowseDataPickerDefinition">
    <tiles:put name="applicationTitle" value="${bundle.inquiry_center_title}" />
    <tiles:put name="pageTitle">
      <h:outputFormat value="#{bundle.my_report_edit_browse_resource_class_components_title}" >
        <f:param id="RptTtl" value="#{myReportsBean.selectedReport.title}" />
      </h:outputFormat>
    </tiles:put>
    <tiles:put name="selectedItemBoxLabel">
      <h:outputText id="slctItmBxLblTxt" value="#{bundle.my_report_edit_browse_resource_class_components_selected_items_box_header}" />:
    </tiles:put>
	<tiles:put name="helpURL"><h:outputText value="#{helpBundle.reportcreation_help_url}" /></tiles:put>
  </tiles:definition>
  
  <%-- Set styles for selectable items --%>
  <c:set var="boldStyleClassId" value="selectableItemGroup" scope="request"/>
  <c:set var="defaultStyleClassId" value="selectableItem" scope="request" />
  <c:set var="disabledStyleClassId" value="selectableItemAlreadySelected" scope="request" />
  
  <%-- Insert Browse User Components Definition --%>
  <f:subview id="myReportEditBrowseResourceClassComponentsDefinitionSubview">
    	<tiles:insert beanName="myReportEditBrowseResourceClassComponentsDefinition" flush="false" />
  </f:subview>    
</f:view>