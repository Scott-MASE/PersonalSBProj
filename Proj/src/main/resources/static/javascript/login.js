

$(document).ready(function() {

	
	
	
	$('#reg-link').off('click').on('click', function(e) {
	    e.preventDefault(); // Prevent the default behavior of the link
	    loadPage(registrationh, registrationj);
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
	                // Store user details in localStorage
	                logged_userId = response.userId;
	                username = response.username;
	                let role = response.role; // Ensure backend sends this

	                localStorage.setItem("userId", response.userId);
	                localStorage.setItem("username", response.username);
	                localStorage.setItem("role", response.role);

	                console.log("Logged in as:", username, "Role:", role);

	                // Redirect based on role
	                if (role === "ADMIN") {
	                    loadPage(adminh, adminj);
	                } else if (role === "USER" || role === "MODERATOR") {
	                    loadPage(dashboardh, dashboardj);
	                } else {
	                    alert("Unknown role. Redirecting to dashboard.");
	                    loadPage(dashboardh, dashboardj);
	                }
	            } else {
	                alert(response.message); 
	            }
	        },
	        error: function(xhr, status, error) {
	            alert("An error occurred: " + error);
	        }
	    });
	});

	
	let storedUserId = localStorage.getItem("userId");
	if (storedUserId) {
	    logged_userId = parseInt(storedUserId, 10);
	}
	let storedUsername = localStorage.getItem("username");
	if (storedUsername) {
		username = storedUsername;
	}

	
	
});
