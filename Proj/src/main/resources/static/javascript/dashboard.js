$(document).ready(function() {
	
	var rootURL = "http://localhost:9092";

	



	
	$('#create-note').off('click').on('click', function(e) {
	    e.preventDefault(); // Prevent default behavior
	    console.log("Opening modal...");

	    // Reset the form fields
	    $("#noteForm")[0].reset();

	    // Clear any displayed values or selections
	    $("#noteTitle").val('');
	    $("#noteTag").val('');
	    $("#notePriority").val('LOW'); // Reset to default priority if needed
	    $("#noteDeadline").val('');

	    // Update modal title and button text for creating a new note
	    $("#createNoteModalLabel").text("Create Note");
	    $("#save-or-create").text("Create");

	    $('#createNoteModal').modal('show'); // Open Bootstrap modal
	});
	

	
	$('#noteForm').off('submit').on('submit', function(event) {
	    event.preventDefault(); // Prevent default form submission
		let noteId = $("#noteForm").data("note-id");
		if ($("#createNoteModalLabel").text() === "Create Note") {
			console.log("creating");
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

		    
		} else {
			
		    console.log("Editing");
			    
			    // Extract form data for updating an existing note
			    let updatedNoteData = {
			        title: $("#noteTitle").val().trim(),
			        content: "",  // Add content field if you have one
			        tag: $("#noteTag").val().trim(),
			        priority: $("#notePriority").val(),
			        deadline: $("#noteDeadline").val() ? new Date($("#noteDeadline").val()).toISOString() : null
			    };

			    // Send AJAX request for updating the note
			    $.ajax({
			        type: "PUT",
			        url: "/api/notes/" + noteId + "/meta",  // Make sure this matches your PUT endpoint
			        contentType: "application/json",
			        data: JSON.stringify(updatedNoteData),
			        success: function(response) {
			            alert("Note updated successfully!");
			            
			            // Close the modal
			            let modalElement = $("#createNoteModal");
			            let modalInstance = bootstrap.Modal.getInstance(modalElement[0]);
			            modalInstance.hide();

			            // Optionally, reset form fields or update the UI with the new note data
			            $("#noteForm")[0].reset();
			            findAllNotes();  // Optionally refresh the notes list
			        },
			        error: function(xhr, status, error) {
			            console.error("Error:", error);
			            alert("Failed to update note.");
			        }
			    });
			

		}
		

	});
	
	$(document).on("click", ".bi-gear-fill.cog-icon", function() {
	    // Find the closest .note-tile-btn from the clicked cog icon
	    let $noteTileBtn = $(this).closest('.note-tile-container').find('.note-tile-btn');
	    
	    // Retrieve data from the closest .note-tile-btn
	    let noteId = $noteTileBtn.data("note-id");
	    let noteTitle = $noteTileBtn.data("note-title");
	    let noteTag = $noteTileBtn.data("note-tag");
	    let notePriority = $noteTileBtn.data("note-priority");
	    let noteDeadline = $noteTileBtn.data("note-deadline");

	    // Populate the modal with the existing note data
	    $("#noteTitle").val(noteTitle);  // Set title in the input field
	    $("#noteTag").val(noteTag);  // Set tag in the input field
	    $("#notePriority").val(notePriority);  // Set priority in the select field
	    $("#noteDeadline").val(noteDeadline);  // Set deadline in the input field

	    // Update the modal title and button text
	    $("#createNoteModalLabel").text("Save Note");
	    $("#save-or-create").text("Save");

	    // Store the noteId in the form data for later use when saving
	    $("#noteForm").data("note-id", noteId);

	    // Open the modal
	    $("#createNoteModal").modal("show");
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
	    $('.scrollview').empty(); // Clear the existing notes

	    $.each(data, function(index, note) {
	        let htmlStr = '<div class="note-tile-container">';  // Container for each note tile
	        
	        // Add the button with data-* attributes populated by note data
	        htmlStr += '<button class="note-tile-btn" data-note-id="' + note.id + '" data-note-title="' + note.title + '" data-note-content="' + note.content + '" data-note-tag="' + note.tag + '" data-note-priority="' + note.priority + '" data-note-deadline="' + note.deadline + '">';
	        
	        htmlStr += '<div class="note-tile">';
	        htmlStr += '<p class="note-title">' + note.title + '</p>';
	        htmlStr += '<p class="note-tags">Tag: <span>' + note.tag + '</span></p>';
	        htmlStr += '<p class="note-priority">Priority: <span>' + note.priority + '</span></p>';
	        htmlStr += '<p class="note-deadline">Deadline:<br><span>' + note.deadline + '</span></p>';
	        htmlStr += '</div>';
	        htmlStr += '</button>';

	        // Add the cog icon with unique ID
	        htmlStr += '<i id="cog-btn-' + note.id + '" class="bi bi-gear-fill cog-icon"></i>';
	        
	        htmlStr += '</div>';
	        
	        $('.scrollview').append(htmlStr);  // Append to scrollview
	    });
	};

	
	$(document).on("click", ".bi-gear-fill.cog-icon", function() {
	    // Find the closest .note-tile-btn from the clicked cog icon
	    let $noteTileBtn = $(this).closest('.note-tile-container').find('.note-tile-btn');
	    
	    // Retrieve data from the closest .note-tile-btn
	    let noteId = $noteTileBtn.data("note-id");
	    let noteTitle = $noteTileBtn.data("note-title");
	    let noteTag = $noteTileBtn.data("note-tag");
	    let notePriority = $noteTileBtn.data("note-priority");
	    let noteDeadline = $noteTileBtn.data("note-deadline");

	    // Populate the modal with the existing note data
	    $("#noteTitle").val(noteTitle);  // Set title in the input field
	    $("#noteTag").val(noteTag);  // Set tag in the input field
	    $("#notePriority").val(notePriority);  // Set priority in the select field
	    $("#noteDeadline").val(noteDeadline);  // Set deadline in the input field

	    // Update the modal title and button text
	    $("#createNoteModalLabel").text("Edit Note");
	    $("#save-or-create").text("Save");

	    // Store the noteId in the form data for later use when saving
	    $("#noteForm").data("note-id", noteId);

	    // Open the modal
	    $("#createNoteModal").modal("show");
	});



	
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
