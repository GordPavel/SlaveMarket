/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

$(document).ready(function () {
    $('.forgot-token').click(function (event) {
        $(".pr-wrap").toggleClass("show-token-reset");
    });

    $('.token-reset-submit').click(function (event) {
        var form = $('.token-reset');
        var uname = form.find('input[type="text"]').val();
        var pass = form.find('input[type="password"]').val();
        console.log(uname + "\t" + pass);
        $.post("/rest/methods/tokenUpdate", {username: uname, password: pass}, function (data, status) {
            document.location = "/loginRedirect?token=" + data;
            data.responseText;
        }).fail(function (data, status) {
            showPopup('Error:', data.responseText);
        });
    });

    $('.token-reset-cancel').click(function (event) {
        $(".pr-wrap").removeClass("show-token-reset");
    });

    $('.register-btn').click(function (event) {
        var form = $('.login');
        var uname = form.find('input[type="text"]').val();
        var pass = form.find('input[type="password"]').val();
        $.post("/rest/methods/registerReq", {username: uname, password: pass}, function (data, status) {
            showPopup('Info:', data);
        }).fail(function (data, status) {
            showPopup('Error:', data.responseText);
        });


    });

    $('.sign-in-btn').click(function (event) {
        var form = $('.login');
        var uname = form.find('input[type="text"]').val();
        var pass = form.find('input[type="password"]').val();
        $.post("/rest/methods/loginReq", {username: uname, password: pass}, function (data, status) {
            document.location = "/loginRedirect?token=" + data;
        }).fail(function (data, status) {
            showPopup('Error:', data.responseText);
        });
    });
});