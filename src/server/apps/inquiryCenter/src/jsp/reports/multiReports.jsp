<%@ page errorPage="/error" %> 

<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://bluejungle.com/destiny/1.0" prefix="d"%>

<d:includeJS location="/core/javascript/jquery-1.10.2.js"/>

<f:view>
  <%@ include file = "../propertybundles/basicBundle.jsp" %>
  <%-- Define My Reports Tiles Definition --%>
  <tiles:definition id="multiReportsDefinition" extends="inquiryCenterMainWithoutContentHeaderDefinition">
    <tiles:put name="applicationTitle" value="${bundle.inquiry_center_title}" />
    <tiles:put name="pageTitle" value="${bundle.my_reports_page_title}" />
    <tiles:put name="secondaryNav" value="/WEB-INF/jspf/tiles/reports/reportSecondaryNav.jspf" />
    <tiles:put name="content" value="/WEB-INF/jspf/tiles/reports/multiReportContent.jspf" />
	<tiles:put name="helpURL"><h:outputText value="#{helpBundle.reportcreation_help_url}" /></tiles:put>
  </tiles:definition>

  <%-- Insert SharePoint Reports Definition --%>
  <f:subview id="multiReportsDefinitionSubview">
    <tiles:insert beanName="multiReportsDefinition" flush="false" />
  </f:subview>

</f:view>

<script type="text/javascript">
    $(document).ready(function() {
        $( "[id='multiReportsDefinitionSubview:primaryNavSubview:prmNavFrm:primaryNavMenu-myReports:li']" ).addClass("current1");
    });
</script>
