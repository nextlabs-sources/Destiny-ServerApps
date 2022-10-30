<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<f:view>
    <body onload="document.reportform.submit();">
        <form name="reportform" method="post" action="/reporter/frameset" target="_self">
            <input type="hidden" name="__showtitle" value="false"/>
            <input type="hidden" name="__format" value="HTML"/>
            <input type="hidden" name="__masterpage" value="false"/>
            <input type="hidden" name="__parameterpage" value="false"/>
             <input type="hidden" name="__report"
                       value="${sessionScope.customReportsBean.selectedReportDesignFileName}"/>
            <c:forEach var="inputParam" items="${sessionScope.customReportsBean.invokeReportParams}">
                <input type="hidden"  name="${inputParam.key}" value="${inputParam.value}"/>
             </c:forEach>
<f:verbatim>
    </div>
</f:verbatim>
</f:view>

