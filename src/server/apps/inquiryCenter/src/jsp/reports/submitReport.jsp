<%-- @ page import="com.nextlabs.destiny.inquirycenter.report.defaultimpl.BirtReportTransform,
                 com.bluejungle.destiny.inquirycenter.report.defaultimpl.MyReportsPageBeanImpl,
                 java.lang.String" --%>
                 
<%--
MyReportsPageBeanImpl reportTransformBean = 
(MyReportsPageBeanImpl) request.getSession().getAttribute("myReportsBean");

com.bluejungle.destiny.inquirycenter.report.IReport selectedReport = reportTransformBean.getSelectedReport();

BirtReportTransform reportTransform = new BirtReportTransform(selectedReport);
--%>

<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
	<body onload="document.reportform.submit();">
		<form name="reportform" method="post" action="/reporter/frameset" target="_self">
			<input type="hidden" name="__showtitle" value="false"/>
			<input type="hidden" name="__format" value="HTML"/>
			<input type="hidden" name="__target" value="_self"/>
			<input type="hidden" name="__masterpage" value="false"/>
			<input type="hidden" name="__parameterpage" value="false"/>
			<h:inputHidden id="__report" value="#{submitReportBean.reportTransform.reportName}"/>
			<h:inputHidden id="UserName" value="#{submitReportBean.reportTransform.users}"/>
			<h:inputHidden id="Action" value="#{submitReportBean.reportTransform.actions}"/>
			<h:inputHidden id="Resource" value="#{submitReportBean.reportTransform.resources}"/>
			<h:inputHidden id="Policy" value="#{submitReportBean.reportTransform.policies}"/>
			<h:inputHidden id="Enforcement" value="#{submitReportBean.reportTransform.enforcements}"/>
			<h:inputHidden id="EventLevel" value="#{submitReportBean.reportTransform.eventLevel}"/>
			<h:inputHidden id="GroupByDimension" value="#{submitReportBean.reportTransform.groupBy}"/>
			<h:inputHidden id="Host" value="#{submitReportBean.reportTransform.host}"/>
			<h:inputHidden id="BeginDate" value="#{submitReportBean.reportTransform.beginDate}"/>
			<h:inputHidden id="EndDate" value="#{submitReportBean.reportTransform.endDate}"/>
			<h:inputHidden id="DBDriver" value="#{submitReportBean.reportTransform.driver}"/>
			<h:inputHidden id="DBURL" value="#{submitReportBean.reportTransform.URL}"/>
			<h:inputHidden id="DBUserid" value="#{submitReportBean.reportTransform.username}"/>
			<h:inputHidden id="DBPassword" value="#{submitReportBean.reportTransform.password}"/>
			<h:inputHidden id="TargetData" value="#{submitReportBean.reportTransform.policyGroupBy}"/>
			<h:inputHidden id="Title" value="#{submitReportBean.reportTransform.title}"/>
			<h:inputHidden id="TimeDimension" value=""/>
		</form>
	</body>
</f:view>