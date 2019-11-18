function openInputModal() {
    var inputModal = $('#input-modal');
    inputModal.on('shown.bs.modal', function () {
        document.getElementById("user-name").focus();
    });
    inputModal.modal('show');
}

$(document).ready(function() {
    $('#btn-input').click(function () {
        var userName = document.getElementById("user-name");
        var password = document.getElementById("password");
        var infoText = $('#info-text');
        $.ajax({
            url: "/log_in",
            type: "POST",
            dataType: "html",
            data: {
                userName: userName.value,
                email: userName.value,
                password: password.value
            },
            success: function (data) {
                $('#navpanel').html($(data).find('#navpanel').html());
                if ($(data).find('#welcome-text').text() !== "") {
                    userName.classList.remove('is-invalid');
                    password.classList.remove('is-invalid');
                    userName.classList.add('is-valid');
                    password.classList.add('is-valid');
                    infoText.css("display", "none");
                    document.location.href = "/";
                } else {
                    userName.classList.remove('is-valid');
                    password.classList.remove('is-valid');
                    userName.classList.add('is-invalid');
                    password.classList.add('is-invalid');
                    infoText.css("display", "block");
                }
            },
            error: function() {
                userName.classList.remove('is-valid');
                password.classList.remove('is-valid');
                userName.classList.add('is-invalid');
                password.classList.add('is-invalid');
                infoText.css("display", "block");
            }
        });
        return false;
    });
    var userNameField = $('#user-name');
    var passwordField = $('#password');
    userNameField.on('input', function() {
        clearInputValid();
    });
    passwordField.on('input', function() {
        clearInputValid();
    });
    userNameField.keypress(function(e) {
        if (e.which === 13) {
            $('#btn-input').click();
        }
    });
    passwordField.keypress(function(e) {
        if (e.which === 13) {
            $('#btn-input').click();
        }
    });
});

function clearInputValid() {
    var userName = document.getElementById("user-name");
    userName.classList.remove('is-valid');
    userName.classList.remove('is-invalid');
    var password = document.getElementById("password");
    password.classList.remove('is-valid');
    password.classList.remove('is-invalid');
    $('#info-text').css("display", "none");
}