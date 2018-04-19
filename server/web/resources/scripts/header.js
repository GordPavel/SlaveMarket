$(function () {
    updateCart();
    $('[data-toggle="popover"]').popover()
});

function updateCart() {
    var cart = $('.cart-info');
    var cartSize = $('.cart-info .badge');
    $.post(
        "/cart/info/", {type: "size"}, function (data) {
            var size = JSON.parse(data);
            if (size.size >= 1) {
                cart.removeAttr("data-toggle");
                cart.removeAttr("data-placement");
                cart.removeAttr("data-content");
                cart.attr({href: "/cart", cart_elements: size.size});
                cartSize.text(size.size);
            } else {
                cart.attr({
                    "data-toggle": "popover",
                    "data-placement": "bottom",
                    "data-content": "Your cart is empty"
                });
                cartSize.text("empty");
            }
        }).fail(function (data, status) {
            console.log("Response error with status " + status + " data:" + data.responseText);
            showPopup("Error", "Can\'t update cart.\nPlease reload page.");
        })
}

function showPopup(header, message) {
    $.magnificPopup.open({
        items: {
            src: '<div class="text-center white-popup">' +
            '<h2>' + header + '</h2>' +
            '<div class="popup-modal-text">' + message + '</div>' +
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
}
