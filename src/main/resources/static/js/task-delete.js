function openDeleteModal() {
    $('#delete-modal').modal('show');
}

$(document).ready(function() {
    $('#btn-delete').click(function (e) {
        var deleteBtn = document.getElementById("delete-button");
        var idUser = deleteBtn.getAttribute("data-iduser");
        var idTask = deleteBtn.getAttribute("data-idtask");
        var overlay = $('#overlay');
        overlay.css('display', 'block');
        $('#delete-modal').modal('hide');
        $.ajax({
            url: '/user/' + idUser + '/task/' + idTask + '/delete',
            type: "POST",
            dataType: "html",
            success: function (data) {
                $('#tasks-div').html($(data).find('#tasks-div').html());
                overlay.css('display', 'none');
            }, error: function () {
                alert("Произошла ошибка при удалении.");
            }
        })
    });
});