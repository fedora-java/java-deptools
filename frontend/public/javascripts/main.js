$(function() {
    $("#class-table tr td:first-child").each(function() {
        var t = this.textContent;
        if (t.length > 30) {
            var elipsized = t.substring(0, 16) + "..." + t.substring(t.length - 13, t.length);
            $(this).html($("<span/>").attr({"title": t}).html(elipsized));
        }
    });
});