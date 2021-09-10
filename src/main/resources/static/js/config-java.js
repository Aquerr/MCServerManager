const newJavaTemplate = "<div class=\"card\">\n" +
    "                  <div th:id=\"${'collapse' + javaDto.id}\">\n" +
    "                      <div class=\"card-body\">\n" +
    "                          <input class=\"java-id form-control\" type=\"number\" hidden>\n" +
    "                          <div class=\"form-group\">\n" +
    "                              <label>Name</label>\n" +
    "                              <input class=\"java-name form-control\" placeholder=\"Name...\">\n" +
    "                          </div>\n" +
    "                          <div class=\"form-group\">\n" +
    "                              <label>Path (to Java executable file)</label>\n" +
    "                              <input class=\"java-path form-control\" placeholder=\"C:\\Program Files\\Java\\jre-1.8\\bin\\java\">\n" +
    "                          </div>\n" +
    "                          <button class=\"btn btn-primary java-save\">Save</button>\n" +
    "                      </div>\n" +
    "                  </div>\n" +
    "              </div>"

$("#add-java").on("click", function () {
    $("#java-list").append(newJavaTemplate);
    unregisterSaveEventListeners();
    registerSaveEventListeners();
});

$(".java-delete").on("click", function (event) {
    const javaId = $(event.target).attr("java-id");

    $.ajax({
        method: "DELETE",
        url: "/api/config/java/" + javaId
    }).always(function (response) {
        console.log(response);
        window.location.reload();
    });
});

function unregisterSaveEventListeners() {
    $(".java-save").unbind("click");
}

function registerSaveEventListeners() {
    $(".java-save").on("click", function (event) {
        const btnElement = $(event.target);
        const cardBodyElement = btnElement.parent();
        const javaId = $(cardBodyElement).find(".java-id").val();
        const javaName = $(cardBodyElement).find(".java-name").val();
        const javaPath = $(cardBodyElement).find(".java-path").val();

        const javaDto = {};
        javaDto["id"] = javaId;
        javaDto["name"] = javaName;
        javaDto["path"] = javaPath;

        $.ajax({
            method: "POST",
            url: "/api/config/java",
            data: JSON.stringify(javaDto),
            contentType: "application/json"
        }).always(function (response) {
            console.log(response);
            window.location.reload();
        });
    });
}

registerSaveEventListeners();