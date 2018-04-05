<!DOCTYPE html>
<html lang="en">

<head>

    <jsp:include page="/resources/templates/inclues.jsp"/>

    <title>${news.header}</title>

    <link href='https://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic' rel='stylesheet'
          type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800'
          rel='stylesheet' type='text/css'>

    <!-- Custom styles for this template -->
    <link rel="stylesheet" href="/resources/styles/news.css"/>

</head>

<body>

<%@include file="/resources/templates/header.jsp" %>


<!-- Page Header -->
<header class="masthead image"
        style="background: transparent url('data:image/jpg;base64,${news.base64EncodedImg} ') 0 0 no-repeat fixed">
    <div class="overlay"></div>
    <div class="container">
        <div class="row">
            <div class="col-lg-8 col-md-10 mx-auto">
                <div class="site-heading">
                    <h1>${news.header}</h1>
                    <span class="subheading">${news.description}</span>
                </div>
            </div>
        </div>
    </div>
</header>

<!-- Main Content -->
<div class="container">
    <div class="row">
        <div class="col-lg-8 col-md-10 mx-auto">
            <div class="post-preview">
                <%--<a href="post.html">--%>
                <%--<h2 class="post-title">--%>
                <%--Man must explore, and this is exploration at its greatest--%>
                <%--</h2>--%>
                <%--<h3 class="post-subtitle">--%>
                <%--Problems look mighty small from 150 miles up--%>
                <%--</h3>--%>
                <%--</a>--%>
                <%--<p class="post-meta">Posted by--%>
                <%--<a href="#">Start Bootstrap</a>--%>
                <%--on September 24, 2018</p>--%>
                ${news.text}
            </div>
            <%--<hr>--%>
            <%--<div class="post-preview">--%>
            <%--<a href="post.html">--%>
            <%--<h2 class="post-title">--%>
            <%--I believe every human has a finite number of heartbeats. I don't intend to waste any of mine.--%>
            <%--</h2>--%>
            <%--</a>--%>
            <%--<p class="post-meta">Posted by--%>
            <%--<a href="#">Start Bootstrap</a>--%>
            <%--on September 18, 2018</p>--%>
            <%--</div>--%>
            <%--<hr>--%>
            <%--<div class="post-preview">--%>
            <%--<a href="post.html">--%>
            <%--<h2 class="post-title">--%>
            <%--Science has not yet mastered prophecy--%>
            <%--</h2>--%>
            <%--<h3 class="post-subtitle">--%>
            <%--We predict too much for the next year and yet far too little for the next ten.--%>
            <%--</h3>--%>
            <%--</a>--%>
            <%--<p class="post-meta">Posted by--%>
            <%--<a href="#">Start Bootstrap</a>--%>
            <%--on August 24, 2018</p>--%>
            <%--</div>--%>
            <%--<hr>--%>
            <%--<div class="post-preview">--%>
            <%--<a href="post.html">--%>
            <%--<h2 class="post-title">--%>
            <%--Failure is not an option--%>
            <%--</h2>--%>
            <%--<h3 class="post-subtitle">--%>
            <%--Many say exploration is part of our destiny, but it’s actually our duty to future generations.--%>
            <%--</h3>--%>
            <%--</a>--%>
            <%--<p class="post-meta">Posted by--%>
            <%--<a href="#">Start Bootstrap</a>--%>
            <%--on July 8, 2018</p>--%>
            <%--</div>--%>
            <hr>
            <!-- Pager -->
            <div class="clearfix">
                <a class="btn btn-primary float-right" href="/news/">See other news <i
                        class="fa fa-arrow-right"></i></a>
            </div>
        </div>
    </div>
</div>

<hr>

<jsp:include page="/resources/templates/footer.jsp"/>


</body>

</html>