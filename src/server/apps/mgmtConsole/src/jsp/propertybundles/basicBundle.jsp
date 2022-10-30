<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<%-- Load Resource Bundles --%>
<f:loadBundle basename="MgmtConsoleMessages" var="bundle" />
<%
	String installMode = System.getProperty("console.install.mode", "OPL");
	if ("OPL".equals(installMode)) {
%>
<f:loadBundle basename="HelpLocations_opl" var="helpBundle" />
<%
	} else if ("OPN".equals(installMode)) {
%>
<f:loadBundle basename="HelpLocations_opn" var="helpBundle" />
<%
	} else {
%>
<f:loadBundle basename="HelpLocations" var="helpBundle" />
<%
	}
%>
