function loadScript(src, callback) {
    var script = document.createElement('script');
    script.src = src;
    script.type = 'text/javascript';
    script.onload = callback;
    document.head.appendChild(script);
}

function unloadScript(src) {
    $("script[src='" + src + "']").remove();
}

function loadPage(page, jsFile) {
    $("#dynamic-content").load(page, function() {
        if (jsFile) {
            $("script[src='" + jsFile + "']").remove();
        }

        if (jsFile) {
            loadScript(jsFile, function() {
                console.log(jsFile + " has been loaded.");
            });
        }

    });
}



$(document).ready(function() {


	loadPage("content/login.html", "javascript/login.js");


	$("#switch-to-registration").on("click", function() {
		loadPage("content/registration.html", "javascript/registration.js");
	});
});
