$(document).ready(function () {
    $("#register-btn").off("click").on("click", function (event) {
        event.preventDefault(); // ✅ Prevents default form submission
		
		console.log("LOADING");

        let username = $("#username").val().trim();
        let password = $("#password").val();
        let confirmPassword = $("#confirm-password").val();

        if (username.length < 2) {
            showAlert("Username must be at least 2 characters long.", "warning");
            return;
        }

        let passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[*@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if (!passwordRegex.test(password)) {
            showAlert("Password must be at least 8 characters, include one uppercase, one lowercase, one number, and one special character (*@$!%*?&).", "warning");
            return;
        }

        if (password !== confirmPassword) {
            showAlert("Passwords do not match.", "warning");
            return;
        }

        // Debugging: Log to console to verify request
        console.log("Sending AJAX request to /api/users/register:", { username, password, role: "USER" });

		$.ajax({
		    type: "POST",
		    url: "/api/users/register",
		    contentType: "application/json",
		    data: JSON.stringify({
		        username: username,
		        password: password,
		        role: "USER"  // Ensure this is what the backend expects
		    }),
		    success: function (response) {
		        console.log(response);
		        loadPage(loginh, loginj);  // Redirect or load the page after successful registration
		    },
		    error: function (xhr) {
		        console.error("Error response:", xhr.responseText);
		        if (xhr.status === 400) {
		            alert("Bad request: Invalid username or password.");
		        } else {
		            alert(xhr.responseText || "Registration failed. Please try again.");
		        }
		    }
		});


    });
	
	$('#log-link').off('click').on('click', function(e) {
	    e.preventDefault(); 
	    loadPage(loginh, loginj);
	});
});
