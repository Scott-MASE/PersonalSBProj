$(document).ready(function() {
	
	$('#reg-link').on('click', function () {
	    loadPage(registrationh, registrationj);
	});
	
    // Listen for the login form submit event
    document.getElementById('login-form').addEventListener('submit', function(event) {
        event.preventDefault();  // Prevent the default form submission

        // Get values from form inputs
        var username = document.getElementById('username').value;
        var password = document.getElementById('password').value;


        loginUser(username, password);
    });


    function loginUser(username, password) {

        const url = '/api/users/login';


        const loginData = {
            username: username,
            password: password
        };


        fetch(url, {
            method: 'POST',  
            headers: {
                'Content-Type': 'application/json', 
            },
            body: JSON.stringify(loginData) 
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

                console.log(data.jwt);
                

                TokenStorage.saveToken(data.jwt);

                // Decode the JWT token to extract the role
                const decodedToken = jwt_decode(data.jwt);
                const role = decodedToken.role;

                // Save the role in localStorage
                localStorage.setItem("role", role);
				localStorage.setItem('username', username);
				
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
