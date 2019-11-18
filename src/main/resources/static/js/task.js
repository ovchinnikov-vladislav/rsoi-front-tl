var codeArea = null;

$(document).ready(function () {
    if (document.getElementById('user-name') != null)
        document.getElementById('user-name').focus();
    if (document.getElementById('source-template-task') != null)
        document.getElementById('source-template-task').focus();
    codeArea = CodeMirror.fromTextArea(document.getElementById('source-template-task'), {
        lineNumbers: true,               // показывать номера строк
        matchBrackets: true,             // подсвечивать парные скобки
        mode: "text/x-java",              // стиль подсветки,
        indentUnit: 4,                    // размер табуляции
        viewportMargin: Infinity
    });
    codeArea.setOption("theme", "darcula");
    var mac = CodeMirror.keyMap.default === CodeMirror.keyMap.macDefault;
    CodeMirror.keyMap.default[(mac ? "Cmd" : "Ctrl") + "-Space"] = "autocomplete";
});

function sendExecuteTask(idUser, idTask) {
    $('#overlay').css('display', 'block');
    var request = {};
    request.idTask = idTask;
    request.idUser = idUser;
    if (codeArea != null)
        request.sourceTask = codeArea.getValue();
    else
        request.sourceTask = $('#source-template-task');
    $.ajax({
        url: "/task/execute",
        contentType: "application/json",
        type: "POST",
        dataType: "html",
        data: JSON.stringify(request),
        success: function (data) {
            $('#overlay').css('display', 'none');
            $('#result-test').html($(data).find('#result-test').html());
        },
        error: function (request, status, error) {
            $('#overlay').css('display', 'none');
            $('#card-result').html("Ошибка компиляции");
        }
    });
}