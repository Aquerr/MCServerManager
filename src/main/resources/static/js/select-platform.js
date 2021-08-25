function registerPlatformButtons() {
    $(".card-clickable").on('click', function () {
        clickPlatform(this);
    });
}

function clickPlatform(clickedPlatformCard) {
    let platformTitle = $(clickedPlatformCard).find(".card-title").text();
    console.log("Chosen platform = " + platformTitle);
    window.location.href = "/servers/add-server?platform=" + platformTitle;
}

registerPlatformButtons();