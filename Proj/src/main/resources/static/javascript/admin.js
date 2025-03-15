$(document).ready(function () {
    const apiUrl = "/api/users"; // Base API URL

    // Logout button
    $('#admin-logout-button').on('click', function () {
		logout()
    });

    // Fetch users from backend and populate the table
	function fetchUsers() {
	    $.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        url: apiUrl,
	        method: "GET",
	        dataType: "json",
	        success: function (users) {
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
	        },
	        error: function () {
	            showAlert("Failed to load users.", "warning");
	        }
	    });
	}


    // Show edit user modal and populate fields
	window.editUser = function (id) {
	    $.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },

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
        let userId = $(this).attr("data-user-id");
        let updatedUser = {
            username: $('#username').val(),
            password: $('#password').val() || null, 
            role: $('#userType').val()
        };

        $.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
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
				headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
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
