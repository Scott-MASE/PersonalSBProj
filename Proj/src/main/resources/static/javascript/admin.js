$(document).ready(function () {
    const apiUrl = "/api/users"; // Base API URL

    // Logout button
    $('#admin-logout-button').on('click', function () {
        console.log("Logging out");
        username = "Guest";
        loadPage(loginh, loginj);
    });

    // Fetch users from backend and populate the table
    function fetchUsers() {
        $.get(apiUrl, function (users) {
            let tableBody = $("#user-table-body");
            tableBody.empty();

            users.forEach(user => {
                let row = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.role}</td>
                        <td>
                            <button class="edit-btn" onclick="editUser(${user.id})">Edit</button>
                            <button class="delete-btn" onclick="deleteUser(${user.id})">Delete</button>
                        </td>
                    </tr>
                `;
                tableBody.append(row);
            });
        }).fail(function () {
            alert("Failed to load users.");
        });
    }

    // Show edit user modal and populate fields
    window.editUser = function (id) {
        $.get(`${apiUrl}/${id}`, function (user) {
            $('#username').val(user.username);
            $('#password').val(""); // Clear password field for security
            $('#userType').val(user.role);
            $('#save-or-create').attr("data-user-id", id);
            $('#editUserModal').modal('show');
        }).fail(function () {
            alert("Failed to fetch user data.");
        });
    };

    // Save changes after editing user
    $('#save-or-create').on("click", function (e) {
        e.preventDefault();
        let userId = $(this).attr("data-user-id");
        let updatedUser = {
            username: $('#username').val(),
            password: $('#password').val() || null, // Only update if provided
            role: $('#userType').val()
        };

        $.ajax({
            url: `${apiUrl}/update/${userId}`,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(updatedUser),
            success: function () {
                alert("User updated successfully.");
                $('#editUserModal').modal('hide');
                fetchUsers(); // Refresh table
            },
            error: function () {
                alert("Failed to update user.");
            }
        });
    });

    // Delete user
    window.deleteUser = function (id) {
        if (confirm("Are you sure you want to delete this user?")) {
            $.ajax({
                url: `${apiUrl}/delete/${id}`,
                type: "DELETE",
                success: function () {
                    alert("User deleted.");
                    fetchUsers(); // Refresh table
                },
                error: function () {
                    alert("Failed to delete user.");
                }
            });
        }
    };

    // Fetch users on page load
    fetchUsers();
});
