// Create merchandise form.
var bf;
// Change password form.
var cpf;
// Change username form.
var cunf;
var $deal_limit = 0;
var $deal_panel;
var $class_name = "";

$(document).ready(function () {
    $deal_panel = $('#accordion');
    $('.addMerchLink').click(function (event) {
        $('#form-container-main').hide();
        $.post("/rest/methods/availableClasses", function (data, status) {
            var classList = $('.availableClasses');
            classList.empty();
            while (classList.firstChild) {
                classList.removeChild(classList.firstChild)
            }
            var form = document.getElementById("form-container");
            while (form.firstChild) {
                form.removeChild(form.firstChild);
            }
            data.forEach(function (item) {
                classList.append('<a href="javascript:void(0)" onclick="chooseClass(\'' + item + '\')" class="list-group-item list-group-item-action classesListItem">' + item + '</a>');
            });
        }).fail(function (data, status) {
            showPopup('Error:', 'Can\'t load classes.\nReload page or contact system administrator.');
        });
    });
});

function chooseClass(item) {
    $('.addingHeader').text('Please enter following values');
    $class_name = item;
    $.post('/rest/methods/getFieldsWithTypes', {className: item}, function (data) {
        $('#form-container-main').show();
        var classList = $('.availableClasses');
        classList.empty();
        var schema = JSON.parse(data);
        var BrutusinForms = brutusin["json-forms"];
        bf = BrutusinForms.create(schema);
        var container = document.getElementById('form-container');
        bf.render(container, '');
        $(".glyphicon-info-sign").removeClass('glyphicon glyphicon-info-sign').addClass('fas fa-info-circle')
    }).fail(function (data, status) {
        showPopup('Error:', 'Can\'t load required fields.\nReload page or contact system administrator.');
    });
}

function addMerch($username, $token) {
    if (null != bf.getData()) {
        $.post('/rest/methods/addMerch', {
            className: $class_name,
            fields: JSON.stringify(bf.getData()),
            username: $username,
            token: $token
        }, function (data) {
            showPopup('Info:', 'successfullyAdded')
        }).fail(function (data, status) {
            showPopup('Error:', 'Can\'t add merchandise');
        });
    }
    else {
        showPopup('Error:', 'Please enter at least one field.');
    }
}

function loadDeals($username, $token, $page) {
    var $offset = ($page - 1) * 10;
    $.post("/rest/methods/getDeals", {
        username: $username,
        token: $token,
        offset: $offset,
        limit: 10
    }, function (data, status) {
        deal_response = JSON.parse(data);
        $deal_limit = deal_response.count;
        var dealsList = deal_response.deals;
        $deal_panel.empty();
        dealsList.forEach(function (deal) {
            appendDeal(deal);
        });
    })

}

function openDeals($username, $token, $offset) {
    var deal_response;
    var $page_count = 1;
    $.post("/rest/methods/getDeals", {
        username: $username,
        token: $token,
        offset: $offset,
        limit: 10
    }, function (data, status) {
        deal_response = JSON.parse(data);
        var dealsList = deal_response.deals;
        $deal_limit = deal_response.count;
        $page_count = Math.ceil($deal_limit / 10);
        var $deal_panel = $('#accordion');
        window.pagObj = $('#pagination').twbsPagination({
            totalPages: $page_count,
            visiblePages: 6,
            first: null,
            last: null,
            prev: '<',
            next: '>',
            onPageClick: function (event, page) {
                loadDeals($username, $token, page)
            }
        });
        $deal_panel.empty();
        dealsList.forEach(function (deal) {
            appendDeal(deal);
            $offset++;
        });
    }).fail(function (data, status) {
        showPopup('Error:', 'Can\'t load deals.\nReload page or contact system administrator.');
    });
}

