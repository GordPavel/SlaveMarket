<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="headerSection">
    <nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
        <div class="container-fluid">
            <a class="navbar-brand" rel="home" href="/" title="Buy Sell Rent Everyting">
                Slave market<img style="max-width:50px; margin-top: -7px;"
                                 src="/resources/images/logo.png">
            </a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/shop">Catalog</a>
                    </li>
                    <c:if test="${null == user}">
                        <li class="nav-item">
                            <a class="nav-link" href="/login">Profile</a>
                        </li>
                    </c:if>
                    <c:if test="${null!= user}">
                        <li class="dropdown nav-item">
                            <a class="dropdown-toggle nav-link" data-toggle="dropdown" href="#">Profile<span
                                    class="caret"></span></a>
                            <ul class="dropdown-menu">
                                <li><a class="nav-link" href="/profile/">My profile</a></li>
                                <li><a class="nav-link" href="/profile/deals">My deals</a></li>
                                <li><a class="nav-link" href="/logout">Log out</a></li>
                            </ul>
                        </li>
                        <c:set var="admin" value="admin"/>
                        <c:if test="${user.role==admin}">
                            <li class="nav-item">
                                <a class="nav-link" href="/adminPanel">Admin panel</a>
                            </li>
                        </c:if>
                    </c:if>
                    <li class="nav-item">
                        <c:if test="${cart.size()<1}">
                            <a class="nav-link" href="#" data-container="body" data-toggle="popover"
                               data-placement="bottom"
                               data-content="Your cart is empty">Cart <span
                                    class="badge badge-info">empty</span></a>
                        </c:if>
                        <c:if test="${cart.size()>0}">
                            <a class="nav-link" href="/cart">Cart <span
                                    class="badge badge-info">${cart.size()}</span></a>
                        </c:if>
                    </li>
                </ul>
                <form class="form-inline my-2 my-lg-0" action="/search">
                    <input class="form-control mr-sm-2" type="search" name="query" placeholder="Search"
                           aria-label="Search">
                    <button class="btn btn-outline-primary my-2 my-sm-0" type="submit"><i class="fa fa-search"></i>
                    </button>
                </form>
            </div>
        </div>
    </nav>
</section>