$(document).ready(function() {
	
	
	var rootURL = "http://localhost:9092";
	

	const username = localStorage.getItem('username');


	if (username) {
	    document.getElementById('username').textContent = username;
	} else {
	    console.log('No username found in localStorage.');
	}
	
	function getUserIdByUsername(username) {
	    $.ajax({
	        url: '/api/users/username/' + username,  // Make sure the URL matches your backend route
	        type: 'GET',
	        success: function(response) {
	            // If user is found, store the user ID in localStorage
	            if (response) {
	                localStorage.setItem('userId', response);
	                console.log('User ID stored in localStorage:', response);
					findAllNotes(response);
					findAllTags(response);
	            }
	        },
	        error: function(xhr, status, error) {
	            // If user is not found or any other error occurs
	            if (xhr.status === 404) {
	                console.log('User not found');
	            } else {
	                console.log('Error: ' + error);
	            }
	        }
	    });
	}
	


		
	$('#logout-button').off('click').on('click', function() {
		logout();
		});
		
	$('#clear-filter-button').off('click').on('click', function() {
			console.log("Reseting Filter");
			$('.sidebar-scrollview input[type="checkbox"]').prop('checked', true);

			// Optionally, trigger the change event for the checkboxes to update the filter
			$('.sidebar-scrollview input[type="checkbox"]').trigger('change');

			});
	



	
	$('#create-note').off('click').on('click', function(e) {
	    e.preventDefault(); 
	    console.log("Opening modal...");

	    $("#noteForm")[0].reset();


	    $("#noteTitle").val('');
	    $("#noteTag").val('');
	    $("#notePriority").val('LOW'); 
	    $("#noteDeadline").val('');


	    $("#createNoteModalLabel").text("Create Note");
	    $("#save-or-create").text("Create");

	    $('#createNoteModal').modal('show'); 
	});
	

	
	$('#noteForm').off('submit').on('submit', function(event) {
	    event.preventDefault(); 
		let noteId = $("#noteForm").data("note-id");
		if ($("#createNoteModalLabel").text() === "Create Note") {
			console.log("creating");
			let noteData = {
			    title: $("#noteTitle").val().trim(),
			    content: "",
			    tag: $("#noteTag").val().trim(),
			    priority: $("#notePriority").val(),
			    deadline: $("#noteDeadline").val() ? new Date($("#noteDeadline").val()).toISOString() : null,
			    user: localStorage.getItem('userId')
			};

			// Send AJAX request
			$.ajax({
				headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
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
					findAllNotes(localStorage.getItem('userId'));
					findAllTags();
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
					headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
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
			            findAllNotes(localStorage.getItem('userId'));  // Optionally refresh the notes list
						findAllTags();
			        },
			        error: function(xhr, status, error) {
			            console.error("Error:", error);
			            alert("Failed to update note.");
			        }
			    });
			

		}
		

	});
	
	$(document).on("click", ".bi-trash-fill.trash-icon", function() {
	    let $noteTileBtn = $(this).closest('.note-tile-container').find('.note-tile-btn');

	    let noteId = $noteTileBtn.data("note-id");
	    let noteTitle = $noteTileBtn.data("note-title");
	    $("#deleteNoteModalLabel").text("Delete " + noteTitle + "?");
	    $("#deleteNoteModal").data("note-id", noteId);
	    $("#deleteNoteModal").modal("show");
	});

	$("#confirmDelete").on("click", function() {

	    let noteId = $("#deleteNoteModal").data("note-id");


	    $.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        type: "DELETE",
	        url: "/api/notes/" + noteId +"/delete",  
	        success: function(response) {
	            
	            let modalElement = $("#deleteNoteModal");
	            let modalInstance = bootstrap.Modal.getInstance(modalElement[0]);
	            modalInstance.hide();


	            findAllNotes(localStorage.getItem('userId'));  
				findAllTags();
	        },
	        error: function(xhr, status, error) {
	            console.error("Error:", error);
	            alert("Failed to delete note.");
	        }
	    });
	});

	
	$(document).on("click", ".bi-gear-fill.cog-icon", function() {
	    let $noteTileBtn = $(this).closest('.note-tile-container').find('.note-tile-btn');
	    
	    let noteId = $noteTileBtn.data("note-id");
	    let noteTitle = $noteTileBtn.data("note-title");
	    let noteTag = $noteTileBtn.data("note-tag");
	    let notePriority = $noteTileBtn.data("note-priority");
	    let noteDeadline = $noteTileBtn.data("note-deadline");

	    $("#noteTitle").val(noteTitle);  
	    $("#noteTag").val(noteTag);  
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
	
	var findAllTags = function(){
		console.log("Finding all tags");
		$.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
			type: 'GET',
		
			url: 'api/notes/'+localStorage.getItem('userId')+'/getTags',
			dataType: 'json',
			success: function(data){
				renderTags(data);
			},
			error: function(error){
				console.log(error);
			}
		})
	}

	
	var findAllNotes = function(userId) {
		console.log("userId: " + userId);
		console.log("Find all notes");
		$.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
			type: 'GET',
			url: "api/notes/" +userId+"/getAll",
			dataType: 'json',
			success: function(data) {
			    
			    renderNotes(data);
				console.log("HHEEERREE" +localStorage.getItem('userId'));
				
				
			  
			},
			error: function(xhr, status, error) {
				$(".details").remove();
				console.log("failed")
			}


		})
	};
	
	var renderTags = function(data){
		$('.sidebar-scrollview').empty(); 
		console.log("tags");
		$('.sidebar-scrollview').append('<h2>Filter</h2>');
		$.each(data, function(index, note) {
			console.log(note)
		let htmlStr = '<label class="custom-checkbox">';
		htmlStr += '<input type="checkbox" checked>';
		htmlStr += '<span class="checkmark"></span>'
		htmlStr += note
		htmlStr += '</label>'
		
		$('.sidebar-scrollview').append(htmlStr);
		
		});
		
	};
	
	$('.sidebar-scrollview').on('change', "input[type='checkbox']", function () {
	    // Get all checked checkboxes
	    const checkedValues = $('.sidebar-scrollview input[type="checkbox"]:checked')
	        .map(function() {
	            return $(this).parent().text().trim();  // Extract tag text
	        })
	        .get();

	    console.log("Checked Tags:", checkedValues);

	    // Ensure at least one tag is selected before making a request
	    if (checkedValues.length === 0) {
	        console.log("No tags selected.");
	        $('.scrollview').empty(); // Clear notes if nothing is selected
	        return;
	    }

	    // Make an AJAX request to fetch notes for the selected tags
	    $.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        url: `http://localhost:9092/api/notes/getTags/${checkedValues.join(',')}/${localStorage.getItem('userId')}`,  // Pass tags in the URL
	        method: 'GET',
//	        headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        success: function (notes) {
	            console.log("Notes received:", notes);

	            if (!notes || notes.length === 0) {
	                showAlert("No notes found for selected tags.", "warning");
	                $('.scrollview').empty(); // Clear existing notes
	                return;
	            }

	            // Pass data to renderNotes function
	            renderNotes(notes);
	        },
	        error: function (xhr) {
	            console.error("Error fetching notes:", xhr.responseText);
	        }
	    });
	});
	
	var renderNotes = function(data) {
	    console.log("populating notes");
	    $('.scrollview').empty(); 

	    $.each(data, function(index, note) {
	        let htmlStr = '<div class="note-tile-container">'; 
	        
	        htmlStr += '<button class="note-tile-btn" data-note-id="' + note.id + '" data-note-title="' + note.title + '" data-note-content="' + note.content + '" data-note-tag="' + note.tag + '" data-note-priority="' + note.priority + '" data-note-deadline="' + note.deadline + '">';
	        
	        htmlStr += '<div class="note-tile">';
	        htmlStr += '<p class="note-title">' + note.title + '</p>';
	        htmlStr += '<p class="note-tags">Tag: <span>' + note.tag + '</span></p>';
	        htmlStr += '<p class="note-priority">Priority: <span>' + note.priority + '</span></p>';
	        htmlStr += '<p class="note-deadline">Deadline:<br><span>' + note.deadline + '</span></p>';
	        htmlStr += '</div>';
	        htmlStr += '</button>';

	        htmlStr += '<i id="cog-btn-' + note.id + '" class="bi bi-gear-fill cog-icon"></i>';
			htmlStr += '<i id="trash-btn-' + note.id + '" class="bi bi-trash-fill trash-icon"></i>';
	        
	        htmlStr += '</div>';
	        
	        $('.scrollview').append(htmlStr);  
	    });
		
	};

	
	$(document).on("click", ".bi-gear-fill.cog-icon", function() {

	    let $noteTileBtn = $(this).closest('.note-tile-container').find('.note-tile-btn');
	    

	    let noteId = $noteTileBtn.data("note-id");
	    let noteTitle = $noteTileBtn.data("note-title");
	    let noteTag = $noteTileBtn.data("note-tag");
	    let notePriority = $noteTileBtn.data("note-priority");
	    let noteDeadline = $noteTileBtn.data("note-deadline");


	    $("#noteTitle").val(noteTitle);  
	    $("#noteTag").val(noteTag);  
	    $("#notePriority").val(notePriority);  
	    $("#noteDeadline").val(noteDeadline);  


	    $("#createNoteModalLabel").text("Edit Note");
	    $("#save-or-create").text("Save");

	    $("#noteForm").data("note-id", noteId);


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
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        url: "/api/notes/" + noteId + "/content",
	        type: "PUT",
	        contentType: "application/json",
	        data: JSON.stringify(updatedContent), // Send raw string, not an object
	        success: function(response) {
	            alert("Note updated successfully!");
	            $("#editNoteModal").modal("hide"); // Close modal
	            findAllNotes(localStorage.getItem('userId'));
	        },
	        error: function(xhr) {
	            alert("Failed to update note: " + xhr.responseText);
	        }
	    });
	});


	
	$("#closeModalBtn").on("click", function() {
	    $("#noteModal").fadeOut();
	});
	
	getUserIdByUsername(username);
	
	
	
	
	

	
});
