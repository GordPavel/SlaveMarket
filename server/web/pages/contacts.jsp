<%--
  Created by IntelliJ IDEA.
  User: s3rius
  Date: 22.04.18
  Time: 11:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="/resources/templates/inclues.jsp"/>
    <title>Contacts</title>
    <style>
        .card {
            background-color: transparent;
        }

        .card-header {
            background: transparent;
            border: 0;
        }

        .map {
            height: 100%;
        }
    </style>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>
<section class="contacts bg-light">
    <div class="container">
        <div class="row">
            <div class="col-sm-6">
                <div class="well">
                    <div class="card">
                        <div class="card-header ">
                            <h3><i class="fa fa-home fa-1x"></i>
                                Address:</h3>
                        </div>
                        <div class="card-body text-center">
                            <h3>Samara, Russia</h3>
                        </div>
                    </div>
                    <br/>
                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fa fa-envelope fa-1x"></i>
                                E-Mail address:</h3>
                        </div>
                        <div class="card-body text-center">
                            <a href="mailto:win10@list.ru?Subject=Hello%20again" target="_top"><h3>win10@list.ru</h3>
                            </a>
                        </div>
                    </div>
                    <br/>
                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fa fa-user fa-1x"></i>
                                Responsible person:</h3>
                        </div>
                        <div class="card-body text-center">
                            <h3>Pavel Kirilin</h3>
                        </div>
                    </div>
                    <br/>
                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fas fa-phone"></i>Contact phone:</h3>
                        </div>
                        <div class="card-body text-center">
                            <a href="tel:8-999-171-60-67"><h3>8-999-171-60-67</h3></a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="map">
                    <iframe src="https://www.google.com/maps/embed/v1/place?key=AIzaSyA_OUWZEKqQmrFDtXIJwSI7Q00OHx13VRg&q=Samara,Russia"
                            width="100%" height="100%" frameborder="0" style="border:0" allowfullscreen>
                    </iframe>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>
