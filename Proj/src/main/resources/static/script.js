window.onload = function() {
    // Function to load content dynamically
    loadLoginPage();
};

function loadLoginPage() {
    fetch('content/login.html')
        .then(response => response.text())
        .then(html => {
            document.getElementById('app').innerHTML = html;
        })
        .catch(error => {
            console.error('Error loading the login page:', error);
        });
}
