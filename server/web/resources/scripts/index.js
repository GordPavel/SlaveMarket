/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

$(document).ready(function () {
    wow = new WOW(
        {
            boxClass: 'wow',
            animateClass: 'animated',
            offset: 0,
            mobile: false,
            live: true
        }
    );
    wow.init();
    $("#owl-slider").owlCarousel({
        navigation: false,
        loop: true,
        mouseDrag: true,
        slideSpeed: 300,
        autoplay: true,
        autoplayTimeout: 4500,
        paginationSpeed: 500,
        items: 1,
        autoHeight: true
    });
    $('.merchandises .owl-carousel').owlCarousel({
        mouseDrag: false,
        nav: true,
        dots: false,
        margin: 30,
        navText: [
            '<i class="fa fa-angle-left" aria-hidden="true"></i>',
            '<i class="fa fa-angle-right" aria-hidden="true"></i>'
        ],
        slideSpeed: 300,
        autoplay: true,
        loop: false,
        autoplayTimeout: 6000,
        paginationSpeed: 500,
        responsive: {
            0: {
                items: 1
            },
            600: {
                items: 2
            },
            993: {
                items: 3
            },
            1200: {
                items: 4
            }
        }
    });
    $('.merchandises .item .control .add-card-btn').click(function (event) {
        var merchId = event.target.getAttribute("data-product_id");
        console.log(merchId);
        $.ajax({
            url: "/cart/",
            method: "POST",
            data: {id: merchId}
        }).done(function (data) {
            if (console && console.log) {
                console.log("Added in cart");
                $(event.target).addClass('disabled');
                $(event.target).text('Added');
                updateCart();
            }
        }).fail(function () {
            showPopup('Error', 'Can\'t add merchandise into cart');
        });
    });
});
