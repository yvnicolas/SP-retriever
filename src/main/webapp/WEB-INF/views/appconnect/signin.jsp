<%@page import="com.dynamease.serviceproviders.config.Uris"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
	<head>
		<title>Sign In</title>
	</head>
	<body>
	Bonjour tout le monde!
	
		<form action="<c:url value="<%=Uris.APPLICATIONIDINPUT%>" />">
		<button type="submit">Connect to the application</button>
	</form>
			
	</body>
</html>
