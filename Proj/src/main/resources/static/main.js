const loginh = "content/login.html";
const loginj = "javascript/login.js";

const dashboardh = "content/dashboard.html";
const dashboardj = "javascript/dashboard.js";

const registrationh = "content/registration.html";
const registrationj = "javascript/registration.js";

const adminh = "content/admin.html";
const adminj = "javascript/admin.js";

var username = "Guest";
var logged_userId = 1;




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
	
	//Authentication Check		
	let isLoggedIn = localStorage.getItem("token");

	if (!isLoggedIn) {
		loadPage(loginh, loginj);
	} else {
		let role = localStorage.getItem("role");
		        if (role) {
		            loadSidebarLinks(role);
		            loadContentByRole(role);
					console.log(role);
		        }
	}

//	loadPage(dashboardh, dashboardj);
//	loadPage(registrationh, registrationj);
	
//	loadPage(adminh, adminj);

});
