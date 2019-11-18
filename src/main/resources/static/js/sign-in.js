$(document).ready(function () {
    document.getElementById('login-text').focus();
    var log_in = $('#login-text');
    var email = $('#email-text');
    var firstName = $('#first-name-text');
    var lastName = $('#last-name-text');
    var password = $('#password-text');

    $('#btn-sign-in').click(function () {
        var form = document.getElementById('form-sign-in');
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
            form.classList.add('was-validated');
        } else {
            $.ajax({
                url: "/sign_in",
                type: "POST",
                dataType: "html",
                data: {
                    userName: log_in.val(),
                    password: password.val(),
                    email: email.val(),
                    firstName: firstName.val(),
                    lastName: lastName.val()
                },
                success: function (data) {
                    log_in.removeClass('is-invalid');
                    email.removeClass('is-invalid');
                    firstName.removeClass('is-invalid');
                    lastName.removeClass('is-invalid');
                    password.removeClass('is-invalid');
                    form.classList.add('was-validated');
                    document.location.href = "/";
                },
                error: function (request, status, error) {
                    form.classList.remove('was-validated');
                    var message = request.responseText;
                    if (message.indexOf("userName") !== -1)
                        log_in.addClass('is-invalid');
                    if (message.indexOf("email") !== -1)
                        email.addClass('is-invalid');
                    if (message.indexOf("password") !== -1) {
                        password.addClass('is-invalid');
                    }
                }
            });
        }
    });

    log_in.on('input', function () {
        log_in.removeClass('is-valid');
        log_in.removeClass('is-invalid');
    });

    log_in.keypress(function (e) {
        if (e.which === 13)
            document.getElementById('email-text').focus();
    });

    email.on('input', function () {
        email.removeClass('is-valid');
        email.removeClass('is-invalid');
    });

    email.keypress(function (e) {
        if (e.which === 13)
            document.getElementById('first-name-text').focus();
    });

    firstName.on('input', function () {
        firstName.removeClass('is-valid');
        firstName.removeClass('is-invalid');
    });

    firstName.keypress(function (e) {
        if (e.which === 13)
            document.getElementById('last-name-text').focus();
    });

    lastName.on('input', function () {
        lastName.removeClass('is-valid');
        lastName.removeClass('is-invalid');
    });

    lastName.keypress(function (e) {
        if (e.which === 13)
            document.getElementById('password-text').focus();
    });

    password.on('input', function () {
        password.removeClass('is-valid');
        password.removeClass('is-invalid');
    });

    password.keypress(function (e) {
        if (e.which === 13)
            $('#btn-sign-in').click();
    });
});