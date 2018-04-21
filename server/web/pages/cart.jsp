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
  Date: 31.03.18
  Time: 19:14
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <jsp:include page="/resources/templates/inclues.jsp"/>
    <title>Cart</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>
<section class="cart_body">
    <div class="accordion">
        <c:forEach var="object" items="${cart}">
            <div>
                <c:forEach var="entery" items="${object.entrySet()}">
                    ${entery.getKey()}=${entery.getValue()}
                </c:forEach>
            </div>
            <br>
        </c:forEach>
    </div>
</section>

<jsp:include page="/resources/templates/footer.jsp"/>
</body>
</html>
