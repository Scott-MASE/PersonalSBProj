$(document).ready(function() {
	
	var rootURL = "http://localhost:9092";

	
//	$.ajax({
//	    type: "GET",
//	    url: rootURL + "/api/users/username",  // Your endpoint to fetch the username
//	    success: function(response) {
//	        // Update the <h4> with the username
//	        $('#username').text(response);
//	    },
//	    error: function(xhr, status, error) {
//			console.error("Error fetching username: ", error);
//			$('#username').text("Guest"); // Display "Guest" if not authenticated
//	    }
//	});


//editNoteModal

	
	$('#create-note').off('click').on('click', function(e) {
	    e.preventDefault(); // Prevent default behavior
	    console.log("Opening modal...");
	    $('#createNoteModal').modal('show'); // Open Bootstrap modal
	});
	
	$('#noteForm').off('submit').on('submit', function(event) {
	    event.preventDefault(); // Prevent default form submission
		console.log("creating")
		
//		let userId = localStorage.getItem("userId");
//		console.log(userId)
		
//		if (!userId) {
//		    alert("User is not logged in.");
//		    return;  // If user ID is not found, stop the submission
//		}

	    // Extract form data
	    let noteData = {
	        title: $("#noteTitle").val().trim(),
	        content: "",
	        tag: $("#noteTag").val().trim(),
	        priority: $("#notePriority").val(),
	        deadline: $("#noteDeadline").val() ? new Date($("#noteDeadline").val()).toISOString() : null,
	        user: "1"
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
				findAllNotes();
	        },
	        error: function (xhr, status, error) {
	            console.error("Error:", error);
	            alert("Failed to create note.");
	        }
	    });
	});
	
	var findAllNotes = function() {
		console.log("Find all notes");
		$.ajax({
			type: 'GET',
			url: "api/notes/getAll",
			dataType: 'json',
			success: renderNotes,
			error: function(xhr, status, error) {
				$(".details").remove();
				console.log("failed")
			}


		})
	};
	
	var renderNotes = function(data) {
	    console.log("populating notes");
	    $('.scrollview').empty(); // Assuming this is your parent container for the note tiles
	    
	    $.each(data, function(index, note) {
	        let htmlStr = '<button class="note-tile-btn" data-note-id="' + note.id + '" data-note-title="' + note.title + '" data-note-content="' + note.content + '">';
	        htmlStr += '<div class="note-tile">';
	        htmlStr += '<p class="note-title">' + note.title + '</p>';
	        htmlStr += '<p class="note-tags">Tag: <span>' + note.tag + '</span></p>';
	        htmlStr += '<p class="note-priority">Priority: <span>' + note.priority + '</span></p>';
	        htmlStr += '<p class="note-deadline">Deadline:<br><span>' + note.deadline + '</span></p>';
	        htmlStr += '</div>';
	        htmlStr += '</button>';
	        $('.scrollview').append(htmlStr);
	    });
	};
	
	$(document).on("click", ".note-tile-btn", function() {
	    let noteId = $(this).data("note-id");
	    let noteTitle = $(this).data("note-title");
	    let noteContent = $(this).data("note-content");

	    $("#editNoteTitle").text(noteTitle); // Set title
	    $("#editNoteContent").val(noteContent); // Set content
	    $("#editNoteForm").data("note-id", noteId); // Store ID in form

	    $("#editNoteModal").modal("show"); // Open modal
	});

	
	$("#editNoteForm").on("submit", function(event) {
	    event.preventDefault(); // Prevent form submission

	    let noteId = $(this).data("note-id");
	    let updatedContent = $("#editNoteContent").val();

	    $.ajax({
	        url: "/api/notes/" + noteId + "/content",
	        type: "PUT",
	        contentType: "application/json",
	        data: JSON.stringify(updatedContent), // Send raw string, not an object
	        success: function(response) {
	            alert("Note updated successfully!");
	            $("#editNoteModal").modal("hide"); // Close modal
	            findAllNotes();
	        },
	        error: function(xhr) {
	            alert("Failed to update note: " + xhr.responseText);
	        }
	    });
	});


	
	$("#closeModalBtn").on("click", function() {
	    $("#noteModal").fadeOut();
	});

	findAllNotes();

	
});
