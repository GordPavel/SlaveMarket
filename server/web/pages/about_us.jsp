<%--
  Created by IntelliJ IDEA.
  User: s3rius
  Date: 22.04.18
  Time: 12:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="/resources/templates/inclues.jsp"/>
    <link rel="stylesheet" href="/resources/styles/about_us.css">
    <title>About us</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>
<section class="intro bg-light">
    <div class="container-fluid main-title">
        <div class="row row justify-content-md-center">
            <div class="col-md-10 text-center">

                <h3 class="font-3">Introducing to Slave Market</h3>

                <div class="intro-body">
                    <h3>
                        Renewed sight on slavery.
                    </h3>
                    <p class="text-center align-content-center intro-paragraph">
                        Our site made for people who needs something more than just a personal assistant.
                        At our site people can sell or buy something, that usually markets won't buy.
                        That site will try to help all the man who want to find something brand new.
                    </p>
                </div>
            </div>
        </div>
    </div>
</section>
<section class="intro-image">

</section>

<section class="info-cards">
    <div class="container">
        <div class="row">

            <div class="card p-3 col-12 col-md-6 col-lg-4">
                <div class="card-img pb-3">
                    <i class="far fa-money-bill-alt fa-10x"></i>
                </div>
                <div class="card-box">
                    <h4 class="card-title py-3">
                        Independent currency</h4>
                    <p>
                        To preserve the anonymity of users, the system uses its own currency.Money inputs and outputs
                        are made only in the form of merchandises.
                    </p>
                </div>
            </div>

            <div class="card p-3 col-12 col-md-6 col-lg-4">
                <div class="card-img pb-3">
                    <i class="fas fa-globe fa-10x"></i>
                </div>
                <div class="card-box">
                    <h4 class="card-title py-3">
                        Worldwide</h4>
                    <p>
                        In our store you can find products not only from all over the earth, but from the entire galaxy.
                    </p>
                </div>
            </div>

            <div class="card p-3 col-12 col-md-6 col-lg-4">
                <div class="card-img pb-3">
                    <i class="far fa-smile fa-10x"></i>
                </div>
                <div class="card-box">
                    <h4 class="card-title py-3">
                        Beautiful Design
                    </h4>
                    <p>
                        100% of users of the site respond positively to the design and usability of the site.
                    </p>
                </div>
            </div>


        </div>

    </div>

</section>
</body>
</html>
