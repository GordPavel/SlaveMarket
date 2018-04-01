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
    <title>Login</title>
</head>
<body>
<%@include file="/resources/templates/header.jsp" %>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="pr-wrap">
                <div class="pass-reset">
                    <label>Enter your login and password one more time to reset token.</label>
                    <input type="text" placeholder="Username"/>
                    <input type="password" placeholder="Password" style="margin-top: 10px;"/>
                    <input type="submit" value="Submit" class="pass-reset-submit btn btn-success btn-sm"/>
                    <button class="pass-reset-cancel btn btn-success btn-sm">Cancel</button>
                </div>
            </div>
            <div class="wrap">
                <p class="form-title">Sign In</p>
                <form class="login">
                    <input type="text" placeholder="Username"/>
                    <input type="password" placeholder="Password" style="margin-top: 10px;"/>
                    <input type="submit" value="Sign In" class="btn btn-outline-primary"/>
                    <div class="remember-forgot">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox"/>
                                        Remember Me
                                    </label>
                                </div>
                            </div>
                            <div class="col-md-6 forgot-pass-content">
                                <a href="javascript:void(0);" class="forgot-pass">Update Token</a>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%--<section class="footerSection">--%>
    <%--<%@include file="/resources/templates/footer.jsp" %>--%>
<%--</section>--%>
</body>
</html>
