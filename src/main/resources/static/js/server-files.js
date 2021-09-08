var editor;

ace.config.set('basePath', '/js/')
$('#file-editor').css("font-size", "16px");

let serverId = $("#server-id").val();

function buildTreeNodePath(node) {
    if(node.title === 'root') {
        return "";
    } else {
        return buildTreeNodePath(node.parent) + "/" + node.title;
    }
}

var glyph_opts = {
    preset: "bootstrap3",
    map: {
    }
};


$('#tree').fancytree({
    // selectMode: 1,
    // glyph: glyph_opts,
    clickFolderNode: 3,
    // source: [
    //     { "title" : "siema", key: "elo"},
    //     { "title" : "boom", key: "nie", folder: true, lazy: true}
    // ],
    source: {
        url: "/api/servers/" + serverId + "/folder-content"
    },
    lazyLoad: function (event, data) {
        var node = data.node;
        console.log("Lazy load!");
        console.log(node);
        // Load child nodes via Ajax GET /getTreeData?mode=children&parent=1234
        data.result = {
            url: "/api/servers/" + serverId + "/folder-content?path=" + buildTreeNodePath(node),
            data: {mode: "children", parent: node.key},
            cache: false
        };
    },
    activate: function(event, data) {
        let node = data.node;
        console.log("Selected node = " + node.title);
        console.log(node);
        if (!node.isFolder()) {
            openFileInEditor(node.title);
        }
    }
});

function openFileInEditor(fileName) {
    let serverId = $("#server-id").val();
    editor = ace.edit("file-editor");
    editor.setTheme("ace/theme/github");
    editor.session.setMode("ace/mode/yaml");
    $("#file-editor-parent").fadeIn();

    $.ajax({
        method: "GET",
        url: "/api/servers/" + serverId + "/file-content/" + fileName,
        success: function (response) {
            console.log(response);
            editor.setValue(response, -1);
        }
    });
}

function saveFile() {
    let serverId = $("#server-id").val();
    let selectedNode = $.ui.fancytree.getTree("#tree").getActiveNode();
    if (selectedNode.isFolder())
        return;

    let fileName = selectedNode.title;
    let saveFileButton = $("#save-file");
    saveFileButton.text("");
    saveFileButton.append("<span class='spinner-border spinner-border-sm' role='status' aria-hidden='true'></span>");

    $.ajax({
        method: "POST",
        url: "/api/servers/" + serverId + "/file-content/" + fileName,
        data: editor.getValue(),
        contentType: "text/plain",
        success: function (response) {
            saveFileButton.text("");
            saveFileButton.removeClass("btn-primary");
            saveFileButton.addClass("btn-success");
            saveFileButton.append("Saved <i id='save-success' style='display: none;' class='far fa-check-circle'></i>");
            $("#save-success").fadeIn();

            setTimeout(function () {
                saveFileButton.text("");
                saveFileButton.removeClass("btn-success");
                saveFileButton.addClass("btn-primary");
                saveFileButton.append("Save");
                $("#save-success").fadeIn();
            }, 4000);
        }
    });
}