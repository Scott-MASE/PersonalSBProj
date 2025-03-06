$(document).ready(function() {

	
	
	
	$('#reg-link').off('click').on('click', function(e) {
	    e.preventDefault(); // Prevent the default behavior of the link
	    loadPage("content/registration.html", "javascript/registration.js");
	});
	
	$('#login-form').off('submit').on('submit', function(e) {
	    e.preventDefault(); 
	    
	    var username = $('#username').val();
	    var password = $('#password').val();


	    $.ajax({
	        url: '/api/users/login',
	        method: 'POST',
	        data: {
	            username: username,
	            password: password
	        },
	        success: function(response) {
	            if (response.message === "Success") {
	                loadPage("content/registration.html", "javascript/registration.js");
	            } else {
	                alert(response.message); 
	            }
	        },
	        error: function(xhr, status, error) {
	            alert("An error occurred: " + error);
	        }
	    });
	});

	
	
});
