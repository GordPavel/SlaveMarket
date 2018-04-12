var bf;

$(document).ready(function () {
    $('.addMerchLink').click(function (event) {
        $('.form-container-main').hide();
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
            $.magnificPopup.open({
                items: {
                    src: '<div class="text-center white-popup">' +
                    '<h2>Error:</h2>' +
                    '<div class="popup-modal-text">Can\'t load classes.\nReload page or contact system administrator.</div>' +
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

function chooseClass(item) {
    $('.addingHeader').text('Please enter following values');
    $.post('/rest/methods/getFieldsWithTypes', {className: item}, function (data) {
        $('.form-container-main').show();
        var classList = $('.availableClasses');
        classList.empty();
        var schema = JSON.parse(data);
        var BrutusinForms = brutusin["json-forms"];
        bf = BrutusinForms.create(schema);
        var container = document.getElementById('form-container');
        bf.render(container, '');
        $(".glyphicon-info-sign").addClass('fas fa-info-circle')
    }).fail(function (data, status) {
        $.magnificPopup.open({
            items: {
                src: '<div class="text-center white-popup">' +
                '<h2>Error:</h2>' +
                '<div class="popup-modal-text">Can\'t load required fields.\nReload page or contact system administrator.</div>' +
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
}

function addMerch() {
    if (null != bf.getData()) {
        alert(JSON.stringify(bf.getData(), null, 4));
    }
    else {
        $.magnificPopup.open({
            items: {
                src: '<div class="text-center white-popup">' +
                '<h2>Error:</h2>' +
                '<div class="popup-modal-text">Enter at least one field</div>' +
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
}
