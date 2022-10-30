<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles-1.1" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Note that in order to function properly, this page cannot contain JSF tags --%>

<%-- Load Resource Bundles --%>
<fmt:setBundle basename="InquiryCenterMessages" var="bundle" />

<%-- Define Home Page Tiles Definition --%>
<tiles:definition id="errorDefinition" extends="inquiryCenterErrorDisplayDefinition">
  <tiles:put name="applicationTitle">
    <fmt:message key="inquiry_center_title" var="bundle" />
  </tiles:put>
</tiles:definition>

<%-- Insert Home Definition --%>
<tiles:insert beanName="errorDefinition" flush="false" />