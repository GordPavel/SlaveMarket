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

<section class="newsSection">
    <div class="container-fluid">
        <div class="row">
            <div class="col-lg-8">
                <div class="panel panel-default">
                    <div class="panel-heading"><i class="fa fa-link"></i> Links</div>
                    <div class="panel-body">
                        <div class="info-items-row row">
                            <div class="info-item col-lg-6">
                                <a href="/info/about">
                                    <span>About Us</span>
                                    <img src="/resources/images/about-us.jpg"/>
                                </a>
                            </div>
                            <div class="info-item col-lg-6">
                                <a href="/info/contacts">
                                    <span>Contacts</span>
                                    <img src="/resources/images/contacts.jpg"/>
                                </a>
                            </div>
                        </div>
                        <div class="info-items-row row">
                            <div class="info-item col-lg-4">
                                <a href="/info/quest">
                                    <span>Questions and Answers</span>
                                    <img src="/resources/images/qanda.jpg"/>
                                </a>
                            </div>
                            <div class="info-item col-lg-4">
                                <a href="/info/dev">
                                    <span>Developers</span>
                                    <img src="/resources/images/dev.jpg"/>
                                </a>
                            </div>
                            <div class="info-item col-lg-4">
                                <a href="/info/coop">
                                    <span>Cooperation with us</span>
                                    <img src="/resources/images/coop.jpg"/>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="sidebar col-lg-4">
                <div class="lastNews">
                    <div class="lastNews-title heading-sm">
                        <i class="fa fa-newspaper"></i> Last news
                    </div>
                    <div class="lastNews">
                        <ul class="newsList list-group">
                            <c:forEach var="index" begin="0" end="3" step="1">
                                <c:if test="${news.size() > index}">
                                    <li class="list-group-item">
                                        <div class="col-sm-5">
                                            <a href="/news/${news.get(index).id}" class="pull-left">
                                                <img src="data:image/jpg;base64,${news.get(index).base64EncodedImg}"
                                                     class="img-rounded pull-right">
                                            </a>
                                        </div>
                                        <div class="col-sm-7">
                                            <h5>${news.get(index).header}</h5>
                                            <h4>
                                                <small class="text-muted">
                                                    <a href="/news/${news.get(index).id}" class="text-muted linkToNews">Read
                                                        More<i class="fa fa-arrow-right"></i></a>
                                                </small>
                                            </h4>
                                        </div>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ul>

                    </div>
                </div>
                <div class="lastNews-footer heading-sm text-center">
                    <a href="/news">
                        See all news <i class="fa fa-arrow-right"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/resources/templates/footer.jsp"/>
</body>
</html>