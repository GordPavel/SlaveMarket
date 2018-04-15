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
    <link rel="stylesheet" href="/resources/styles/brutusin-json-forms.min.css">
    <script src="/resources/scripts/profile.js"></script>
    <script src="/resources/scripts/brutusin-json-forms.min.js"></script>
    <script src="/resources/scripts/brutusin-json-forms-bootstrap.min.js"></script>
    <script src="/resources/scripts/jquery.magnific-popup.min.js"></script>
    <script src="/resources/scripts/jquery.twbsPagination.min.js"></script>
    <link rel="stylesheet" href="/resources/styles/magnific-popup.css">
    <title>Profile</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>

<section class="profilePageContent">
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
                        <a class="nav-link addMerchLink" data-toggle="tab" href="#addingMerch"><i
                                class="fas fa-plus"></i> Add
                            merchandise</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link my-deals-list"
                           onclick="openDeals('${user.username}', '${user.token}', 0);"
                           data-toggle="tab"
                           href="#allDeals"><i class="fas fa-list"></i>All deals</a>
                    </li>
                </ul>
            </div>
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
                                            <div id="deals-accordion">
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
                                                                            <c:choose>
                                                                                <c:when test="${item.state == 'sold'}">
                                                                                    <tr class="table-success">
                                                                                        <td>Price</td>
                                                                                        <td>${item.price}</td>
                                                                                    </tr>
                                                                                </c:when>
                                                                                <c:when test="${item.state == 'bought'}">
                                                                                    <tr class="table-danger">
                                                                                        <td>Price</td>
                                                                                        <td>${item.price}</td>
                                                                                    </tr>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <tr>
                                                                                        <td>Price:</td>
                                                                                        <td>${item.price}</td>
                                                                                    </tr>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                                <tr>

                                                                                    <td>Merchandise id</td>
                                                                                    <td><a href="/merchandise/${item.merchId}">${item.merchId}</a></td>
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
                        <h3 class="addingHeader">
                            Which type of merchandise you want to add?
                        </h3>
                        <div id="content-container">
                            <div id="form-container-main">
                                <div id='form-container'
                                     style="padding-left:12px;padding-right:12px;padding-bottom: 12px;"></div>
                                <div class="panel-footer">
                                    <button class="btn btn-primary"
                                            onclick="if (bf.validate()) {addMerch()}">Add merchandise
                                    </button>
                                </div>
                            </div>

                            <div class="list-group availableClasses">

                            </div>

                        </div>
                    </div>
                    <div id="allDeals" class="container tab-pane fade"><br>
                        <h3>My Deals</h3>
                        <div class="my-deals-list">
                            <div id="accordion">

                            </div>
                            <div id="my-deals-footer">
                                <nav aria-label="Page navigation">
                                    <ul class="pagination" id="pagination"></ul>
                                </nav>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</section>

<%--<jsp:include page="/resources/templates/footer.jsp"/>--%>

</body>
</html>
