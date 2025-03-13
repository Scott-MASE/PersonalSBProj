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

function showAlert(message, type) {
    const alertElement = $(`<div class="alert alert-${type}" role="alert">${message}</div>`);

    alertElement.css({
        position: 'absolute',
        top: '20px',  
        left: '50%',
        transform: 'translateX(-50%)', 
        zIndex: 9999,  
        width: 'auto',
        maxWidth: '80%',  
        margin: '0 auto',
    });

    $('body').append(alertElement);  // Append the alert to the body

    // Optionally, auto-remove the alert after 2 seconds (2000ms)
    setTimeout(() => {
        alertElement.fadeOut('slow', function() {
            $(this).remove();
        });
    }, 2000);
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
