<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.dynamease.serviceproviders.config.Uris"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Home</title>
</head>
<body>
	<h4>Vous êtes connecté sous l'Id ${currentUser.getId() }</h4>
	<form action="<%=Uris.SIGNOUT%>" method="POST">
		<button type="submit">Se déconnecter</button>
	</form>


	<h4>Statut des connecteurs Service Providers</h4>


	<table style="text-align: center">
		<thead style="text-align: center">
			<tr>
				<th width="33%" style="text-align: center">Connecteur</th>
				<th width="33%" style="text-align: center">Connexion</th>
				<th width="33%" style="text-align: center">Selection</th>
			</tr>
		</thead>
		<c:forEach items="${serviceProviders}" var="sp">
			<tr>
				<td><c:out value="${sp.name}" /></td>
				<c:if test="${sp.connected}">
					<td><form action="<c:url value="<%=Uris.DISCONNECT%>" />"
							method="POST">
							<button type="submit">Disconnect</button>
							<input type="hidden" name="sp" value="${sp.name}" />
						</form></td>
				</c:if>
				<c:if test="${!sp.connected}">
					<td>
						<form action="<c:url value="${sp.URL }" />" method="POST">
							<button type="submit">Connect</button>
							<input type="hidden" name="scope" value="${sp.permissions}" />
						</form>
					</td>
				</c:if>
				<c:if test="${sp.selected}">
					<td><form action="<%=Uris.SELECT%>" method="POST">
							<button type="submit">Unselect</button>
							<input type="hidden" name="sp" value="${sp.name}" />
						</form></td>
				</c:if>
				<c:if test="${!sp.selected}">

					<td>
						<form action="<%=Uris.SELECT%>" method="POST">
							<button type="submit">select</button>
							<input type="hidden" name="sp" value="${sp.name}" />
						</form>
					</td>
				</c:if>
		</c:forEach>

	</table>

	<h4>Action Menu</h4>
	<br>
	<form action="<c:url value="<%=Uris.NAMELOOKUP%>" />" method="GET">
		<button type="submit">Chercher un nom</button>

	</form>
	   <br>
    <form action="<c:url value="<%=Uris.BYE%>" />" method="GET">
        <button type="submit">Traiter un Fichier</button>

    </form>
        <br>
    <form action="<c:url value="<%=Uris.BYE%>" />" method="GET">
        <button type="submit">Enregistrer mes contacts</button>
    </form>
</body>
</html>