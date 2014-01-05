<%@page import="com.dynamease.serviceproviders.config.Uris"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Home</title>
</head>
<body>
	<ul>
		<li><a href="<c:url value="<%=Uris.MAIN%>" />"> Back to Home</a></li>
		<%-- 		<li><a href="<c:url value="<%=Uris.PARTIALSIGNOUT%>" />"> --%>
		<!-- 				Keep Connection to FB</a></li> -->
	</ul>




	<h3>
		Recherche sur
		<c:out value="${name}" />
	</h3>

		<c:forEach items="${results}" var="sp">
			<c:if test="${sp.info.connected}">
				<h4>
					<c:out value="${sp.info.name}" />
				</h4>
				<table>
					<c:forEach items="${sp.listInfo}" var="person">
						<tr>
							<c:out value="${person.info }" />
						</tr>
					</c:forEach>
				</table>
			</c:if>

		</c:forEach>

</body>
</html>