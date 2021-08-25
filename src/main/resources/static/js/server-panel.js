let consoleUpdateIntervalId = null;

$("#delete-button").on("click", function () {
    console.log("Showing delete server confirmation...");
    $("#delete-confirmation-modal").modal("show");
});

$("#delete").on("click", function (){
    console.log("Deleting server...");
    let serverId = $("#server-id").val()
    $.ajax({
        url: "/api/servers/" + serverId,
        method: "DELETE"
    }).done(function (response){
        window.location.href = "/";

    });
});

function showStartStopButton() {
    let serverId = $("#server-id").val();
    $.ajax({
        url: "/api/servers/status/" + serverId,
        method: "GET",
        dataType: "text"
    }).done(function (response) {
        console.log("Server is " + response);
        if(response === "Running")
        {
            $("#toggle-button").className= "nav-link";
            $("#toggle-button").style = "color: #007bff;text-decoration: none;background-color: transparent; cursor: pointer";
            $("#toggle-button").text("Stop Server");
        }
        else
        {
            $("#toggle-button").className= "nav-link";
            $("#toggle-button").style = "color: #007bff;text-decoration: none;background-color: transparent; cursor: pointer";
            $("#toggle-button").text("Start Server");
        }
        $("#toggle-button").on("click", function (event) {
            let serverId = $("#server-id").val();

            let button = $(this);
            button.prop("disabled", true);
            button.text("");
            button.append("<span class='spinner-border spinner-border-sm' role='status' aria-hidden='true'></span>");

            $.ajax({
                url: "/api/servers/" + serverId + "/toggle",
                method: "POST"
            }).done(function (response) {
                location.reload();
            });
        });
    });
}


function updateConsole() {
    let serverId = $("#server-id").val();

    $.ajax({
        url: "/api/servers/" + serverId + "/latest-log/" + 20,
        method: "GET"
    }).done(function (response) {
        console.log(response);
        let serverConsole = document.getElementById("console");
        serverConsole.value = response.join("\n");
        serverConsole.scrollTop = serverConsole.scrollHeight;
    });
}

showStartStopButton();
consoleUpdateIntervalId = setInterval(updateConsole, 5000);

$("#save-settings").on("click", function () {

    let serverId = $("#server-id").val();
    let button = $(this);
    button.prop("disabled", true);
    button.text("");
    button.append("<span class='spinner-border spinner-border-sm' role='status' aria-hidden='true'></span>");

    let json = {};
    let properties = $("#server-settings").find("input[id^='property']");
    for (const property of properties) {
        json[$(property).attr("name")] = $(property).val();
    }

    console.log("Saving settings = " + JSON.stringify(json));

    $.ajax({
        url: "/api/servers/" + serverId + "/settings",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(json)
    }).done(function () {
        location.reload();
    });
});

$("#console-input").on("keydown", function (event) {
    if (event.code === "Enter") {
        let serverId = $("#server-id").val();
        let command = $("#console-input").val();
        $("#console-input").val("");

        $.ajax({
            url: "/api/servers/" + serverId + "/command",
            method: "POST",
            contentType: "text/plain",
            data: command
        }).done(function (response) {
            console.log("Command sent!");
            console.log(response);
        });
    }
});