function appendDeal(deal) {
    var state = deal.state;
    var $bage_class = "badge-info";
    var $table_class = "";
    if (state === "on sale") {
        $bage_class = "badge-info";
    } else if (state === "sold") {
        $bage_class = "badge-success";
        $table_class = "table-success";
    } else if (state === "bought") {
        $bage_class = "badge-primary";
        $table_class = "table-danger";
    } else if (state === "removed") {
        $bage_class = "badge-warning";
    }
    $deal_panel.append('<div class="card">\n' +
        ' <div class="card-header">\n' +
        '     <a class="card-link" data-toggle="collapse" href="#collapseDeal' + deal.id + '">\n' +
        '         <div class="row">\n' +
        '             <div class="col-md-6" style="display: inline-block;">\n' +
        '                 Deal#' + deal.id + '\n' +
        '             </div>' +
        '            <div class="col-md-6 text-right">' +
        '                 <span class="badge ' + $bage_class + '">' + deal.state + '</span>\n' +
        '            </div>\n' +
        '         </div>\n' +
        '     </a>\n' +
        ' </div>\n' +
        ' <div id="collapseDeal' + deal.id + '" class="collapse" data-parent="#accordion">\n' +
        '     <div class="card-body">\n' +
        '        <pre>\n' +
        '            <table class="table table-hover">\n' +
        '               <tbody>\n' +
        '                  <tr>\n' +
        '                    <td>id:</td>\n' +
        '                    <td>' + deal.id + '</td>\n' +
        '                  </tr>\n' +
        '                  <tr>\n' +
        '                    <td>Date:</td>\n' +
        '                    <td>' + deal.time + '</td>\n' +
        '                  </tr>\n' +
        '                  <tr>\n' +
        '                    <td>State:</td>\n' +
        '                    <td>' + deal.state + '</td>\n' +
        '                  </tr>\n' +
        '                  <tr class="' + $table_class + '">\n' +
        '                    <td>Price:</td>\n' +
        '                    <td>' + deal.price + '<i class="fab fa-stripe-s"></i></td>\n' +
        '                  </tr>\n' +
        '                  <tr>\n' +
        '                      <td>Merchandise id</td>\n' +
        '                      <td><a href="/merchandise/' + deal.merchId + '">' + deal.merchId + '</a></td>\n' +
        '                  </tr>\n' +
        '               </tbody>\n' +
        '            </table>\n' +
        '        </pre>\n' +
        '     </div>\n' +
        '  </div>\n');
}

function openUserEditor() {
    var new_username_form = {
        "$schema": "http://json-schema.org/draft-03/schema#",
        "type": "object",
        "properties": {
            "new_username": {
                "type": "string",
                "title": "New username",
                "minLength": 1,
                "description": "Your new username"
            }
        }
    };
    var password_forms = {
        "$schema": "http://json-schema.org/draft-03/schema#",
        "type": "object",
        "properties": {
            "old_password": {
                "title": "old password",
                "type": "string",
                "required": true,
                "format": "password",
                "minLength": 8,
                "description": "Your old password"
            },
            "new_password": {
                "title": "old password",
                "type": "string",
                "required": true,
                "minLength": 8,
                "format": "password",
                "description": "Your new password"
            }
        }
    };
    $('#changeUsernameForm').empty();
    $('#changePasswordForm').empty();
    var BrutusinForms = brutusin["json-forms"];
    cpf = BrutusinForms.create(password_forms);
    BrutusinForms.addDecorator(function (element, schema) {
        if (element.tagName) {
            var tagName = element.tagName.toLowerCase();
            if (tagName === "input" && schema.type === "string") {
                if (schema.format === 'inputstream' || schema.format === 'file') {
                    element.type = 'file';
                } else if (schema.format === 'password') {
                    element.type = 'password';
                }
            }
        }
    });

    var container1 = document.getElementById('changePasswordForm');
    cpf.render(container1);
    cunf = BrutusinForms.create(new_username_form);
    var container2 = document.getElementById('changeUsernameForm');
    cunf.render(container2);

}

function new_pass() {
    if (cpf.getData() != null) {
        alert(JSON.stringify(cpf.getData()));
    } else {
        showPopup('Error:', 'Please enter at least one field.')
    }
}

function new_username() {
    if (cunf.getData() != null) {
        alert(JSON.stringify(cunf.getData()));
    } else {
        showPopup('Error:', 'Please enter at least one field.')
    }
}