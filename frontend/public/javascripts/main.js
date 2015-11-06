$(function() {
    $("#class-table tr td:first-child").each(function() {
        var t = this.textContent;
        if (t.length > 30) {
            var elipsized = t.substring(0, 16) + "..." + t.substring(t.length - 13, t.length);
            $(this).html($("<span/>").attr({"title": t}).html(elipsized));
        }
    });
    changeQueryType();
});

function changeQueryType() {
    var transition = {
            "classes": ["Class name:"],
            "manifests": ["Header:", "Value:"]
    }[$("#qtype")[0].value];
    $("#q2").css("display", transition[1]? "inline": "none").prop("disabled", !transition[1]);
    $("label[for='q']").text(transition[0]);
    $("label[for='q2']").text(transition[1] || "");
}