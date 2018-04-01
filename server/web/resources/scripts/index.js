/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

$(document).ready(function () {
    $("#owl-slider").owlCarousel({
        navigation: false,
        loop: true,
        dots: true,
        // dotsContainer: '#main-dot-container',
        slideSpeed: 300,
        autoplay: true,
        autoplayTimeout: 4500,
        paginationSpeed: 500,
        items: 1,
        autoHeight: true
    });
});