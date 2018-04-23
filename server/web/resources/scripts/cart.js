$(document).ready(function () {
    //Calculating totals
    var price = calculate();
    $(".totals-cell").text(price);

    //Remove item by clicking on remove
    $(".eraser").click(function (event) {
        var id = parseInt($(event.target).attr("merch_id"));
        $.post("/cart/remove", {id: id}, function (data, status) {
            if (JSON.parse(data).status === true) {
                $('.merchandise[item_id="' + id + '"]').hide().attr("item_status", "hidden");
                price = calculate();
                $(".totals-cell").text(price);
                if ($('.merchandise[item_status="true"]').length === 0) {
                    window.location.replace("/");
                }
            }
        }).fail(function (data, status) {
            showPopup('Error:', 'Can\'t remove merchandise');
        });
    });
});

function calculate() {
    var price = 0;
    $('.merchandise').each(function () {
        var item = $(this);
        if (item.attr("item_status") === "true") {
            price += parseInt(item.attr('item_price'));
        }
    });
    return price;
}

function checkout() {
    $.post("/cart/info", {type: "items"}, function (data) {
        var response = JSON.parse(data);
        var sales = response.items.filter(function (item) {
            return JSON.parse(item).state === "on sale";
        });
        if (sales.length === 0) {
            showPopupWithCallback('Info:', 'Nothing to buy. You will be redirected to home page.', function (e) {
                e.preventDefault();
                $.magnificPopup.close();
                checkout_post(false);
                window.location.replace("/");
            });
        } else {
            if (sales.length !== response.items.length) {
                showPopupWithCallback('Info:', 'Some of chosen merchandises unavailable. They will be skipped.', function (e) {
                    e.preventDefault();
                    $.magnificPopup.close();
                    checkout_post(true);
                });
            } else {
                checkout_post(true);
            }
        }
    }).fail(function (data) {
        showPopup('Error:', 'Cannot connect to server.')
    });
}

function checkout_post(flag) {
    $.post("/checkout", function (data, status) {
        if (flag) {
            showPopupWithCallback('Info:', 'Successfully bought', function (e) {
                e.preventDefault();
                $.magnificPopup.close();
                window.location.replace("/");
            });
        } else {
            window.location.replace("/");
        }
    }).fail(function (data, status) {
        var response = JSON.parse(data.responseText);
        showPopup('Error:', response.error + '\nCart was cleared. Please update page.');
    });
}

function showPopupWithCallback(header, message, callback) {
    $.magnificPopup.open({
        items: {
            src: '<div class="text-center white-popup">' +
            '<h2>' + header + '</h2>' +
            '<div class="popup-modal-text">' + message + '</div>' +
            '<button class="btn btn-outline-primary popup-modal-dismiss">Close</button>' +
            '</div>',
            type: 'inline'
        },
        closeOnBgClick: false,
        enableEscapeKey: false,
        closeBtnInside: true
    });
    $(document).on('click', '.popup-modal-dismiss', callback);
}
