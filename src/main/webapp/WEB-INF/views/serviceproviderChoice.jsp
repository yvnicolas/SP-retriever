<%@page import="com.dynamease.serviceproviders.config.Uris"%>

<nav class="top-bar">


	<section class="top-bar-section">
		<ul class="center">
			<li class="has-form">
				<form action="<c:url value="<%=Uris.SPCHOICE%>" />" method="POST">
					<button type="submit">View LinkedIn</button>
					<input type="hidden" name="sp" value="LINKEDIN" />
				</form>
			</li>
			<li class="has-form">
                <form action="<c:url value="<%=Uris.SPCHOICE%>" />" method="POST">
                    <button type="submit">View Facebook</button>
                    <input type="hidden" name="sp" value="FACEBOOK" />
                </form>
            </li>
		</ul>
	</section>
</nav>
