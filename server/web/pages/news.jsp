<!DOCTYPE html>
<html lang="en">

<head>

    <jsp:include page="/resources/templates/inclues.jsp"/>

    <title>${news.header}</title>

    <link href='https://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic' rel='stylesheet'
          type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800'
          rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/resources/styles/news.css"/>

</head>

<body>

<%@include file="/resources/templates/header.jsp" %>


<!-- Page Header -->
<header class="masthead image"
        style="background: transparent url('data:image/jpg;base64,${news.base64EncodedImg} ') 0 0 no-repeat fixed;background-size: cover;">
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

<div class="container">
    <div class="row">
        <div class="col-lg-8 col-md-10 mx-auto">
            <div class="post-preview">
                ${news.text}
            </div>
            <hr>
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