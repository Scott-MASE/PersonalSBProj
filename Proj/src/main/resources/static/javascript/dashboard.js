$(document).ready(() => {
    const rootURL = "http://localhost:9092";
    const username = localStorage.getItem("username");
    const role = localStorage.getItem("role");
	
	const createNoteButton = document.getElementById('create-note');
	if (role === 'Moderator') {
	    createNoteButton.style.display = 'none';
}
    if (username) {
        $("#username").text(username);
    } else {
        console.log("No username found in localStorage.");
    }
	
	// resets the create/edit note modal values
    function resetForm() {
        $("#noteForm")[0].reset();
        $("#noteTitle, #noteTag").val("");
        $("#notePriority").val("LOW");
        $("#noteDeadline").val("");
        $("#noteAccess").val("PRIVATE");
    }

    function closeModal(modalId) {
        let modalElement = $(modalId);
        let modalInstance = bootstrap.Modal.getInstance(modalElement[0]);
        modalInstance.hide();
    }
	
	// function for performing ajax request, reduces repetition
    function handleNoteRequest(url, method, data, successMessage) {
        $.ajax({
            headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
            type: method,
            url,
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function () {
                showAlert(successMessage, "success");
                closeModal("#createNoteModal");
                resetForm();
                fetchNotes();
                fetchTags();
            },
            error: function (xhr, status, error) {
                console.error("Error:", error);
                showAlert("Failed to process note.", "warning");
            }
        });
    }
	
	// keeps track of note sort dropdowns current value
    $("#sortNotes").on("change", function () {
        localStorage.setItem("sortNotes", $(this).val());
        fetchNotes();
    });

    $("#logout-button").on("click", logout);

	// checks all filter boxes
    $("#clear-filter-button").on("click", function () {
        console.log("Resetting Filter");
        $(".sidebar-scrollview input[type='checkbox']").prop("checked", true).trigger("change");
    });

    $("#create-note").on("click", function (e) {
        e.preventDefault();
        console.log("Opening modal...");
        resetForm();
        $("#createNoteModalLabel").text("Create Note");
        $("#save-or-create").text("Create");
        $("#createNoteModal").modal("show");
    });

    $("#noteForm").on("submit", function (event) {
        event.preventDefault();
        let noteId = $("#noteForm").data("note-id");
        let noteData = {
            title: $("#noteTitle").val().trim(),
            content: "", 
            tag: $("#noteTag").val().trim(),
            priority: $("#notePriority").val(),
            deadline: $("#noteDeadline").val() ? new Date($("#noteDeadline").val()).toISOString() : null,
            access: $("#noteAccess").val()
        };

        if ($("#createNoteModalLabel").text() === "Create Note") {
            console.log("Creating note...");
            handleNoteRequest("/api/notes/create", "POST", noteData, "Note created successfully!");
        } else {
            console.log("Editing note...");
			// this is used a few times, it chooses wether mod endpoint or user endpoint is used. 
			// they generally do the same thing, but mod enpoints can access other users public notes
            let url = role === "Moderator" ? `/api/notes/${noteId}/mod/meta` : `/api/notes/${noteId}/meta`;
            handleNoteRequest(url, "PUT", noteData, "Note updated successfully!");
        }
    });

    $(document).on("click", ".bi-trash-fill.trash-icon", function () {
        let $noteTileBtn = $(this).closest(".note-tile-container").find(".note-tile-btn");
        let noteId = $noteTileBtn.data("note-id");
        let noteTitle = $noteTileBtn.data("note-title");
        $("#deleteNoteModalLabel").text(`Delete ${noteTitle}?`);
        $("#deleteNoteModal").data("note-id", noteId).modal("show");
    });

	// delete note confirmation
    $("#confirmDelete").on("click", function () {
        let noteId = $("#deleteNoteModal").data("note-id");
        let deleteRequest = { userConfirmation: "confirmed" };
        let url = role === "Moderator" ? `/api/notes/${noteId}/delete/mod` : `/api/notes/${noteId}/delete`;

        $.ajax({
            headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
            type: "DELETE",
            url,
            contentType: "application/json",
            data: JSON.stringify(deleteRequest),
            success: function () {
                closeModal("#deleteNoteModal");
                fetchNotes();
                fetchTags();
                showAlert("Note deleted", "success");
            },
            error: function (xhr, status, error) {
                console.error("Error:", error);
                showAlert("Failed to delete note.", "warning");
            }
        });
    });


	$(document).on("click", ".bi-gear-fill.cog-icon", function() {
	    let $noteTileBtn = $(this).closest('.note-tile-container').find('.note-tile-btn');
	    openNoteModal($noteTileBtn);
	});
	
	// populates the edit/create note modal with the selected notes details
	function openNoteModal($noteTileBtn) {
	    $("#noteTitle").val($noteTileBtn.data("note-title"));
	    $("#noteTag").val($noteTileBtn.data("note-tag"));
	    $("#notePriority").val($noteTileBtn.data("note-priority"));
	    $("#noteDeadline").val($noteTileBtn.data("note-deadline"));
	    $("#createNoteModalLabel").text("Edit Note");
	    $("#save-or-create").text("Save");
	    $("#noteForm").data("note-id", $noteTileBtn.data("note-id"));
	    $("#createNoteModal").modal("show");
	}

	function fetchTags() {
	    console.log("Finding all tags");

	    let url = role === "User" ? 'api/notes/getTags/loggedUser' : 'api/notes/getTags/publicTags';

	    $.ajax({
	        headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        type: 'GET',
	        url: url,
	        dataType: 'json',
	        success: renderTags,
	        error: console.error
	    });
	}

	// when empty or invalid, returns the users notes, when a valid username is entered, returns their public notes
	$("#getPublicNotes").on("click", function(e) {
	    e.preventDefault();
	    let username = $("#publicUsername").val().trim() || "null";
	    localStorage.setItem("publicUsername", username);
	    fetchNotes();
	});

	// retrieves notes, the users by default with variable order, or another users public notes if a valid name is provided
	function fetchNotes() {
	    console.log("Fetching notes");
	    let sortOption = localStorage.getItem('sortNotes') || 0;
	    let username = localStorage.getItem('publicUsername') || "null";
	    let url = role !== "Moderator" ? 
	        `api/notes/getAll/loggedUser/${sortOption}/${username}` : 
	        `api/notes/getPublic/mod/${sortOption}/${username}`;

	    $.ajax({
	        headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        type: 'GET',
	        url: url,
	        dataType: 'json',
	        success: renderNotes,
	        error: function(xhr, status, error) {
	            $(".details").remove();
	            console.log("Failed to fetch notes:", error);
	        }
	    });
	}
	
	//add tags to the filter scrollview
	function renderTags(data) {
	    const container = $('.sidebar-scrollview').empty().append('<h2>Filter</h2>');
	    data.forEach(tag => {
	        const htmlStr = `<label class="custom-checkbox">
	            <input type="checkbox" checked>
	            <span class="checkmark"></span> ${tag}
	        </label>`;
	        container.append(htmlStr);
	    });
	}

	//keeps track of which tags are ticked in filter, refreshes notes if a tickbox is changed
	$('.sidebar-scrollview').on('change', "input[type='checkbox']", function() {
	    const checkedValues = $('.sidebar-scrollview input[type="checkbox"]:checked')
	        .map((_, el) => $(el).parent().text().trim())
	        .get();

	    console.log("Checked Tags:", checkedValues);

	    if (!checkedValues.length) {
	        console.log("No tags selected.");
	        $('.scrollview').empty();
	        return;
	    }
		
		// gets rid of public notes if tag is changed, mod can only see pub notes
		if (role !== "Moderator"){
		$("#publicUsername").text("");
		localStorage.setItem("publicUsername", "");
		}

	    const url = role === "User" ? 
	        `/api/notes/getTags/loggedUser/${checkedValues.join(',')}` :
	        `/api/notes/getTags/public/${checkedValues.join(',')}`;

	    $.ajax({
	        headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        url: url,
	        method: 'GET',
	        success: function(response) {
	            const notes = response._embedded?.noteList || [];
	            if (!notes.length) {
	                showAlert("No notes found for selected tags.", "warning");
	                $('.scrollview').empty();
	                return;
	            }
	            renderNotes(notes);
	        },
	        error: function(xhr) {
	            console.error("Error fetching notes:", xhr.responseText);
	        }
	    });
	});

	// this creates the hex notes and populates the main content area with them
	function renderNotes(data) {
	    console.log("Populating notes");
	    const container = $('.scrollview').empty();

	    data.forEach(note => {
	        const htmlStr = `<div class="note-tile-container">
	            <button class="note-tile-btn" 
	                data-note-id="${note.id}" 
	                data-note-access="${note.access}" 
	                data-note-title="${note.title}" 
	                data-note-content="${note.content}" 
	                data-note-tag="${note.tag}" 
	                data-note-priority="${note.priority}" 
	                data-note-deadline="${note.deadline}">
	                <div class="note-tile">
	                    <p class="note-title">${note.title}</p>
	                    <p class="note-tags">Tag: <span>${note.tag}</span></p>
	                    <p class="note-priority">Priority: <span>${note.priority}</span></p>
	                    <p class="note-priority">Access: <span>${note.access}</span></p>
	                    <p class="note-deadline">Deadline:<br><span>${note.deadline}</span></p>
	                </div>
	            </button>
	            ${username === note.username || role === "Moderator" ? `
	                <i id="cog-btn-${note.id}" class="bi bi-gear-fill cog-icon"></i>
	                <i id="trash-btn-${note.id}" class="bi bi-trash-fill trash-icon"></i>` : ''
	            }
	        </div>`;
	        container.append(htmlStr);
	    });
	}


	$(document).on("click", ".note-tile-btn", function() {
	    $("#editNoteTitle").text($(this).data("note-title"));
	    $("#editNoteContent").val($(this).data("note-content"));
	    $("#editNoteForm").data("note-id", $(this).data("note-id"));
	    $("#editNoteModal").modal("show");
	});

	$("#editNoteForm").on("submit", function(event) {
	    event.preventDefault();
	    const noteId = $(this).data("note-id");
	    const updatedContent = $("#editNoteContent").val();
	    updateNoteContent(noteId, updatedContent);
	});

	function updateNoteContent(noteId, content) {
	    const url = role === "User" ? 
	        `/api/notes/${noteId}/content` : 
	        `/api/notes/${noteId}/mod/content`;

	    $.ajax({
	        headers: { Authorization: `Bearer ${TokenStorage.getToken()}` },
	        url: url,
	        type: "PUT",
	        contentType: "application/json",
	        data: JSON.stringify({ content }),
	        success: function() {
	            showAlert("Note updated successfully!", "success");
	            $("#editNoteModal").modal("hide");
	            fetchNotes();
	        },
	        error: function(xhr) {
	            showAlert("Failed to update note: " + xhr.responseText, "warning");
	        }
	    });
	}

	// limits title and tag length to ensure they dont spill off the hexagon
	function enforceCharacterLimit(input, limit) {
	    input.addEventListener("input", function() {
	        if (this.value.length > limit) {
	            this.value = this.value.slice(0, limit);
	            showAlert(`Maximum ${limit} characters allowed!`, "warning");
	        }
	    });
	}

	enforceCharacterLimit(document.getElementById("noteTitle"), 10);
	enforceCharacterLimit(document.getElementById("noteTag"), 10);

	$("#closeModalBtn").on("click", function() {
	    $("#noteModal").fadeOut();
	});

	fetchNotes();
	fetchTags();
});
