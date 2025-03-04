window.onload = function() {
    // Function to load content dynamically
    loadLoginPage();
};

$(document).ready(function () {

	if (!isLoggedIn) {
		// Force user to login if not authenticated
		$("#dynamic-content").load("content/login.html");


};
