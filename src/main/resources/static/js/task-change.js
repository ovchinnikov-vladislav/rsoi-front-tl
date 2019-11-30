var type_operation = 0;

function readURL(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('.image-task').attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function showAndUploadImage() {
    $('#change-image').on('click', function () {
        $('#file-input').trigger('click');
    });

    $("#file-input").change(function () {
        readURL(this);
        $('#submit-file').trigger('click');
    });

    $('#submit-file').click(function () {
        var file = $("#file-input");
        var fd = new FormData;
        fd.append('file', file.prop('files')[0]);
        $('#overlay').css('display', 'block');
        $.ajax({
            processData: false,
            contentType: false,
            data: fd,
            url: document.location.href + '/upload',
            type: 'POST',
            success: function (data) {
                $('#overlay').css('display', 'none');
            },
            error: function (request) {
                $('#overlay').css('display', 'none');
            }
        });
    });
}

function setComplexityTask() {
    $('#complexity-task').slider({
        formatter: function (value) {
            return 'Current value: ' + value;
        }
    });
}

var sourceTemplateArea = null;

function setSourceTemplateArea() {
    sourceTemplateArea = CodeMirror.fromTextArea(document.getElementById('source-template-task'), {
        lineNumbers: true,               // показывать номера строк
        matchBrackets: true,             // подсвечивать парные скобки
        mode: "text/x-java",              // стиль подсветки,
        indentUnit: 4,                    // размер табуляции
        viewportMargin: Infinity
    });
    sourceTemplateArea.setOption("theme", "darcula");
}

var sourceTestArea = null;

function setSourceTestArea() {
    sourceTestArea = CodeMirror.fromTextArea(document.getElementById('source-test'), {
        lineNumbers: true,               // показывать номера строк
        matchBrackets: true,             // подсвечивать парные скобки
        mode: "text/x-java", // стиль подсветки
        indentUnit: 4,                    // размер табуляции
        viewportMargin: Infinity
    });
    sourceTestArea.setOption("theme", "darcula");
}

function setInputValid() {
    $('#name-task').on('input', function () {
        $('#name-task').removeClass('is-invalid');
    });
    $('#text-task').on('input', function () {
        $('#text-task').removeClass('is-invalid');
    });
    $('#source-test').on('input', function () {
        $('#source-test').removeClass('is-invalid');
    })
}

function setSaveTask() {
    var location = document.location.href;
    if (type_operation === 0)
        location = location.substr(0, location.lastIndexOf('/')) + "/create";
    else if (type_operation === 1)
        location = location + "/update";
    var name_info = $('#name-info-text');
    var text_task_info = $('#text-task-info-text');
    var source_test_info = $('#source-test-info-text');
    $('#save_button').click(function () {
        $('#overlay').css('display', 'block');
        var dataTask = {};
        dataTask.nameTask = $('#name-task').val() + "";
        dataTask.description = $('#description-task').val() + "";
        dataTask.textTask = $('#text-task').val() + "";
        dataTask.templateCode = sourceTemplateArea.getValue("\n") + "";
        dataTask.complexity = $('#complexity-task').val();
        var dataTest = {};
        dataTest.sourceCode = sourceTestArea.getValue("\n") + "";
        dataTest.description = $('#description-test').val() + "";
        dataTask.test = dataTest;
        $.ajax({
            url: location,
            dataType: "html",
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify(dataTask),
            success: function (data) {
                $('#name-task').removeClass('is-invalid');
                name_info.css("display", "none");
                $('#text-task').removeClass('is-invalid');
                text_task_info.css("display", "none");
                $('#source-test').removeClass('is-invalid');
                source_test_info.css("display", "none");
                $('#btn-ok').click(function () {
                    window.location.href = document.referrer;
                });
                $('#info-text-modal').text("Данные задачи успешно сохранены.");
                $('#info-modal').modal('show');
            },
            error: function (request, status) {
                var message = request.responseText;
                if (message.indexOf("nameTask") !== -1) {
                    $('#name-task').addClass('is-invalid');
                    name_info.css("display", "block");
                }
                if (message.indexOf("textTask") !== -1) {
                    $('#text-task').addClass('is-invalid');
                    text_task_info.css("display", "block");
                }
                if (message.indexOf("sourceCode") !== -1) {
                    $('#source-test').addClass('is-invalid');
                    source_test_info.css("display", "block");
                }
                $('#info-text-modal').text("При сохранении произошла ошибка. Проверьте, что обязательные поля заполнены.");
                $('#btn-ok').click(function () {
                    $('#info-modal').modal('hide');
                    $('#overlay').css('display', 'none');
                });
                $('#info-modal').modal('show');
                $('#overlay').css('display', 'none');
            }
        });
    });
}

$(document).ready(function () {
    if (document.getElementById('user-name') != null)
        document.getElementById('user-name').focus();
    if (document.getElementById('name-task') != null)
        document.getElementById('name-task').focus();
    setSourceTemplateArea();
    setSourceTestArea();
    setComplexityTask();
    showAndUploadImage();
    setSaveTask();
    var mac = CodeMirror.keyMap.default === CodeMirror.keyMap.macDefault;
    CodeMirror.keyMap.default[(mac ? "Cmd" : "Ctrl") + "-Space"] = "autocomplete";
});

function sleep(ms) {
    ms += new Date().getTime();
    while (new Date() < ms){}
}