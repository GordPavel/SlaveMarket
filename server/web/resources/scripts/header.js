$(function () {
    updateCart();
    $('body').on('click', '.merchandises .item .control .add-card-btn', function (event) {
        let merchId = event.target.getAttribute("data-product_id");
        console.log(merchId);
        $.post("/cart/", {id: merchId}, function (data) {
            console.log("Added in cart");
            $(event.target).addClass('disabled');
            $('.cart-info').attr({
                "data-toggle": "",
                "data-placement": "",
                "data-content": ""
            });
            updateCart();
        }).fail(function (data, status) {
            console.log("Response error with status " + status + " data:" + data.responseText);
            showPopup('Error', 'Can\'t add merchandise into cart');
        });
    });
    $('body').on('click', '.merchandises .item .control .disabled', function (event) {
        showPopup('Info:', 'Already in cart')
    });
});

function updateCart() {
    let cart = $('.cart-info');
    let cartSize = $('.cart-info .badge');
    $.post(
        "/cart/info/", {type: "items"}, function (data) {
            let cart_res = JSON.parse(data);
            let size = cart_res.size;
            if (size >= 1) {
                cart.attr({href: "/cart", cart_elements: size.size});
                cartSize.text(size);
                cart_res.items.map(x => JSON.parse(x)).forEach(x => {
                    $('.add-card-btn[data-product_id="' + x.id + '"]')
                        .addClass("disabled")
                        .removeClass('add-card-btn')
                        .html('<i class="fas fa-check"></i>');
                });
            } else {
                cart.attr({
                    "data-toggle": "popover",
                    "data-placement": "bottom",
                    "data-content": "Your cart is empty"
                });
                cartSize.text("empty");
                $('[data-toggle="popover"]').popover();
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
