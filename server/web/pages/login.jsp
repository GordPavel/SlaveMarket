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
  Date: 01.04.18
  Time: 3:51
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <jsp:include page="/resources/templates/inclues.jsp"/>
    <link rel="stylesheet" href="/resources/styles/login.css">
    <script src="/resources/scripts/login.js"></script>
    <script src="/resources/scripts/jquery.magnific-popup.min.js"></script>
    <link rel="stylesheet" href="/resources/styles/magnific-popup.css">
    <title>Login</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>
<section class="loginForm">
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <div class="pr-wrap">
                    <div class="token-reset">
                        <label>Enter your login and password one more time to reset token.</label>
                        <input type="text" placeholder="Username"/>
                        <input type="password" placeholder="Password" style="margin-top: 10px;"/>
                        <input type="submit" value="Submit" class="token-reset-submit btn btn-primary btn-sm"/>
                        <button class="token-reset-cancel btn btn-primary btn-sm">Cancel</button>
                    </div>
                </div>
                <div class="wrap">
                    <p class="form-title">Sign In</p>
                    <form class="login" action="javascript:void(0);">
                        <input type="text" placeholder="Username"/>
                        <input type="password" placeholder="Password" style="margin-top: 10px;"/>
                        <input type="submit" value="Sign In" class="btn btn-primary sign-in-btn"/>
                        <input type="submit" value="Register" class="btn btn-primary register-btn"/>
                        <div class="remember-forgot">
                            <%--<div class="row">--%>
                            <%--<div class="col-md-6">--%>
                            <%--<div class="checkbox">--%>
                            <%--<label>--%>
                            <%--<input type="checkbox"/>--%>
                            <%--Remember Me--%>
                            <%--</label>--%>
                            <%--</div>--%>
                            <%--</div>--%>
                            <div class="text-center forgot-pass-content">
                                <a href="javascript:void(0);" class="forgot-token">Update Token</a>
                            </div>
                            <%--</div>--%>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div id="test-modal" class="white-popup-block mfp-hide">
        <h1>Modal dialog</h1>
        <p>You won't be able to dismiss this by usual means (escape or
            click button), but you can close it programatically based on
            user choices or actions.</p>
        <p><a class="popup-modal-dismiss" href="#" style="">Dismiss</a></p>
    </div>
</section>
<%--<section class="footerSection">--%>
<%--<%@include file="/resources/templates/footer.jsp" %>--%>
<%--</section>--%>
<%--<jsp:include page="/resources/templates/footer.jsp"/>--%>

</body>
</html>
