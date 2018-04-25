let merch_items = [];
let excluded_classes = [];
let main_offset = 0;
let sort_param = "id";
let main_desc = true;

const Item = ({id, name, benefit, price, image}) => `
<div class="item">
    <div class="image text-center">
        <a href="/merch/${id}">
            <img src="data:image/jpg;base64,${image}" alt="Image">
        </a>
    </div>
    <div class="info text-center">
        <div class="name">
            <h3 class="name">${name}</h3>
        </div>
        <div class="benefit">
            Benefit: ${benefit}%
        </div>
        <div class="price">
            <h4>${price}<i class="fab fa-stripe-s"></i></h4>
        </div>
        <div class="control">
            <div class="buttons" style="display: inline-flex;">
                <a href="javascript:void(0);" onclick="void(0);" class="add-card-btn merch_ctrl"
                   data-product_id="${id}">
                   <i class="fa fa-shopping-cart" data-product_id="${id}" aria-hidden="true"></i>
                </a>
                <a href="/merchandises/${id}" class="merch_ctrl"><i class="fa fa-search"></i><span
                                        class="d-none d-md-inline">Show</span></a>
            </div>
        </div>
    </div>
</div>
`;

function updateShop(reload) {
    let res = merch_items.filter(x => excluded_classes.indexOf(x.class) === -1).map(x => {
        return {id: x.id, name: x.name, benefit: Math.round(x.benefit), price: x.price, image: x.image}
    }).map(Item).join('');
    let content = $('#main-shop');
    content.empty();
    content.append(res);
    updateCart();
}

function loadGoods(query, limit, offset, order, desc, reload) {
    $.post("/rest/methods/search", {
        query: query,
        limit: limit,
        order: order,
        desc: desc,
        offset: offset
    }, function (data) {
        if (reload) {
            merch_items = [];
        }
        main_offset = offset + limit;
        if (data.length === 0) {
            $('#load_more').hide();
        } else {
            $('#load_more').show();
        }
        data.map(x => JSON.parse(x)).forEach(x => merch_items.push(x));
        updateShop(reload);
    });
}

function loadClasses() {
    $.post("/rest/methods/availableClasses", function (data, status) {
        let classList = $('#availableClasses');
        classList.empty();
        data.forEach(function (item) {
            classList.append('<a href="#" class="list-group-item sub_sub_item class_select">' + item + ' <i chosen="true" class="fas fa-check"></i></a>');
        });
    });
}

$(document).ready(function () {

    $('#slide-submenu').on('click', function () {
        $(this).closest('.list-group').fadeOut('slide', function () {
            $('.mini-submenu').fadeIn();
        });

    });

    $('.mini-submenu').on('click', function () {
        $(this).next('.list-group').toggle('slide');
        $('.mini-submenu').hide();
    });

    $('#load_more').on('click', function () {
        loadGoods("", 20, main_offset, sort_param, main_desc, false)
    });

    $('.desc_sort').click(function (event) {
        main_desc = true;
        sort_param = $(event.target).attr("param");
        loadGoods("", 20, 0, sort_param, main_desc, true);
    });

    $('.asc_sort').click(function (event) {
        main_desc = false;
        sort_param = $(event.target).attr("param");
        loadGoods("", 20, 0, sort_param, main_desc, true);
    });

    loadClasses();
    $('body').on('click', '.class_select', function (event) {
        const className = $(event.target).text().trim();
        console.log(className);
        let checker = $($(event.target).children()[0]);
        if (checker.attr("chosen") === "true") {
            checker
                .removeClass("fa-check")
                .addClass("fa-times")
                .attr("chosen", "false");
            excluded_classes.push(className);
        } else {
            checker
                .removeClass("fa-times")
                .addClass("fa-check")
                .attr("chosen", "true");
            let index = excluded_classes.indexOf(className);
            if (index > -1) {
                excluded_classes.splice(index, 1);
            }
        }
        loadGoods("", 20, 0, sort_param, main_desc, true);
    });

    loadGoods("", 20, 0, sort_param, main_desc, true);
});
