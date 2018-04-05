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
        var form = $('.login');
        var uname = form.find('input[type="text"]').val();
        var pass = form.find('input[type="password"]').val();
        console.log(uname + "\t" + pass);
        $.post("/rest/methods/tokenUpdate", {username: uname, password: pass}, function (data, status) {
            document.location = "/loginRedirect?token=" + data;
            data.responseText;
        }).fail(function (data, status) {
            $.magnificPopup.open({
                items: {
                    src: '<div class="text-center white-popup">' +
                    '<h2>Error:</h2>' +
                    '<div class="popup-modal-text">' + data.responseText + '</div>' +
                    '<button class="btn btn-outline-primary popup-modal-dismiss">Close</button>' +
                    '</div>',
                    type: 'inline'
                },
                closeBtnInside: true
            });
            $(document).on('click', '.popup-modal-dismiss', function (e) {
                e.preventDefault();
                $.magnificPopup.close();
            });
        });
    });

    $('.token-reset-cancel').click(function (event) {
        $(".pr-wrap").removeClass("show-token-reset");
    });

    $('.register-btn').click(function (event) {
        var form = $('.login');
        var uname = form.find('input[type="text"]').val();
        var pass = form.find('input[type="password"]').val();
        var $modal;
        var $data = 'Data';
        $.post("/rest/methods/registerReq", {username: uname, password: pass}, function (data, status) {
            $.magnificPopup.open({
                items: {
                    src: '<div class="text-center white-popup">' +
                    '<h2>Info:</h2>' +
                    '<div class="popup-modal-text">' + data + '</div>' +
                    '<button class="btn btn-outline-primary popup-modal-dismiss">Close</button>' +
                    '</div>',
                    type: 'inline'
                },
                closeBtnInside: true
            });
            $(document).on('click', '.popup-modal-dismiss', function (e) {
                e.preventDefault();
                $.magnificPopup.close();
            });
        }).fail(function (data, status) {
            $.magnificPopup.open({
                items: {
                    src: '<div class="text-center white-popup">' +
                    '<h2>Error:</h2>' +
                    '<div class="popup-modal-text">' + data.responseText + '</div>' +
                    '<button class="btn btn-outline-primary popup-modal-dismiss">Close</button>' +
                    '</div>',
                    type: 'inline'
                },
                closeBtnInside: true
            });
            $(document).on('click', '.popup-modal-dismiss', function (e) {
                e.preventDefault();
                $.magnificPopup.close();
            });
        });


    });

    $('.sign-in-btn').click(function (event) {
        var form = $('.login');
        var uname = form.find('input[type="text"]').val();
        var pass = form.find('input[type="password"]').val();
        $.post("/rest/methods/loginReq", {username: uname, password: pass}, function (data, status) {
            document.location = "/loginRedirect?token=" + data;
        }).fail(function (data, status) {
            $.magnificPopup.open({
                items: {
                    src: '<div class="text-center white-popup">' +
                    '<h2>Error:</h2>' +
                    '<div class="popup-modal-text">' + data.responseText + '</div>' +
                    '<button class="btn btn-outline-primary popup-modal-dismiss">Close</button>' +
                    '</div>',
                    type: 'inline'
                },
                closeBtnInside: true
            });
            $(document).on('click', '.popup-modal-dismiss', function (e) {
                e.preventDefault();
                $.magnificPopup.close();
            });
        });
    });
});