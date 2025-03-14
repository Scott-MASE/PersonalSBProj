$(document).ready(function () {
    $("#register-form").submit(function (event) {
        event.preventDefault(); // ✅ Prevents default form submission

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
                alert("Registration successful! Redirecting to login...");
                window.location.href = "/login"; // ✅ Redirect on success
            },
            error: function (xhr) {
                console.error("Error response:", xhr.responseText);
                alert(xhr.responseText || "Registration failed. Please try again.");
            }
        });
    });
});
