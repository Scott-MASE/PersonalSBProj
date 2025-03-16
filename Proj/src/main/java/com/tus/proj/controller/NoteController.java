package com.tus.proj.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tus.proj.note_managment.Access;
import com.tus.proj.note_managment.Note;
import com.tus.proj.note_managment.dto.CreateNoteRequestDTO;
import com.tus.proj.note_managment.dto.DeleteNoteRequestDTO;
import com.tus.proj.note_managment.dto.UpdateNoteContentRequestDTO;
import com.tus.proj.note_managment.dto.UpdateNoteMetaRequestDTO;
import com.tus.proj.service.NoteService;
import com.tus.proj.service.UserService;
import com.tus.proj.user_managment.User;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

	private final NoteService noteService;
	private final UserService userService;

	public NoteController(NoteService noteService, UserService userService) {
		this.noteService = noteService;
		this.userService = userService;
	}

	private static EntityModel<Note> generateHATEOASLinks(Note savedNote) {
		EntityModel<Note> noteModel = EntityModel.of(savedNote);
		Link selfLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(savedNote.getId())).withSelfRel();
		Link updateMetaLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(savedNote.getId(), null))
				.withRel("updateMeta");
		Link updateContentLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteContent(savedNote.getId(), null))
				.withRel("updateContent");
		Link deleteLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(savedNote.getId(), null))
				.withRel("delete");
		noteModel.add(selfLink, updateMetaLink, updateContentLink, deleteLink);
		return noteModel;
	}

	@PreAuthorize("hasRole('User')")
	@PostMapping("/create")
	public ResponseEntity<?> createNote(@RequestBody CreateNoteRequestDTO noteRequest) {
		if (noteRequest.getTitle() == null || noteRequest.getTitle().isEmpty()) {
			return ResponseEntity.badRequest().body("Title is required");
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> opUser = userService.findByUsername(username);
		if (!opUser.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or you can return a message in the body
		}

		User user = opUser.get();

		Note note = new Note(noteRequest.getTitle(), noteRequest.getContent(), noteRequest.getPriority(),
				noteRequest.getDeadline(), user, noteRequest.getTag(), noteRequest.getAccess());

		Note savedNote = noteService.saveNote(note);

		EntityModel<Note> noteModel = generateHATEOASLinks(savedNote);

		return ResponseEntity.status(HttpStatus.CREATED).body(noteModel);
	}

	// Unused, added just in case its ever needed.
	@PreAuthorize("hasRole('Admin')")
	@GetMapping("/getAll")
	public ResponseEntity<CollectionModel<EntityModel<Note>>> getAllNotes() {
		List<Note> notes = noteService.getAllNotes();
		Collections.reverse(notes);

		// Wrapping notes in HATEOAS
		CollectionModel<EntityModel<Note>> notesModel = CollectionModel.wrap(notes);

		for (Note note : notes) {
			Link selfLink = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel();
			Link updateLink = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(note.getId(), null))
					.withRel("update");
			Link deleteLink = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(note.getId(), null))
					.withRel("delete");
			notesModel.add(selfLink, updateLink, deleteLink);
		}

		return ResponseEntity.ok(notesModel);
	}

	@PreAuthorize("hasRole('User')")
	@GetMapping("/getAll/loggedUser/{order}/{pUsername}")
	public ResponseEntity<List<Note>> getAllNotesById(@PathVariable int order, @PathVariable String pUsername) {
		List<Note> notes;
		
		if(pUsername.equals("null")) {
			
		    String username = SecurityContextHolder.getContext().getAuthentication().getName();

		    // Get the user from the username
		    Optional<User> opUser = userService.findByUsername(username);

		    if (!opUser.isPresent()) {
		        // If user is not found, return 404 Not Found
		        return ResponseEntity.status(HttpStatus.NOT_FOUND)
		                             .body(null); // Or a message like "User not found"
		    }

		    User user = opUser.get();

		    // Get the notes for the user
		    notes = noteService.getAllNotesByUserId(user.getId());
			
		} else {
			
		    Optional<User> opUser = userService.findByUsername(pUsername);

		    if (!opUser.isPresent()) {
			    String username = SecurityContextHolder.getContext().getAuthentication().getName();

			    // Get the user from the username
			    opUser = userService.findByUsername(username);

			    if (!opUser.isPresent()) {
			        // If user is not found, return 404 Not Found
			        return ResponseEntity.status(HttpStatus.NOT_FOUND)
			                             .body(null); // Or a message like "User not found"
			    }

			    User user = opUser.get();

			    // Get the notes for the user
			    notes = noteService.getAllNotesByUserId(user.getId());
		    }
			notes = noteService.getPublicNotesByUsername(pUsername);
			
		}
		


	    // If no notes are found, return 204 No Content
	    if (notes.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NO_CONTENT)
	                             .body(notes); // An empty body is acceptable, or an empty array/list
	    }

	    // Reverse the order of notes if needed
	    switch(order) {
	    	case 0: 
	    		Collections.sort(notes, Comparator.comparing(Note::getId));
	    		Collections.reverse(notes);
	    		break;
	    	case 1: 
	            Collections.sort(notes, Comparator.comparing(note -> note.getPriority()));
	            break;
	    	case 2:
	            Collections.sort(notes, Comparator.comparing(note -> note.getPriority()));
	            Collections.reverse(notes);
	            break;
	        case 3:
	        	Collections.sort(notes, Comparator.comparing(note -> note.getTitle().toLowerCase()));
	            break;
	        case 4: 
	            Collections.sort(notes, Comparator.comparing(Note::getDeadline));
	            break;
	        default:
	        	break;
	    }


	    return ResponseEntity.ok(notes);
	}
	
	


	@PreAuthorize("hasRole('User')")
	@GetMapping("/{id}")
	public ResponseEntity<?> getNoteById(@PathVariable int id) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> opUser = userService.findByUsername(username);
		if (!opUser.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or you can return a message in the body
		}

		User user = opUser.get();

		Optional<Note> oPnote = noteService.getNoteById(id);

		if (oPnote.isPresent()) {
			Note note = oPnote.get();

			if (note.getUser().getId() != user.getId()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("You are not authorized to access or modify this note.");
			}

			EntityModel<Note> noteModel = generateHATEOASLinks(note);

			return ResponseEntity.ok(noteModel);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
		}
	}

	@PreAuthorize("hasRole('User')")
	@PutMapping("/{id}/meta")
	public ResponseEntity<?> updateNoteMeta(@PathVariable int id, @RequestBody UpdateNoteMetaRequestDTO noteRequest) {
		Optional<Note> existingNote = noteService.getNoteById(id);
		if (existingNote.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
		}
		
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		
		Optional<User> opUser = userService.findByUsername(username);
		if (!opUser.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or you can return a message in the body
		}

		User user = opUser.get();
		Note note = existingNote.get();
		
		if ((note.getUser().getId() != user.getId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("You are not authorized to access or modify this note.");
		}

		
		note.setDeadline(noteRequest.getDeadline());
		note.setPriority(noteRequest.getPriority());
		note.setTag(noteRequest.getTag());
		note.setTitle(noteRequest.getTitle());
		note.setContent(noteRequest.getContent());
		note.setAccess(noteRequest.getAccess());

		Note savedNote = noteService.updateNote(id, note);

		// Adding HATEOAS links
		EntityModel<Note> noteModel = generateHATEOASLinks(savedNote);

		return ResponseEntity.ok(noteModel);
	}

	@PreAuthorize("hasRole('User')")
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteNote(@PathVariable int id, @RequestBody DeleteNoteRequestDTO deleteRequest) {
		// Check if user has confirmed deletion
		if (!"confirmed".equalsIgnoreCase(deleteRequest.getUserConfirmation())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deletion not confirmed");
		}
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> opUser = userService.findByUsername(username);
		if (!opUser.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or you can return a message in the body
		}

		User user = opUser.get();
		

		Optional<Note> oPnote = noteService.getNoteById(id);
		if (oPnote.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
		}
		
		Note note = oPnote.get();
		
		if (note.getUser().getId() != user.getId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("You are not authorized to access or modify this note.");
		}

		noteService.deleteNote(id);
		return ResponseEntity.ok("Note deleted successfully");
	}

	@PreAuthorize("hasRole('User')")
	@PutMapping("/{id}/content")
	public ResponseEntity<?> updateNoteContent(@PathVariable int id,
			@RequestBody UpdateNoteContentRequestDTO updateRequest) {
		Optional<Note> existingNote = noteService.getNoteById(id);
		if (existingNote.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
		}

		Note note = existingNote.get();
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> opUser = userService.findByUsername(username);
		if (!opUser.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or you can return a message in the body
		}

		User user = opUser.get();
		
		if (note.getUser().getId() != user.getId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("You are not authorized to access or modify this note.");
		}

		String newContent = updateRequest.getContent();

		if (newContent.startsWith("\"") && newContent.endsWith("\"")) {
			newContent = newContent.substring(1, newContent.length() - 1);
		}

		note.setContent(newContent);
		Note savedNote = noteService.updateNote(id, note);

		// Adding HATEOAS links
		EntityModel<Note> noteModel = generateHATEOASLinks(savedNote);

		return ResponseEntity.ok(noteModel);
	}

	@PreAuthorize("hasRole('User')")
	@GetMapping("/getTags/loggedUser")
	public ResponseEntity<?> getAllTags() {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> opUser = userService.findByUsername(username);
		if (!opUser.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or you can return a message in the body
		}

		User user = opUser.get();
		
		List<String> tags = noteService.getAllUniqueTagsByUserId(user.getId());

		// Check if notes are found
		if (tags.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tags found");
		}

		// Return tags with HATEOAS links
		return ResponseEntity.status(HttpStatus.OK).body(tags);
	}

	@PreAuthorize("hasRole('User')")
	@GetMapping("/getTags/loggedUser/{tags}")
	public ResponseEntity<?> getNotesByTags(@PathVariable List<String> tags) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> opUser = userService.findByUsername(username);
		if (!opUser.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or you can return a message in the body
		}

		User user = opUser.get();
		// Call the service method to get the notes by tags
		List<Note> notes = noteService.getNotesByTagListAndUserId(tags, user.getId());

		// Check if notes are found
		if (notes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No notes found for the given tags.");
		}

		Collections.reverse(notes);

		CollectionModel<EntityModel<Note>> notesModel = CollectionModel.wrap(notes);
		for (Note note : notes) {
			Link selfLink = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel();
			Link updateLink = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(note.getId(), null))
					.withRel("update");
			Link deleteLink = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(note.getId(), null))
					.withRel("delete");
			notesModel.add(selfLink, updateLink, deleteLink);
		}

		return ResponseEntity.status(HttpStatus.OK).body(notesModel);
	}
	
	@PreAuthorize("hasRole('User')")
	@GetMapping("/getPublic/{username}")
	public ResponseEntity<List<Note>> getPublicNotesByUsername(@PathVariable String username) {
	    // Find the user by the provided username
	    Optional<User> opUser = userService.findByUsername(username);
	    if (!opUser.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	    User user = opUser.get();

	    // Retrieve the public notes for this user.
	    // Ensure that noteService.getPublicNotesByUserId() returns only those notes
	    // where note.getAccess() equals Access.PUBLIC.
	    List<Note> publicNotes = noteService.getPublicNotesByUserId(user.getId());

	    if (publicNotes.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(publicNotes);
	    }

	    return ResponseEntity.ok(publicNotes);
	}

}
