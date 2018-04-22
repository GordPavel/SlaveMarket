// Create merchandise form.
var bf;
// Change password form.
var cpf;
// Change username form.
var cunf;
var $deal_limit = 0;
var $deal_panel;
var $class_name = "";
var exifNode = $('#exif');
var thumbNode = $('#thumbnail');
var currentFile;
var coordinates;

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
        BrutusinForms.addDecorator(function (element, schema) {
            if (element.tagName) {
                var tagName = element.tagName.toLowerCase();
                if (tagName === "input" && schema.type === "string") {
                    if (schema.format === 'inputstream' || schema.format === 'file') {
                        element.type = 'file';
                    }
                }
            }
        });
        bf = BrutusinForms.create(schema);
        var container = document.getElementById('form-container');
        bf.render(container, '');
        $(".glyphicon-info-sign").removeClass('glyphicon glyphicon-info-sign').addClass('fas fa-info-circle');
        var result = $('#img-result');
        if (window.createObjectURL || window.URL || window.webkitURL ||
            window.FileReader) {
            result.children().hide();
        }
        $('input[type="file"]')
            .on('change', dropChangeHandler);
        $('#img-edit')
            .on('click', function (event) {
                event.preventDefault();
                var imgNode = result.find('img, canvas');
                var img = imgNode[0];
                var pixelRatio = window.devicePixelRatio || 1;
                imgNode.Jcrop({
                    setSelect: [
                        40,
                        40,
                        (img.width / pixelRatio) - 40,
                        (img.height / pixelRatio) - 40
                    ],
                    onSelect: function (coords) {
                        coordinates = coords
                    },
                    onRelease: function () {
                        coordinates = null
                    }
                }).parent().on('click', function (event) {
                    event.preventDefault()
                });
                $('#img-crop').show();
            });
        $('#img-crop')
            .on('click', function (event) {
                event.preventDefault();
                var img = result.find('img, canvas')[0];
                var pixelRatio = window.devicePixelRatio || 1;
                if (img && coordinates) {
                    updateResults(loadImage.scale(img, {
                        left: coordinates.x * pixelRatio,
                        top: coordinates.y * pixelRatio,
                        sourceWidth: coordinates.w * pixelRatio,
                        sourceHeight: coordinates.h * pixelRatio,
                        minWidth: result.width(),
                        maxWidth: result.width(),
                        pixelRatio: pixelRatio,
                        downsamplingRatio: 0.5
                    }));
                    coordinates = null;
                }
                $('#img-crop').hide();
            })
    }).fail(function (data, status) {
        showPopup('Error:', 'Can\'t load required fields.\nReload page or contact system administrator.');
    });
}

function displayImage(file, options) {
    currentFile = file;
    if (!loadImage(
        file,
        updateResults,
        options
    )) {
        result.children().replaceWith(
            $('<span>' +
                'Your browser does not support the URL or FileReader API.' +
                '</span>')
        )
    }
}

function updateResults(img, data) {
    var fileName = currentFile.name;
    var href = img.src;
    var dataURLStart;
    var content;
    if (!(img.src || img instanceof HTMLCanvasElement)) {
        content = $('<span>Loading image file failed</span>')
    } else {
        if (!href) {
            href = img.toDataURL(currentFile.type + 'REMOVEME');
            // Check if file type is supported for the dataURL export:
            dataURLStart = 'data:' + currentFile.type;
            if (href.slice(0, dataURLStart.length) !== dataURLStart) {
                fileName = fileName.replace(/\.\w+$/, '.png')
            }
        }
        content = $('<a target="_blank">').append(img)
            .attr('download', fileName)
            .attr('href', href)
    }
    var result = $('#img-result');
    result.children().replaceWith(content);
    var actionsNode = $('#img-actions');
    if (img.getContext) {
        actionsNode.show()
    }
    if (data && data.exif) {
        displayExifData(data.exif)
    }
}

function displayExifData(exif) {
    var thumbnail = exif.get('Thumbnail');
    var tags = exif.getAll();
    var table = exifNode.find('table').empty();
    var row = $('<tr></tr>');
    var cell = $('<td></td>');
    var prop;
    if (thumbnail) {
        thumbNode.empty();
        loadImage(thumbnail, function (img) {
            thumbNode.append(img).show()
        }, {orientation: exif.get('Orientation')})
    }
    for (prop in tags) {
        if (tags.hasOwnProperty(prop)) {
            table.append(
                row.clone()
                    .append(cell.clone().text(prop))
                    .append(cell.clone().text(tags[prop]))
            )
        }
    }
    exifNode.show()
}

function addMerch($username, $token) {
    if (null != bf.getData()) {
        var img = $('a[target="_blank"]').attr("href").split(',')[1];
        var request = bf.getData();
        if (request.image) {
            request.image = img;
        }
        $.post('/rest/methods/addMerch', {
            className: $class_name,
            fields: JSON.stringify(request),
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
            "new_password": {
                "title": "New password",
                "type": "string",
                "required": true,
                "format": "password",
                "minLength": 8,
                "description": "Your new password"
            },
            "new_password_rep": {
                "title": "Repeat new password",
                "type": "string",
                "required": true,
                "minLength": 8,
                "format": "password",
                "description": "Your new password one more time"
            }
        }
    };
    $('#changeUsernameForm').empty();
    $('#changePasswordForm').empty();
    var BrutusinForms1 = brutusin["json-forms"];
    cpf = BrutusinForms1.create(password_forms);
    BrutusinForms1.addDecorator(function (element, schema) {
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
    var BrutusinForms2 = brutusin["json-forms"];
    var container1 = document.getElementById('changePasswordForm');
    cpf.render(container1);
    cunf = BrutusinForms2.create(new_username_form);
    var container2 = document.getElementById('changeUsernameForm');
    cunf.render(container2);
    $(".glyphicon-info-sign").removeClass('glyphicon glyphicon-info-sign').addClass('fas fa-info-circle');
}

function new_pass(username, token) {
    if (cpf.getData() != null) {
        var newPass = cpf.getData();
        if (newPass.new_password !== newPass.new_password_rep) {
            showPopup('Error:', 'Password does not match');
        } else {
            $.post("/change/password", {
                username: username,
                newPassword: newPass.new_password,
                token: token
            }, function (data, status) {
                showPopup("Info:", "Successfully changed");
            }).fail(function (data, status) {
                showPopup("Error", "Can\'t change password. Try another, or reload page");
            });
        }
    } else {
        showPopup('Error:', 'Please enter passwords.')
    }
}

function new_username(username, token) {
    if (cunf.getData() != null) {
        $.post("/change/login", {
            username: username,
            newUsername: cunf.getData().new_username,
            token: token
        }, function (data, status) {
            showPopup("Info:", "Successfully changed");
        }).fail(function (data, status) {
            showPopup("Error", "Can\'t change username. Try another, or reload page");
        });
    } else {
        showPopup('Error:', 'Please enter at least one field.');
    }
}

function dropChangeHandler(e) {
    $('#img-editor').show();
    e.preventDefault();
    e = e.originalEvent;
    var target = e.dataTransfer || e.target;
    var file = target && target.files && target.files[0];
    var result = $('#img-result');
    var options = {
        maxWidth: result.width(),
        canvas: true,
        pixelRatio: window.devicePixelRatio,
        downsamplingRatio: 0.5,
        orientation: true
    };
    if (!file) {
        return
    }
    exifNode.hide();
    thumbNode.hide();
    displayImage(file, options)
}