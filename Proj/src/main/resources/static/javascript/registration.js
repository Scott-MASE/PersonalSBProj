$(document).ready(() => {
    // Handle registration button click
    $("#register-btn").off("click").on("click", function (event) {
        event.preventDefault(); // Prevent default form submission
        console.log("LOADING");

        const username = $("#username").val().trim();
        const password = $("#password").val();
        const confirmPassword = $("#confirm-password").val();

        if (username.length < 2) {
            showAlert("Username must be at least 2 characters long.", "warning");
            return;
        }

        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[*@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if (!passwordRegex.test(password)) {
            showAlert("Password must be at least 8 characters, include one uppercase, one lowercase, one number, and one special character (*@$!%*?&).", "warning");
            return;
        }

        if (password !== confirmPassword) {
            showAlert("Passwords do not match.", "warning");
            return;
        }

        // Debug log for verifying the request
        console.log("Sending AJAX request to /api/users/register:", { username, password, role: "USER" });

        $.ajax({
            type: "POST",
            url: "/api/users/register",
            contentType: "application/json",
            data: JSON.stringify({ username, password, role: "USER" }),
            success: function (response) {
                console.log(response);
                loadPage(loginh, loginj); // Load login page after successful registration
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
	
    // Handle log link click
    $('#log-link').off('click').on('click', function (e) {
        e.preventDefault(); 
        loadPage(loginh, loginj);
    });
});
