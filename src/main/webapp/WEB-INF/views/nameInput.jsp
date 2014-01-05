<%@page import="com.dynamease.serviceproviders.config.Uris"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<title>Name Lookup</title>

</head>
<body>

	<p>personne à rechercher</p>

	<%-- Needs to add the main prefix here although I do not understand why --%>
	<form action=<%=Uris.URISPREFIX + Uris.NAMELOOKUP%> method="POST">

		<table>
			<tr>
				<td>First:</td>
				<td><input type='text' name="first" /></td>
			</tr>
			     <tr>
                <td>Last:</td>
                <td><input type='text' name="last" /></td>
            </tr>
			<tr>
				<td colspan='2'><input name="submit" type="submit"
					value="submit" /></td>
			</tr>
			<tr>
				<td colspan='2'><input name="reset" type="reset" /></td>
			</tr>
		</table>
	</form>
</body>
</html>
