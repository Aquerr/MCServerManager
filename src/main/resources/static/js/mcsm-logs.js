function loadLogs() {
    console.log("Getting MCSM logs...");
    $.ajax({
        method: "GET",
        url: "/api/mcsm/logs",
        success: function (response) {
            $("#mcsm-logs").append(response.join("\n"));
        }
    });
}

loadLogs();