<%--
  ~ Copyright (c) 2018.
  ~ You may use, distribute and modify this code
  ~ under the terms of the TOT (take on trust) public license which unfortunately won't be
  ~ written for another century. So you can modify and copy this files.
  ~
  --%>

<%--
  Created by IntelliJ IDEA.
  User: s3rius
  Date: 03.04.18
  Time: 17:04
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <jsp:include page="/resources/templates/inclues.jsp"/>
    <link rel="stylesheet" href="/resources/styles/profile.css">
    <title>Profile</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>
<h2>${user.username}</h2>
<h2>${home}</h2>
<jsp:include page="/resources/templates/footer.jsp"/>

</body>
</html>
