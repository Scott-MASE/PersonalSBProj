const loginh = "content/login.html";
const loginj = "javascript/login.js";

const dashboardh = "content/dashboard.html";
const dashboardj = "javascript/dashboard.js";

const registrationh = "content/registration.html";
const registrationj = "javascript/registration.js";

const adminh = "content/admin.html";
const adminj = "javascript/admin.js";

let username = "Guest";
let logged_userId = 1;

function unloadScript(src) {
    $(`script[src='${src}']`).remove();
}

function loadPage(page, jsFile) {
    $("#dynamic-content").load(page, () => {
        if (jsFile) {
            $(`script[src='${jsFile}']`).remove();
        }
    });
}

function logout() {
    console.log("Logging out");
    sessionStorage.clear();
    localStorage.clear();
    TokenStorage.removeToken();
    loadPage(loginh, loginj);
}

function showAlert(message, type) {
    const alertElement = $(`<div class="alert alert-${type}" role="alert">${message}</div>`);

    alertElement.css({
        position: 'absolute',
        top: '20%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        zIndex: 9999,
        width: 'auto',
        maxWidth: '80%',
        margin: '0 auto'
    });

    $('body').append(alertElement);

    setTimeout(() => {
        alertElement.fadeOut('slow', function() {
            $(this).remove();
        });
    }, 4000);
}

$(document).ready(() => {
    const isLoggedIn = localStorage.getItem("token");

    if (!isLoggedIn) {
        loadPage(loginh, loginj);
    } else {
        const role = localStorage.getItem("role");
        if (role) {
            if (role === "User" || role === "Moderator") {
                loadPage(dashboardh, dashboardj);
                console.log("logged user");
            } else if (role === "Admin") {
                loadPage(adminh, adminj);
                console.log("logged admin");
            }
        }
    }
});
