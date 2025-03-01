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

document.addEventListener("DOMContentLoaded", function () {
	loadLoginPage();
});
