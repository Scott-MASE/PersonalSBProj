const loginh = "content/login.html";
const loginj = "javascript/login.js";

const dashboardh = "content/dashboard.html";
const dashboardj = "javascript/dashboard.js";

const registrationh = "content/registration.html";
const registrationj = "javascript/registration.js";




function unloadScript(src) {
    $("script[src='" + src + "']").remove();
}

function loadPage(page, jsFile) {
    $("#dynamic-content").load(page, function() {
        if (jsFile) {
            $("script[src='" + jsFile + "']").remove();
        }


    });
}



$(document).ready(function() {


	loadPage(dashboardh, dashboardj);
//	loadPage(registrationh, registrationj);
//	loadPage(loginh, loginj);

});
