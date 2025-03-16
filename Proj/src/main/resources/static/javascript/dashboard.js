$(document).ready(function() {


	var rootURL = "http://localhost:9092";


	const username = localStorage.getItem('username');


	if (username) {
		document.getElementById('username').textContent = username;
	} else {
		console.log('No username found in localStorage.');
	}



	$('#sortNotes').on('change', function() {
		let selectedValue = $(this).val();
		localStorage.setItem('sortNotes', selectedValue);
		findAllNotes();
	});



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
		$("#noteAccess").val('PRIVATE');

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
				access: $("#noteAccess").val()
			};

			// Send AJAX request
			$.ajax({
				headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
				type: "POST",
				url: "/api/notes/create",
				contentType: "application/json",
				data: JSON.stringify(noteData),
				success: function(response) {
					showAlert("Note created successfully!", "success");

					// Close the modal
					let modalElement = $("#createNoteModal");
					let modalInstance = bootstrap.Modal.getInstance(modalElement[0]);
					modalInstance.hide();

					// Reset form
					$("#noteForm")[0].reset();
					findAllNotes();
					findAllTags();
				},
				error: function(xhr, status, error) {
					console.error("Error:", error);
					showAlert("Failed to create note.", "warning");
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
				deadline: $("#noteDeadline").val() ? new Date($("#noteDeadline").val()).toISOString() : null,
				access: $("#noteAccess").val()
			};

			// Send AJAX request for updating the note
			$.ajax({
				headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
				type: "PUT",
				url: "/api/notes/" + noteId + "/meta",  // Make sure this matches your PUT endpoint
				contentType: "application/json",
				data: JSON.stringify(updatedNoteData),
				success: function(response) {
					showAlert("Note updated successfully!", "success");

					// Close the modal
					let modalElement = $("#createNoteModal");
					let modalInstance = bootstrap.Modal.getInstance(modalElement[0]);
					modalInstance.hide();

					// Optionally, reset form fields or update the UI with the new note data
					$("#noteForm")[0].reset();
					findAllNotes();  // Optionally refresh the notes list
					findAllTags();
				},
				error: function(xhr, status, error) {
					console.error("Error:", error);
					showAlert("Failed to update note.", "warning");
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

		// Create the request payload with user confirmation
		let deleteRequest = {
			userConfirmation: "confirmed" // Change this to whatever the user confirmation should be
		};

		$.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
			type: "DELETE",
			url: "/api/notes/" + noteId + "/delete",
			contentType: "application/json", // Ensure content type is JSON
			data: JSON.stringify(deleteRequest), // Send the deleteRequest as JSON in the body
			success: function(response) {
				let modalElement = $("#deleteNoteModal");
				let modalInstance = bootstrap.Modal.getInstance(modalElement[0]);
				modalInstance.hide();

				findAllNotes();
				findAllTags();
				showAlert("Note deleted", "success")
			},
			error: function(xhr, status, error) {
				console.error("Error:", error);
				showAlert("Failed to delete note.", "warning");
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
		let noteAccess = $noteTileBtn.data("note-access");

		$("#noteTitle").val(noteTitle);
		$("#noteTag").val(noteTag);
		$("#notePriority").val(notePriority);  // Set priority in the select field
		$("#noteDeadline").val(noteDeadline);  // Set deadline in the input field
		$("#noteAccess").val(noteAccess);

		// Update the modal title and button text
		$("#createNoteModalLabel").text("Save Note");
		$("#save-or-create").text("Save");

		// Store the noteId in the form data for later use when saving
		$("#noteForm").data("note-id", noteId);

		// Open the modal
		$("#createNoteModal").modal("show");
	});

	var findAllTags = function() {
		console.log("Finding all tags");
		

		
		$.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
			type: 'GET',

			url: 'api/notes/getTags/loggedUser',
			dataType: 'json',
			success: function(data) {
				renderTags(data);
			},
			error: function(error) {
				console.log(error);
			}
		})
	}
	
	$("#getPublicNotes").on("click", function(e) {
	    e.preventDefault();
	    
	    let username = $("#publicUsername").val().trim();
	    
	    if (!username) {
	        username = "null";
	    }
	    
	    localStorage.setItem("publicUsername", username);
	    
	    findAllNotes();
	});


	var findAllNotes = function() {
		console.log("Find all notes");

		let sortOption = localStorage.getItem('sortNotes') || 0; // Default to Priority (Low to High)
		
		let username = localStorage.getItem('publicUsername');
		if (!username) {
		    username = "null";
		}
		console.log(username);

		$.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
			type: 'GET',
			url: "api/notes/getAll/loggedUser/" + sortOption + "/" + username,
			dataType: 'json',
			success: function(data) {

				renderNotes(data);



			},
			error: function(xhr, status, error) {
				$(".details").remove();
				console.log("failed")
			}


		})
	};

	var renderTags = function(data) {
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

	$('.sidebar-scrollview').on('change', "input[type='checkbox']", function() {
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
			url: `http://localhost:9092/api/notes/getTags/loggedUser/${checkedValues.join(',')}`,  // Pass tags in the URL
			method: 'GET',
			//	        headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
			success: function(response) {
				console.log("Response received:", response);

				// Extract notes from HATEOAS response
				const notes = response._embedded?.noteList || [];

				if (!notes.length) {
					showAlert("No notes found for selected tags.", "warning");
					$('.scrollview').empty(); // Clear existing notes
					return;
				}

				// Pass data to renderNotes function
				renderNotes(notes);
			},
			error: function(xhr) {
				console.error("Error fetching notes:", xhr.responseText);
			}
		});
	});

	var renderNotes = function(data) {
		console.log("populating notes");
		$('.scrollview').empty();

		$.each(data, function(index, note) {
			let htmlStr = '<div class="note-tile-container">';

			htmlStr += '<button class="note-tile-btn" data-note-id="' + note.id + '" data-note-access="' + note.access + '" data-note-title="' + note.title + '" data-note-content="' + note.content + '" data-note-tag="' + note.tag + '" data-note-priority="' + note.priority + '" data-note-deadline="' + note.deadline + '">';

			htmlStr += '<div class="note-tile">';
			htmlStr += '<p class="note-title">' + note.title + '</p>';
			htmlStr += '<p class="note-tags">Tag: <span>' + note.tag + '</span></p>';
			
			htmlStr += '<p class="note-priority">Priority: <span>' + note.priority + '</span></p>';
			htmlStr += '<p class="note-priority">Access: <span>' + note.access + '</span></p>';

			htmlStr += '<p class="note-deadline">Deadline:<br><span>' + note.deadline + '</span></p>';
			
			htmlStr += '</div>';
			htmlStr += '</button>';

			if ( note.access !== "PUBLIC" || username == note.username ) {
			    htmlStr += '<i id="cog-btn-' + note.id + '" class="bi bi-gear-fill cog-icon"></i>';
			    htmlStr += '<i id="trash-btn-' + note.id + '" class="bi bi-trash-fill trash-icon"></i>';
			}

			htmlStr += '</div>';

			$('.scrollview').append(htmlStr);
			console.log(note.username);
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

		// Create an object to match the UpdateNoteContentRequestDTO structure
		let updateData = {
			content: updatedContent
		};

		$.ajax({
			headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
			url: "/api/notes/" + noteId + "/content",
			type: "PUT",
			contentType: "application/json",
			data: JSON.stringify(updateData),
			success: function(response) {
				showAlert("Note updated successfully!", "success");
				$("#editNoteModal").modal("hide"); // Close modal
				findAllNotes();
			},
			error: function(xhr) {
				showAlert("Failed to update note: " + xhr.responseText, "warning");
			}
		});
	});

	const titleInput = document.getElementById("noteTitle");
	const tagInput = document.getElementById("noteTag");
	const form = document.getElementById("noteForm");

	function enforceCharacterLimit(input, limit) {
		input.addEventListener("input", function() {
			if (this.value.length > limit) {
				this.value = this.value.slice(0, limit); // Truncate extra characters
				showAlert(`Maximum ${limit} characters allowed!`, "warning");
			}
		});
	}

	enforceCharacterLimit(titleInput, 10);
	enforceCharacterLimit(tagInput, 10);





	$("#closeModalBtn").on("click", function() {
		$("#noteModal").fadeOut();
	});

	findAllNotes();
	findAllTags();







});
