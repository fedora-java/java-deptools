$(function() {
    $(".ellipsized").each(function() {
        var t = this.textContent.trim();
        if (t.length > 30) {
            var ellipsized = t.substring(0, 16) + "..." + t.substring(t.length - 13, t.length);
            if ($(this).attr("title")) {
                $(this).html(ellipsized);
            } else {
                $(this).html($("<span/>").attr({"title": t}).html(ellipsized));
            }
        }
    });
    changeQueryType();
    reflowManifest();
});

function reflowManifest() {
    $(".manifest-value").each(function() {
        this.textContent = this.textContent.match(/.{0,80}/g).join(" ");
    });
}

function changeQueryType() {
    var transition = {
            "classes": ["Class name:"],
            "manifests": ["Header:", "Value:"]
    }[$("#qtype")[0].value];
    $("#q2").css("display", transition[1]? "inline": "none").prop("disabled", !transition[1]);
    $("label[for='q']").text(transition[0]);
    $("label[for='q2']").text(transition[1] || "");
}