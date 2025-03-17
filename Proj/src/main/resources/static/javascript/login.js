$(document).ready(() => {
    // Registration link event
    $('#reg-link').on('click', () => loadPage(registrationh, registrationj));
    
    // Listen for the login form submit event using vanilla JS
    document.getElementById('login-form').addEventListener('submit', event => {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        loginUser(username, password);
    });

    function loginUser(username, password) {
        const url = '/api/users/login';
        const loginData = { username, password };

        fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginData)
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 401) {
                showAlert('Invalid username or password!', 'danger');
            } else {
                showAlert('Something went wrong. Please try again.', 'danger');
            }
        })
        .then(data => {
            if (data && data.jwt) {
                console.log(data.jwt);
                TokenStorage.saveToken(data.jwt);

                // Decode JWT to extract role
                const decodedToken = jwt_decode(data.jwt);
                const role = decodedToken.role;

                // Save role and username in localStorage
                localStorage.setItem("role", role);
                localStorage.setItem("username", username);
                console.log(role);

                // Redirect based on role
                if (role === "Admin") {
                    console.log("Loading Admin Content");
                    loadPage("content/admin.html", "javascript/admin.js");
                } else if (role === "User" || role === "Moderator") {
                    console.log("Loading user Content");
                    loadPage("content/dashboard.html", "javascript/dashboard.js");
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

    if (toggleButton) {
        toggleButton.addEventListener('click', () => {
            const isPassword = passwordInput.type === 'password';
            passwordInput.type = isPassword ? 'text' : 'password';
            toggleButton.innerHTML = isPassword ? '<i class="bx bx-show"></i>' : '<i class="bx bx-hide"></i>';
        });
    }
});
