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
    <%--<link rel="stylesheet" href="/resources/styles/profile.css">--%>
    <%--<script src="/resources/scripts/profile.js"></script>--%>
    <title>Profile</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>

<section class="profilePageContent">
    <%--<div class="container">--%>
    <%--<!-- Nav pills -->--%>
    <%--<div class="row">--%>
    <%--<div class="col-md-3">--%>
    <%--<div class="container profile">--%>
    <%--<div class="row">--%>
    <%--<div class="span12">--%>
    <%--<div class="card bg-light card-body mb-3 card bg-faded p-1 mb-3all clearfix">--%>
    <%--<div class="row-fluid">--%>
    <%--<div class="span2 text-center">--%>
    <%--<c:if test="${ null == user.image}">--%>
    <%--<img src="/resources/images/user2.png"--%>
    <%--class="rounded-circle userProfile">--%>
    <%--</c:if>--%>
    <%--<c:if test="${null != user.image}">--%>
    <%--<img src="data:image/jpg;base64,${user.encodedImage}"--%>
    <%--class="rounded-circle userProfile">--%>
    <%--</c:if>--%>
    <%--</div>--%>
    <%--<div class="span4">--%>
    <%--<h2>${user.username}</h2>--%>
    <%--<ul class="unstyled">--%>
    <%--<li><i class="icon-phone"></i> 916-241-3613</li>--%>
    <%--<li><i class="icon-envelope"></i> jonniespratley@me.com</li>--%>
    <%--<li><i class="icon-globe"></i> http://jonniespratley.me</li>--%>
    <%--</ul>--%>
    <%--</div>--%>
    <%--<div class="span6">--%>
    <%--<ul class="inline stats">--%>
    <%--<li><span>275</span>--%>
    <%--Deals--%>
    <%--</li>--%>
    <%--<li><span>354</span>--%>
    <%--Followers--%>
    <%--</li>--%>
    <%--<li><span>186</span>--%>
    <%--Photos--%>
    <%--</li>--%>
    <%--</ul>--%>
    <%--<div>--%>
    <%--<!--/span6-->--%>
    <%--</div>--%>
    <%--<!--/row-->--%>
    <%--</div>--%>
    <%--<!--Body content-->--%>
    <%--</div>--%>
    <%--</div>--%>
    <%--</div>--%>
    <%--</div>--%>
    <%--</div>--%>
    <%--</div>--%>
    <%--</div>--%>
    <%--</div>--%>
    <div class="container">
        <div class="row">
            <!-- Nav tabs -->
            <div class="col-md-3 profile-menu">
                <ul class="nav nav-tabs flex-column" role="tablist">
                    <li class="nav-item">
                        <a class="nav-link active" data-toggle="tab" href="#profileContent"><i class="fas fa-user"></i>
                            Profile</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" data-toggle="tab" href="#addingMerch"><i class="fas fa-plus"></i> Add
                            merchandise</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" data-toggle="tab" href="#logout"><i class="fas fa-sign-out-alt"></i> Logout</a>
                    </li>
                </ul>
            </div>

            <!-- Tab panes -->
            <div class="col-md-9">
                <div class="tab-content">
                    <div id="profileContent" class="container tab-pane active"><br>
                        <div class="panel-group">
                            <div class="card">
                                <div class="card-header">Username</div>
                                <div class="card-body">${user.username}</div>
                            </div>
                            <div class="card">
                                <div class="card-header">Balance</div>
                                <div class="card-body">${user.balance}</div>
                            </div>
                            <div class="card panel-dark">
                                <div class="card-header">Last Deals</div>
                                <div class="card-body">
                                    <c:choose>
                                        <c:when test="${myDeals.size()!=0}">
                                            <div id="accordion" id="deals-accordion">
                                                <c:forEach items="${myDeals}" var="item">
                                                    <div class="card">
                                                        <div class="card-header collapsed" data-toggle="collapse"
                                                             href="#collapse${item.id}">
                                                            <a class="card-title">
                                                                Deal #${item.id}
                                                            </a>
                                                        </div>
                                                        <div id="collapse${item.id}" class="collapse card-body"
                                                             data-parent="#deals-accordion">
                                                                <pre>
                                                                    <table class="table table-hover">
                                                                        <tbody>
                                                                            <tr>
                                                                                <td>id:</td>
                                                                                <td>${item.id}</td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td>Date:</td>
                                                                                <td>${item.time}</td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td>State:</td>
                                                                                <td>${item.state}</td>
                                                                            </tr>
                                                                            <c:set var="sym" value=""/>
                                                                            <c:set var="trCl" value=""/>
                                                                            <c:choose>
                                                                                <c:when test="${item.state == 'sold'}">
                                                                                    <c:set target="sym" value="+"/>
                                                                                    <c:set target="trCl"
                                                                                           value="success"/>
                                                                                </c:when>
                                                                                <c:when test="${item.state == 'bought'}">
                                                                                    <c:set target="sym" value="-"/>
                                                                                    <c:set target="trCl"
                                                                                           value="danger"/>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <c:set target="sym" value=""/>
                                                                                    <c:set target="trCl" value=""/>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                            <tr class="${trCl}">
                                                                                <td>Balance</td>
                                                                                <td>${sym}${item.price}</td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td>
                                                                                    Merchandise id
                                                                                </td>
                                                                                <td>
                                                                                        ${item.merchId}
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                    </table>
                                                                </pre>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            Nothing to show
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div id="addingMerch" class="container tab-pane fade"><br>
                        <h3>Menu 1</h3>
                        <p>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea
                            commodo consequat.</p>
                    </div>
                    <div id="menu2" class="container tab-pane fade"><br>
                        <h3>Menu 2</h3>
                        <p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque
                            laudantium, totam rem aperiam.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

</section>

<jsp:include page="/resources/templates/footer.jsp"/>

</body>
</html>
