package com.tus.proj.note_managment;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tus.proj.service.NoteService;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRole;

import org.springframework.http.HttpStatus;

import java.util.Collections;
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
    	
        
        if (noteRequest.getTitle() == null || noteRequest.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
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


    @GetMapping("/getAll")
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        Collections.reverse(notes);

        return ResponseEntity.ok(notes);
    }
    
    @GetMapping("/{id}/getAll")
    public ResponseEntity<List<Note>> getAllNotesById(@PathVariable int id) {
        List<Note> notes = noteService.getAllNotesByUserId(id);
        Collections.reverse(notes);

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




    @PutMapping("/{id}/meta")
    public ResponseEntity<?> updateNoteMeta(@PathVariable int id, @RequestBody CreateNoteRequest noteRequest) {
        Optional<Note> existingNote = noteService.getNoteById(id);
        if (existingNote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }
        System.out.println("note found");
        
        Note note = existingNote.get();
        
        note.setDeadline(noteRequest.getDeadline());
        note.setPriority(noteRequest.getPriority());
        note.setTag(noteRequest.getTag());
        note.setTitle(noteRequest.getTitle());
        
       

        Note savedNote = noteService.updateNote(id, note);
        return ResponseEntity.ok(savedNote);
    }


    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteNote(@PathVariable int id) {
        Optional<Note> note = noteService.getNoteById(id);
        if (note.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        noteService.deleteNote(id);
        return ResponseEntity.ok("Note deleted successfully");
    }
    
    @PutMapping("/{id}/content")
    public ResponseEntity<?> updateNoteContent(@PathVariable int id, @RequestBody String newContent) {
        Optional<Note> existingNote = noteService.getNoteById(id);
        if (existingNote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        Note note = existingNote.get();

        // Remove extra quotes if they exist (to fix old data)
        if (newContent.startsWith("\"") && newContent.endsWith("\"")) {
            newContent = newContent.substring(1, newContent.length() - 1);
        }

        note.setContent(newContent);
        Note savedNote = noteService.updateNote(id, note);
        return ResponseEntity.ok(savedNote);
    }
    
    @GetMapping("/getTags")
    public ResponseEntity<?> getAllTags() {
    	
    	List<String> tags = noteService.getAllUniqueTagsByUserId(1);
        
        // Check if notes are found
        if (tags.isEmpty()) {
            // Return a 404 (Not Found) if no notes are found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tags found");
        }

        // Return a 200 (OK) response with the list of notes
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }
    
    @GetMapping("/getTags/{tags}")
    public ResponseEntity<?> getNotesByTags(@PathVariable List<String> tags) {
        // Call the service method to get the notes by tags
        List<Note> notes = noteService.getNotesByTagListAndUserId(tags, 1);
        
        // Check if notes are found
        if (notes.isEmpty()) {
            // Return a 404 (Not Found) if no notes are found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No notes found for the given tags.");
        }
        
        Collections.reverse(notes);
        

        // Return a 200 (OK) response with the list of notes
        return ResponseEntity.status(HttpStatus.OK).body(notes);
    }
    
    
}
