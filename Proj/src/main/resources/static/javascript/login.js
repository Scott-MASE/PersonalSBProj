$(document).ready(function() {
    // Add click event listener to the login button
	if (!$("#login-btn").data("clicked")) {
	    $("#login-btn").on("click", function() {
	       loadPage("content/registration.html", "javascript/registration.js");
	    });

	    // Mark the button as having the event listener attached
	    $("#login-btn").data("clicked", true);
	}
	
});
