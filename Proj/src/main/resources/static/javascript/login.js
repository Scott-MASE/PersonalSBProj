$(document).ready(function() {

	
	
	
	$('#reg-link').off('click').on('click', function(e) {
	    e.preventDefault(); // Prevent the default behavior of the link
	    loadPage("content/registration.html", "javascript/registration.js");
	});

	
	
});
