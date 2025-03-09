package com.tus.proj.note_managment;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tus.proj.service.NoteService;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRole;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
	
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createNote(@RequestBody CreateNoteRequest noteRequest) {
    	
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication error.");
//        }
        
//        User loggedInUser = (User) authentication.getPrincipal();  // Safely cast

        // Check if the logged-in user has the 'USER' role
//        if (loggedInUser.getRole() != UserRole.USER) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to create notes.");
//        }
        
        if (noteRequest.getTitle() == null || noteRequest.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
        }
        if (noteRequest.getUser() == null) {
            return ResponseEntity.badRequest().body("User is required");
        }

        Note note = new Note(
            noteRequest.getTitle(),
            noteRequest.getContent(),
            noteRequest.getPriority(),
            noteRequest.getDeadline(),
            noteRequest.getUser(),
            noteRequest.getTag()
        );

        Note savedNote = noteService.saveNote(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }


    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable int id) {
        Optional<Note> note = noteService.getNoteById(id);
        
        if (note.isPresent()) {
            return ResponseEntity.ok(note.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }
    }




    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable int id, @RequestBody CreateNoteRequest noteRequest) {
        Optional<Note> existingNote = noteService.getNoteById(id);
        if (existingNote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        Note updatedNote = new Note(
            noteRequest.getTitle(),
            noteRequest.getContent(),
            noteRequest.getPriority(),
            noteRequest.getDeadline(),
            noteRequest.getUser(),
            noteRequest.getTag()
        );

        Note savedNote = noteService.updateNote(id, updatedNote);
        return ResponseEntity.ok(savedNote);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable int id) {
        Optional<Note> note = noteService.getNoteById(id);
        if (note.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        noteService.deleteNote(id);
        return ResponseEntity.ok("Note deleted successfully");
    }
}
