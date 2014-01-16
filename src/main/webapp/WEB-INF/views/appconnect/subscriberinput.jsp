
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.dynamease.serviceproviders.config.Uris"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Signin</title>

</head>
<body>

	<p>Please Signin - Signup (no difference at this stage)</p>

	<%-- Needs to add the main prefix here although I do not understand why --%>
	<form action=<%=Uris.URISPREFIX + Uris.IDPROCESS%> method="POST">

		<table>
			<tr>
				<td>Id:</td>
				<td><input type='text' name="id" /></td>
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
