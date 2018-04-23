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
    <link rel="icon" href="/resources/images/logo.png"/>
    <link rel="stylesheet" href="/resources/styles/not_found.css"/>
    <link rel="stylesheet" href="/resources/styles/footer.css">
    <link rel="stylesheet" href="/resources/styles/bootstrap.min.css">
    <title>Error</title>
    <style>
        @font-face {
            font-family: 'Montserrat';
            font-style: normal;
            font-weight: 400;
            src: local('Montserrat Regular'), local('Montserrat-Regular'), url(../resources/fonts/Montserrat-Font.ttf) format('truetype');
        }

        body {
            font-family: 'Montserrat', sans-serif;
        }

        .mar-top-50 {
            position: absolute;
            top: 50%;
            left: 0;
            right: 0;
            border: 3px solid #215fad;
            margin-top: inherit;
            font-size: 25pt;
        }
    </style>
</head>
<body>
<div class="container-fluid mar-top-50">
    <div class="h-75 row text-center bg-light">
        <div class="col text-center" style="font-size: 25pt;">
            <h1 class="bg-light">${errorHead}</h1>
            <p class="message-text">${errorMsg}</p>
        </div>
    </div>
</div>
<jsp:include page="/resources/templates/footer.jsp"/>
</body>
</html>
