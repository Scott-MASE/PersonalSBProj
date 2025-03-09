const loginh = "content/login.html";
const loginj = "javascript/login.js";

const dashboardh = "content/dashboard.html";
const dashboardj = "javascript/dashboard.js";

const registrationh = "content/registration.html";
const registrationj = "javascript/registration.js";


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


//	loadPage(dashboardh, dashboardj);
//	loadPage(registrationh, registrationj);
	loadPage(loginh, loginj);

});
