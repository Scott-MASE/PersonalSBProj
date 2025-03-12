$(document).ready(function() {
    // Listen for the login form submit event
    document.getElementById('login-form').addEventListener('submit', function(event) {
        event.preventDefault();  // Prevent the default form submission

        // Get values from form inputs
        var username = document.getElementById('username').value;
        var password = document.getElementById('password').value;

        // Call the login function to process the login request
        loginUser(username, password);
    });

    // Function to handle login request
    function loginUser(username, password) {
        // URL for the login endpoint
        const url = '/api/users/login';

        // Prepare the login data to send in the request body as JSON
        const loginData = {
            username: username,
            password: password
        };

        // Make the login request using the fetch API
        fetch(url, {
            method: 'POST',  // HTTP method for the login
            headers: {
                'Content-Type': 'application/json',  // Specify content type as JSON
            },
            body: JSON.stringify(loginData)  // Send the login data as a JSON string
        })
        .then(response => {
            if (response.ok) {  // If the response is OK (status 200)
                return response.json();  // Parse the JSON response
            } else if (response.status === 401) {  // Unauthorized (invalid credentials)
                showAlert('Invalid username or password!', 'danger');
            } else {
                showAlert('Something went wrong. Please try again.', 'danger');
            }
        })
        .then(data => {
            if (data && data.jwt) {
                // Successful login, show success alert
                showAlert('Login successful!', 'success');
                console.log(data.jwt);
                
                // Save JWT token in storage
                TokenStorage.saveToken(data.jwt);

                // Decode the JWT token to extract the role
                const decodedToken = jwt_decode(data.jwt);
                const role = decodedToken.role;

                // Save the role in localStorage
                localStorage.setItem("role", role);
				
				console.log(role);

                // Redirect based on role
                if (role === "Admin") {
					console.log("Loading Admin Content")
                    loadPage("content/admin.html", "javascript/admin.js")
                } else if (role === "User") {
					console.log("Loading user Content")
                    loadPage("content/dashboard.html", "javascript/dashboard.js")
                } 
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('An error occurred during login. Please try again.', 'warning');
        });
    }

    // Function to show alert messages
    function showAlert(message, type) {
        // Create a Bootstrap alert and append it to the body or a specific container
        const alertElement = `<div class="alert alert-${type}" role="alert">${message}</div>`;
        $('body').append(alertElement);  // Append the alert to the body

        // Optionally, auto-remove the alert after 5 seconds
        setTimeout(() => {
            $(alertElement).fadeOut('slow', function() {
                $(this).remove();
            });
        }, 5000);
    }

    // Password visibility toggle functionality
    const passwordInput = document.getElementById('password');
    const toggleButton = document.getElementById('togglePassword');

    if (toggleButton) {  // Check if the toggle button exists before adding event listener
        toggleButton.addEventListener('click', function() {
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';  // Show password
                toggleButton.innerHTML = '<i class="bx bx-show"></i>';  // Update the icon
            } else {
                passwordInput.type = 'password';  // Hide password
                toggleButton.innerHTML = '<i class="bx bx-hide"></i>';  // Update the icon
            }
        });
    }
});
