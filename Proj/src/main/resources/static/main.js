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

        // Update the URL without reloading the page
        var pageName = page.split('/').pop().split('.')[0]; // Extract file name without extension
        history.pushState({ page: pageName }, null, `${pageName}`); // Update the URL with the page name
    });
}

// Handle popstate event (back/forward buttons in the browser)
$(window).on('popstate', function(event) {
    // Get the page from the state object
    var pageName = event.originalEvent.state ? event.originalEvent.state.page : 'login'; // Default to 'login' if no state
    var page = `content/${pageName}.html`; // Construct the path to the page
    var jsFile = `javascript/${pageName}.js`; // Construct the path to the JS file
    
    loadPage(page, jsFile); // Load the appropriate page based on the URL state
});

// Initially load the login page with login.js
var initialPage = window.location.hash.substring(1) || 'login'; // Default to 'login' if no hash
loadPage(`content/${initialPage}.html`, `javascript/${initialPage}.js`);

// Example: Trigger page change to registration page with registration.js (for demonstration)
$("#switch-to-registration").on("click", function() {
    loadPage("content/registration.html", "javascript/registration.js");
});

$(document).ready(function() {


	loadPage("content/login.html", "javascript/login.js");


	$("#switch-to-registration").on("click", function() {
		loadPage("content/registration.html", "javascript/registration.js");
	});
});
