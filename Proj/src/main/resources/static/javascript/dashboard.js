$(document).ready(function() {
	

	
	
	$('#create-note').off('click').on('click', function(e) {
	    e.preventDefault(); // Prevent default behavior
	    console.log("Opening modal...");
	    $('#createNoteModal').modal('show'); // Open Bootstrap modal
	});
	
	$('#noteForm').off('submit').on('submit', function(event) {
	    event.preventDefault(); // Prevent default form submission
		console.log("creating")
		
		let userId = localStorage.getItem("userId");
		console.log(userId)
		
		if (!userId) {
		    alert("User is not logged in.");
		    return;  // If user ID is not found, stop the submission
		}

	    // Extract form data
	    let noteData = {
	        title: $("#noteTitle").val().trim(),
	        content: "",
	        tag: $("#noteTag").val().trim(),
	        priority: $("#notePriority").val(),
	        deadline: $("#noteDeadline").val() ? new Date($("#noteDeadline").val()).toISOString() : null,
	        user: {
	            id: 1, // Replace with actual logged-in user ID
	            username: "testUser",
	            role: "USER"
	        }
	    };

	    // Send AJAX request
	    $.ajax({
	        type: "POST",
	        url: "/api/notes/create",
	        contentType: "application/json",
	        data: JSON.stringify(noteData),
	        success: function (response) {
	            alert("Note created successfully!");
	            
	            // Close the modal
	            let modalElement = $("#createNoteModal");
	            let modalInstance = bootstrap.Modal.getInstance(modalElement[0]);
	            modalInstance.hide();

	            // Reset form
	            $("#noteForm")[0].reset();
	        },
	        error: function (xhr, status, error) {
	            console.error("Error:", error);
	            alert("Failed to create note.");
	        }
	    });
	});
	
});
