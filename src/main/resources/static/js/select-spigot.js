function registerCardClickEvent(card) {
    $(card).on("click", function () {
        // $(this).on("click", function () {
        const spigotVersion = $(this).find(".spigot-version").val();

        $("#spigot-modal .modal-body").text("Are you sure you want to install Spigot " + spigotVersion + "?");
        $("#spigot-modal #install-server").attr("spigot-version", spigotVersion);
        $("#spigot-modal").modal("show");
    });
}

for (const card of $(".card")) {
    registerCardClickEvent(card);
}

$("#install-server").on("click", function () {
    $("#spigot-modal").modal("hide");
    $("#server-install-modal").modal("show");

    let serverId = 0;
    let installationStatusMessageInterval;
    const spigotVersion = $("#install-server").attr("spigot-version");

    $("#server-install-modal #open-server").hide();
    $("#server-install-modal .modal-body").html("                <div class=\"text-center\">\n" +
        "                    <p>Your server is being installed. Please wait!</p>\n" +
        "                    <div class=\"loader\" role=\"status\">\n" +
        "                        <span class=\"sr-only\">Loading...</span>\n" +
        "                    </div>\n" +
        "                    <p id=\"installation-status\"></p>\n" +
        "                </div>");

    //TODO: Fire installation on the server...
    $.ajax({
        url: "/api/spigot/" + spigotVersion + "/install",
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
        console.log("Getting status for server id = " + serverId);
        $.ajax({
            url: "/api/servers/installation-status/" + serverId,
            method: "GET"
        }).done(function (response) {
            let message = response["message"];
            let percent = response["percent"];
            let phase = response["phase"];

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