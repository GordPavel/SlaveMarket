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
    <script src="/resources/scripts/cart.js"></script>
    <title>Cart</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>
<h1>Shopping Cart</h1>
<section class="cart">
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-12 col-md-offset-1">
                <table class="table table-hover borderless">
                    <thead>
                    <tr>
                        <th>Product</th>
                        <th class="text-center d-none d-none d-sm-table-cell">Class</th>
                        <th class="text-center">Price</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${cart}">
                        <tr class="merchandise" item_id="${item.id}" item_price="${item.price}"
                            item_status="${item.available}">
                            <td class="col-md-6">
                                <div class="media">
                                    <a class="thumbnail pull-left" href="#"> <img class="media-object"
                                                                                  src="data:image/jpg;base64,${item.image}"
                                                                                  style="width: 72px; height: 72px;">
                                    </a>
                                    <div class="media-body">
                                        <h4 class="media-heading"><a href="/merchandises/${item.id}">${item.name}</a>
                                        </h4>
                                        <c:choose>
                                            <c:when test="${item.available}">
                                                <span>Status: </span><span
                                                    class="text-success"><strong>In Stock</strong></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span>Status: </span><span
                                                    class="text-danger"><strong>Not available</strong></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </td>
                            <td class="text-center d-none d-sm-table-cell">${item.className}</td>
                            <td class="text-center"><strong>${item.price}</strong><i
                                    class="fab fa-stripe-s"></i></td>
                            <td>
                                <button type="button" class="btn btn-danger eraser" merch_id="${item.id}">
                                    <i class="far fa-trash-alt" merch_id="${item.id}"></i> <span
                                        class="d-none d-md-inline" merch_id="${item.id}">Remove</span>
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="row justify-content-end ">
            <div>
                <table class="table">
                    <thead>
                    <tr>
                        <th scope="col"><h3>Total</h3></th>
                        <th scope="col" class="text-right"><h4><strong class="totals-cell">0</strong><i
                                class="fab fa-stripe-s"></i>
                        </h4></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>
                            <button type="button" onclick="location.href='/shop'" class="btn btn-default">
                                <i class="fa fa-shopping-cart"></i> <span
                                    class="d-none d-md-inline">Continue Shopping</span>
                            </button>
                        </td>
                        <td class="text-right">
                            <button type="button" class="btn btn-success"
                                    onclick="checkout();">
                                <span class="d-none d-md-inline">Checkout </span><i class="fa fa-play"></i>
                            </button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>
</body>
</html>
