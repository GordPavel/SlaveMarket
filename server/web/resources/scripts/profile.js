$('#sideMenu').affix({
    offset:{
        top: $('#firstRow').offset().top + $('#firstRow').outerHeight(),
        bottom: $('footer').outerHeight() + 50
    }
});