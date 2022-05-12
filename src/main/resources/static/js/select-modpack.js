function registerCardClickEvent(card) {
    $(card).on("click", function () {
        // $(this).on("click", function () {
        let modpackTitle = $(this).find(".card-title").text();
        let modpackId = $(this).find(".modpack-id").val();

        $.ajax({
            url: "/api/modpacks/" + modpackId + "/description",
            method: "GET"
        }).done(function (description) {
            $("#modpack-modal .modal-title").text(modpackTitle);
            $("#modpack-modal .modal-body").html(description);
            $("#modpack-modal #select-server-version").attr("modpack-id", modpackId);
            $("#modpack-modal").modal("show");
        });
    });
}

function appendModpacksToList(modpackObjects) {
    for (const modpackObject of modpackObjects) {
        let modpackCardTemplate = $("#modpack-card-template").clone().children().first();
        modpackCardTemplate.find(".card-img-top").attr("src", modpackObject["thumbnailUrl"]);
        modpackCardTemplate.find(".card-title").text(modpackObject["name"]);
        modpackCardTemplate.find(".card-version").text("Version " + modpackObject["version"]);
        modpackCardTemplate.find(".modpack-id").val(modpackObject["id"]);
        modpackCardTemplate.find(".card-text").text(modpackObject["summary"]);
        modpackCardTemplate.attr("hidden", false);

        $("#modpacks-list").append(modpackCardTemplate);

        registerCardClickEvent(modpackCardTemplate);
    }
}

for (const card of $(".card")) {
    registerCardClickEvent(card);
}

$("#category-filter").on("change", function (){
    let categoryId = $(this).val();
    let version = $("#version-filter").val();
    console.log("Category changed to = " + categoryId);

    $.ajax({
        url: "/api/modpacks/search?categoryId=" + categoryId + "&size=20&version=" + version + "&index=0",
        method: "GET",
        success: function (response) {
            console.log(response);
            $("#modpacks-list").empty();
            appendModpacksToList(response);
        }
    });
});

$("#version-filter").on("change", function (){
    let version = $(this).val();
    let categoryId = $("#category-filter").val();
    console.log("Version changed to = " + categoryId);

    $.ajax({
        url: "/api/modpacks/search?categoryId=" + categoryId + "&size=20&version=" + version + "&index=0",
        method: "GET",
        success: function (response) {
            console.log(response);
            $("#modpacks-list").empty();
            appendModpacksToList(response);
        }
    });
});

$("#modpack-name-search").on("keydown", function(event){
    if (event.keyCode == 13) {
        let version = $("#version-filter").val();
        let categoryId = $("#category-filter").val();
        let modpackName = $("#modpack-name-search").val();

        $.ajax({
            url: "/api/modpacks/search?categoryId=" + categoryId + "&size=20&version=" + version + "&index=0" + "&modpackName=" + modpackName,
            method: "GET",
            success: function (response) {
                console.log(response);
                $("#modpacks-list").empty();
                appendModpacksToList(response);
            }
        })
    }
});

$("#select-server-version").on("click", function () {

    let modpackId = $("#select-server-version").attr("modpack-id");
    $("#modpack-modal").modal("hide");

    let selectServerVersionModal = $("#server-version-modal");
    selectServerVersionModal.find("#install-server").attr("modpack-id", modpackId);
    selectServerVersionModal.modal("show");

    $("#modpack-modal #select-server-version").attr("modpack-id", modpackId);

    console.log("Getting avaialble server packs for modpack id = " + modpackId);
    $.ajax({
        url: "/api/modpacks/" + modpackId + "/serverpacks",
        method: "GET",
        success: function (response) {
            console.log(response);
            let serverVersionsList = $("#server-versions-list");
            serverVersionsList.empty();
            for (const serverpack of response) {
                serverVersionsList.append(new Option(serverpack["name"], serverpack["id"]));
            }
        }
    });
});

$("#install-server").on("click", function () {
    $("#server-version-modal").modal("hide");
    $("#server-install-modal").modal("show");

    let serverId = 0;
    let installationStatusMessageInterval;
    let modpackId = $("#install-server").attr("modpack-id");
    let serverPackId = $("#server-versions-list").val();

    $("#server-install-modal #open-server").hide();
    $("#server-install-modal .modal-body").html("                <div class=\"text-center\">\n" +
        "                    <p>Your modpack is being installed. Please wait!</p>\n" +
        "                    <div class=\"loader\" role=\"status\">\n" +
        "                        <span class=\"sr-only\">Loading...</span>\n" +
        "                    </div>\n" +
        "                    <p id=\"installation-status\"></p>\n" +
        "                </div>");

    //TODO: Fire installation on the server...
    $.ajax({
        url: "/api/modpacks/" + modpackId + "/serverpacks/" + serverPackId + "/install",
        method: "POST",
        success: function (response) {
            console.log(response);
            serverId = response;
        },
        error: function (errorResponse) {
            console.log(errorResponse);
            clearInterval(installationStatusMessageInterval);
            $("#server-install-modal .modal-body").text(errorResponse["responseJSON"]["message"]);
        }
    });

    function showInstallationStatus() {
        $.ajax({
            url: "/api/servers/installation-status/" + serverId,
            method: "GET"
        }).done(function (response) {
            let message = response["message"];
            let percent = response["percent"];
            let phase = response["phase"];
            $("#server-install-modal #installation-status").text(percent + "% " + message);

            if (phase === 5) {
                clearInterval(installationStatusMessageInterval);
                $("#server-install-modal .modal-body").text("Your server is ready!");
                $("#server-install-modal #open-server").show();
                $("#server-install-modal #open-server").attr("href", "/servers/" + serverId);
            } else if (phase === -1) {
                $("#server-install-modal .modal-body").text("ERROR: " + message);
            } else {
                $("#server-install-modal #installation-status").text(percent + "% " + message);
            }
        });
    }

    //TODO: Show installation status for the user
    installationStatusMessageInterval = setInterval(showInstallationStatus, 4000);
});

$(window).scroll(function () {
    if($(window).scrollTop() == $(document).height() - $(window).height()) {
        console.log("Loading more modpacks...");

        let version = $("#version-filter").val();
        let categoryId = $("#category-filter").val();
        // Get current modpack count
        let currentModpacksCount = $("#modpacks-list").children().length;

        $.ajax({
            url: "/api/modpacks/search?categoryId=" + categoryId + "&size=20&version=" + version + "&index=" + currentModpacksCount,
            method: "GET",
            success: function (response) {
                console.log(response);
                appendModpacksToList(response);
            }
        });
        // Load more mod
    }
});