$(document).ready(function () {
    $("#register-btn").off("click").on("click", function (event) {
        event.preventDefault(); // ✅ Prevents default form submission
		
		console.log("LOADING");

        let username = $("#username").val().trim();
        let password = $("#password").val();
        let confirmPassword = $("#confirm-password").val();

        if (username.length < 2) {
            alert("Username must be at least 2 characters long.");
            return;
        }

        let passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[*@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if (!passwordRegex.test(password)) {
            alert("Password must be at least 8 characters, include one uppercase, one lowercase, one number, and one special character (*@$!%*?&).");
            return;
        }

        if (password !== confirmPassword) {
            alert("Passwords do not match.");
            return;
        }

        // Debugging: Log to console to verify request
        console.log("Sending AJAX request to /api/users/register:", { username, password, role: "USER" });

        $.ajax({
            type: "POST",
            url: "/api/users/register", // ✅ Ensure this matches the backend route
            contentType: "application/json",
            data: JSON.stringify({
                username: username,
                password: password,
                role: "USER"
            }),
            success: function (response) {
                loadPage("content/login.html", "javascript/login.js");
            },
            error: function (xhr) {
                console.error("Error response:", xhr.responseText);
                alert(xhr.responseText || "Registration failed. Please try again.");
            }
        });
    });
	
	$('#log-link').off('click').on('click', function(e) {
	    e.preventDefault(); // Prevent the default behavior of the link
	    loadPage("content/login.html", "javascript/login.js");
	});
});
