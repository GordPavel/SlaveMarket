<%--
  Created by IntelliJ IDEA.
  User: s3rius
  Date: 24.04.18
  Time: 18:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="/resources/templates/inclues.jsp"/>
    <link rel="stylesheet" href="/resources/styles/shop.css">
    <script src="/resources/scripts/shop.js"></script>
    <title>Slave Market</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>

<section class="content">
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-3 sidebar">
                <div class="mini-submenu">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </div>
                <div class="list-group">
                    <div href="#" class="list-group-item">
                        Main menu
                        <span id="slide-submenu" style="float: right;">
                            <i class="fa fa-times"></i>
                        </span>
                    </div>
                    <a class="list-group-item d-inline-block collapsed" href="#sub1" data-toggle="collapse"
                       aria-expanded="false">
                        <i class="fa fa-comment-o"></i> Sort by params
                    </a>
                    <div class="collapse" id="sub1" data-parent="#sidebar">
                        <a href="#sub_price" class="list-group-item sub_item collapsed"
                           data-toggle="collapse"
                           aria-expanded="false">Class</a>
                        <div id="sub_price" class="collapse">
                            <a href="javascript:void(0)" param="class" class="list-group-item sub_sub_item desc_sort">Desc</a>
                            <a href="javascript:void(0)" param="class" class="list-group-item sub_sub_item asc_sort ">Asc</a>
                        </div>
                        <a href="#sub_name" class="list-group-item sub_item collapsed"
                           data-toggle="collapse"
                           aria-expanded="false">Name</a>
                        <div id="sub_name" class="collapse">
                            <a href="javascript:void(0);" param="name" class="list-group-item sub_sub_item desc_sort">Desc</a>
                            <a href="javascript:void(0);" param="name" class="list-group-item sub_sub_item asc_sort ">Asc</a>
                        </div>
                        <a href="#sub_benefit" class="list-group-item sub_item collapsed"
                           data-toggle="collapse"
                           aria-expanded="false">Benefit</a>
                        <div id="sub_benefit" class="collapse">
                            <a href="javascript:void(0);" param="benefit" class="list-group-item sub_sub_item desc_sort">Desc</a>
                            <a href="javascript:void(0);" param="benefit" class="list-group-item sub_sub_item asc_sort ">Asc</a>
                        </div>
                    </div>
                    <a class="list-group-item d-inline-block collapsed" href="#sub2" data-toggle="collapse"
                       aria-expanded="false">
                        <i class="fa fa-comment-o"></i> Displayable classes
                    </a>
                    <div class="collapse" id="sub2" data-parent="#sidebar">
                        <div id="availableClasses">

                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-9">
                <div id="main-shop" class="row merchandises">

                </div>
                <div class="row">
                    <button class="merch_ctrl" id="load_more" style="width: 100%;">Load more</button>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>
