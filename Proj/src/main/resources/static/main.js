function loadPage(page) {
    fetch(`content/${page}.html`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('app').innerHTML = html;
            attachEventListeners();
        })
        .catch(error => {
            console.error(`Error loading the ${page} page:`, error);
        });
}

function attachEventListeners() {
    // Attach event listeners after the page loads
    const loginLink = document.querySelector('.login-link a');
    const signupLink = document.querySelector('.signup-link a');

    if (loginLink) {
        loginLink.addEventListener('click', function (event) {
            event.preventDefault();
            loadPage('login');
        });
    }

    if (signupLink) {
        signupLink.addEventListener('click', function (event) {
            event.preventDefault();
            loadPage('registration');
        });
    }
}

//awawdawd

document.addEventListener("DOMContentLoaded", function () {
    loadPage('login'); // Default page to load
});


