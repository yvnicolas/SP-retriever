<%@page import="com.dynamease.serviceproviders.config.Uris"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Bye</title>
</head>
<body>
	Thank you for testing this sample!

	<form action="<c:url value="<%=Uris.MAIN%>" />">
		<button type="submit">start again</button>
	</form>
</body>
</html>
