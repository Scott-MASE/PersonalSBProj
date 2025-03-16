$(document).ready(() => {
    const apiUrl = "/api/users"; // Base API URL
    const authHeader = { Authorization: `Bearer ${TokenStorage.getToken()}` };

    // Logout button
    $('#admin-logout-button').on('click', () => logout());

    // Fetch users from backend and populate the table
    function fetchUsers() {
        $.ajax({
            headers: authHeader,
            url: apiUrl,
            method: "GET",
            dataType: "json",
            success: function (users) {
                const tableBody = $("#user-table-body").empty();
                users.forEach(user => {
                    const row = `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.role}</td>
                            <td>
                                <button class="edit-btn" data-id="${user.id}">Edit</button>
                                <button class="delete-btn" data-id="${user.id}">Delete</button>
                            </td>
                        </tr>
                    `;
                    tableBody.append(row);
                });
            },
            error: function () {
                showAlert("Failed to load users.", "warning");
            }
        });
    }

    // Delegate click event for edit and delete buttons
    $(document).on('click', '.edit-btn', function () {
        const userId = $(this).data("id");
        editUser(userId);
    });

    $(document).on('click', '.delete-btn', function () {
        const userId = $(this).data("id");
        deleteUser(userId);
    });

    // Show edit user modal and populate fields
    window.editUser = function (id) {
        $.ajax({
            headers: authHeader,
            url: `${apiUrl}/${id}`,
            type: "GET",
            contentType: "application/json",
            success: function (user) {
                $('#username').val(user.username);
                $('#password').val(""); 
                $('#userType').val(user.role);
                $('#save-or-create').attr("data-user-id", id);
                $('#editUserModal').modal('show');
            },
            error: function () {
                showAlert("Failed to fetch user data. Make sure you are logged in.", "warning");
            }
        });
    };

    // Save changes after editing user
    $('#save-or-create').on("click", function (e) {
        e.preventDefault();
        const userId = $(this).attr("data-user-id");
        const updatedUser = {
            username: $('#username').val(),
            password: $('#password').val() || null, 
            role: $('#userType').val()
        };

        $.ajax({
            headers: authHeader,
            url: `${apiUrl}/edit/${userId}`,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(updatedUser),
            success: function () {
                showAlert("User updated successfully.", "success");
                $('#editUserModal').modal('hide');
                fetchUsers(); 
            },
            error: function () {
                showAlert("Failed to update user.", "warning");
            }
        });
    });

    window.deleteUser = function (id) {
        if (confirm("Are you sure you want to delete this user?")) {
            $.ajax({
                headers: authHeader,
                url: `${apiUrl}/delete/${id}`,
                type: "DELETE",
                success: function () {
                    showAlert("User deleted.", "success");
                    fetchUsers(); 
                },
                error: function () {
                    showAlert("Failed to delete user.", "warning");
                }
            });
        }
    };

    fetchUsers();
});
