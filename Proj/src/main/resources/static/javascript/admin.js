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
            success: (users) => {
                const tableBody = $("#user-table-body").empty();
                users.forEach(user => {
                    const row = `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.role}</td>
                            <td>
                                <button class="edit-btn" data-id="${user.id}">Edit</button>
                                <button class="delete-btn" data-id="${user.id}" data-username="${user.username}">Delete</button>
                            </td>
                        </tr>
                    `;
                    tableBody.append(row);
                });
            },
            error: () => showAlert("Failed to load users.", "warning")
        });
    }

    // On click listener for edit button
    $(document).on('click', '.edit-btn', function() {
        const userId = $(this).data("id");
        editUser(userId);
    });

    $(document).on('click', '.delete-btn', function() {
        const userId = $(this).data("id");
        const username = $(this).data("username");
        // Update modal title to "Delete {username}?"
        $("#deleteUserModalLabel").text(`Delete ${username}?`);
        // Store user id in modal and show modal
        $("#deleteUserModal").data("user-id", userId).modal("show");
    });

    // Show edit user modal and populate fields
    window.editUser = function(id) {
        $.ajax({
            headers: authHeader,
            url: `${apiUrl}/${id}`,
            type: "GET",
            contentType: "application/json",
            success: (user) => {
                $('#username').val(user.username);
                $('#password').val("");
                $('#userType').val(user.role);
                $('#save-or-create').attr("data-user-id", id);
                $('#editUserModal').modal('show');
            },
            error: () => showAlert("Failed to fetch user data. Make sure you are logged in.", "warning")
        });
    };

    // Save changes after editing user
    $('#save-or-create').on("click", function(e) {
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
            success: () => {
                showAlert("User updated successfully.", "success");
                $('#editUserModal').modal('hide');
                fetchUsers();
            },
            error: () => showAlert("Failed to update user.", "warning")
        });
    });

    // Handle deletion using modal confirmation
    $("#confirmDelete").on("click", () => {
        const userId = $("#deleteUserModal").data("user-id");
        $.ajax({
            headers: authHeader,
            url: `${apiUrl}/delete/${userId}`,
            type: "DELETE",
            success: () => {
                showAlert("User deleted.", "success");
                $("#deleteUserModal").modal("hide");
                fetchUsers();
            },
            error: () => showAlert("Failed to delete user.", "warning")
        });
    });

    // Initial fetch of users
    fetchUsers();
});
