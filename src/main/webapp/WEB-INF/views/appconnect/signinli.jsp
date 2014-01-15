<%@page import="com.dynamease.serviceproviders.config.Uris"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Sign In</title>
</head>
<body>
	<%-- 		<form action="<c:url value="<%=Uris.SPRINGFBSIGNIN%>" />" method="POST"> --%>
	<!-- 		    <button type="submit">Sign in with Facebook</button> -->
	<!-- 		    <input type="hidden" name="scope" value="email,publish_stream,offline_access" />		     -->
	<!-- 		</form> -->

	<form action="<c:url value="<%=Uris.SPRINGLISIGNIN%>" />" method="POST">
		<button type="submit">Sign in with LinkedIn</button>
		<input type="hidden" name="scope" value="r_fullprofile,r_network" />
	</form>
</body>
</html>
