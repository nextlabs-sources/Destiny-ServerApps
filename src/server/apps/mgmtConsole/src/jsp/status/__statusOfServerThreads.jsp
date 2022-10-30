<%@ taglib prefix="j" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="serverThreads"
	class="com.bluejungle.destiny.mgmtconsole.status.ThreadDumpManager" 
	scope="request" />
<HTML>
<BODY>
<h2>Stack Trace of JVM taken at <%= java.util.Calendar.getInstance().getTime() %></h2>
<j:forEach items="${serverThreads.stackTraces}" var="thisTrace">
	<h4><a name="${thisTrace.key.id}">${thisTrace.key}</a></h4>
	    <j:forEach items="${thisTrace.value}" var="lineOfTrace">
          at ${lineOfTrace}<br/>
        </j:forEach>     
</j:forEach>
</BODY>
</HTML>