<%@page import="com.dynamease.serviceproviders.config.Uris"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Ca Marche</title>
</head>
<body>
	Connexion avec facebook etablie avec succes
	<form action="<c:url value="<%=Uris.MAIN %>" />">
		<button type="submit">continue</button>
	</form>
</body>
</html>
