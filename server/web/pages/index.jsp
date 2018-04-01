<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright (c) 2018.
  ~ You may use, distribute and modify this code
  ~ under the terms of the TOT (take on trust) public license which unfortunately won't be
  ~ written for another century. So you can modify and copy this files.
  ~
  --%>
<html>
<head>
    <jsp:include page="/resources/templates/inclues.jsp"/>
    <link rel="stylesheet" href="/resources/styles/index.css"/>
    <link rel="stylesheet" href="/resources/styles/owl.carousel.css">
    <link rel="stylesheet" href="/resources/styles/owl.theme.default.css">
    <script src="/resources/scripts/owl.carousel.min.js"></script>
    <script src="/resources/scripts/index.js"></script>
    <title>Slave market</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>
<section class="main-slider">
    <div id="owl-slider" class="owl-carousel">
        <c:forEach items="${news}" var="item">
            <c:if test="${item.slider==true}">
                <a href="/news/${item.id}">
                    <div class="owl-img"
                         style="background-image:url('data:image/jpg;base64,${item.base64EncodedImg}');"></div>
                    <div class="container">
                        <div class="owl-text-overlay">
                            <div class="owl-title">${item.header}</div>
                            <div class="owl-caption hidden-sm-down">${item.description}</div>
                        </div>
                    </div>
                </a>
            </c:if>
        </c:forEach>
    </div>
    <div id="main-dot-container" class="owl-dots">
    </div>
</section>
<%--<section class="newsSection">--%>
<%--<div class="lastNewsList">--%>
<%--<div class="container">--%>
<%--<div class="row lastNewsListHeader">--%>
<%--<div class="col-md-6 text-left">--%>
<%--News--%>
<%--</div>--%>
<%--<a class="col-md-6 text-right" href="/news">--%>
<%--See all news--%>
<%--</a>--%>
<%--</div>--%>
<%--<c:forEach var="i" begin="0" end="0" step="1">--%>
<%--<c:if test="${i<0}">--%>
<%--<div class="row">--%>
<%--<div class="col-md-6 news">--%>
<%--<div class="newsHeader text-center">--%>
<%--</div>--%>
<%--<div class="row">--%>
<%--<div class="newsImage">--%>
<%--<i class="fa fa-newspaper"></i>--%>
<%--</div>--%>
<%--<div class="newsDescription">--%>
<%--some descriptionsaiuefasieufnasleifnase asefnalseifn aseifun--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--<div class="col-md-6 news">--%>
<%--<div class="newsHeader text-center">--%>
<%--</div>--%>
<%--<div class="row">--%>
<%--<div class="newsImage">--%>
<%--<i class="fa fa-newspaper"></i>--%>
<%--</div>--%>
<%--<div class="newsDescription">--%>
<%--some descriptionsaiuefasieufnasleifnase asefnalseifn aseifun--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</c:if>--%>
<%--</c:forEach>--%>
<%--</div>--%>
<%--</section>--%>

<section class="content">
    <div class="container">
        <div class="panel panel-default">
            <div class="panel-heading"><i class="fa fa-newspaper"></i> News</div>
            <div class="panel-body">
                <div class="row">
                    <div class=" col-lg-8">
                        <c:forEach var="i" begin="0" end="2" step="2">
                        <c:if test="${i lt news.size() && i+1 lt news.size()}">
                        <div class="last-news-row row">
                            <div class="last-news col-lg-6">
                                <a href="/news/${news.get(i).id}">
                                    <span>${news.get(i).header}</span>
                                    <img src="data:image/jpg;base64,${news.get(i).base64EncodedImg}"/>
                                </a>
                            </div>
                            <div class="last-news col-lg-6">
                                <a href="/news/${news.get(i+1).id}">
                                    <span>${news.get(i+1).header}</span>
                                    <img src="data:image/jpg;base64,${news.get(i+1).base64EncodedImg}"/>
                                </a>
                            </div>
                            </c:if>
                            </c:forEach>
                        </div>
                        <div class="sidebar col-lg-4"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<%--<section class="footerSection">--%>
<%--<%@include file="/resources/templates/footer.jsp" %>--%>
<%--</section>--%>


</body>
</html>