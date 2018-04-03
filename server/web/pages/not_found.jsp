<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: s3rius
  Date: 04.04.18
  Time: 1:54
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <jsp:include page="/resources/templates/inclues.jsp"/>
    <link rel="stylesheet" href="/resources/styles/not_found.css"/>
    <title>Not found</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>

<div class="http-error-container h-100 row align-items-center">
    <div class="col text-center" style="font-size: 25pt;">
        <h1>HTTP Status 404 - Page Not Found</h1>
        <p class="message-text">The page you requested is not available. You might try returning to the <a
                href="<c:url value="/"/>">home page</a>.</p>
    </div>
</div>
<jsp:include page="/resources/templates/footer.jsp"/>
</body>
</html>
