package com.tus.proj.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tus.proj.note_managment.Note;
import com.tus.proj.note_managment.dto.CreateNoteRequestDTO;
import com.tus.proj.note_managment.dto.DeleteNoteRequestDTO;
import com.tus.proj.note_managment.dto.UpdateNoteContentRequestDTO;
import com.tus.proj.note_managment.dto.UpdateNoteMetaRequestDTO;
import com.tus.proj.service.NoteService;
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
    
    private static EntityModel<Note> generateHATEOASLinks(Note savedNote){
        EntityModel<Note> noteModel = EntityModel.of(savedNote);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(savedNote.getId())).withSelfRel();
        Link updateMetaLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(savedNote.getId(), null)).withRel("updateMeta");
        Link updateContentLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteContent(savedNote.getId(), null)).withRel("updateContent");
        Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(savedNote.getId(), null)).withRel("delete");
        noteModel.add(selfLink, updateMetaLink,updateContentLink, deleteLink);
        return noteModel;
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/create")
    public ResponseEntity<?> createNote(@RequestBody CreateNoteRequestDTO noteRequest) {
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


        EntityModel<Note> noteModel = generateHATEOASLinks(savedNote);


        return ResponseEntity.status(HttpStatus.CREATED).body(noteModel);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/getAll")
    public ResponseEntity<CollectionModel<EntityModel<Note>>> getAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        Collections.reverse(notes);

        // Wrapping notes in HATEOAS
        CollectionModel<EntityModel<Note>> notesModel = CollectionModel.wrap(notes);

        for (Note note : notes) {
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel();
            Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(note.getId(), null)).withRel("update");
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(note.getId(), null)).withRel("delete");
            notesModel.add(selfLink, updateLink, deleteLink);
        }

        return ResponseEntity.ok(notesModel);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/{id}/getAll")
    public ResponseEntity<List<Note>> getAllNotesById(@PathVariable int id) {
        List<Note> notes = noteService.getAllNotesByUserId(id);
        Collections.reverse(notes);

        return ResponseEntity.ok(notes);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable int id) {
        Optional<Note> note = noteService.getNoteById(id);

        if (note.isPresent()) {
        	EntityModel<Note> noteModel = generateHATEOASLinks(note.get());

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

        Note note = existingNote.get();
        note.setDeadline(noteRequest.getDeadline());
        note.setPriority(noteRequest.getPriority());
        note.setTag(noteRequest.getTag());
        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());

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

        Optional<Note> note = noteService.getNoteById(id);
        if (note.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        noteService.deleteNote(id);
        return ResponseEntity.ok("Note deleted successfully");
    }


    @PreAuthorize("hasRole('User')")
    @PutMapping("/{id}/content")
    public ResponseEntity<?> updateNoteContent(@PathVariable int id, @RequestBody UpdateNoteContentRequestDTO updateRequest) {
        Optional<Note> existingNote = noteService.getNoteById(id);
        if (existingNote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        Note note = existingNote.get();

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
    @GetMapping("{id}/getTags")
    public ResponseEntity<?> getAllTags(@PathVariable int id) {
        List<String> tags = noteService.getAllUniqueTagsByUserId(id);

        // Check if notes are found
        if (tags.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tags found");
        }

        // Return tags with HATEOAS links
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/getTags/{tags}/{id}")
    public ResponseEntity<?> getNotesByTags(@PathVariable List<String> tags, @PathVariable int id) {
        // Call the service method to get the notes by tags
        List<Note> notes = noteService.getNotesByTagListAndUserId(tags, id);

        // Check if notes are found
        if (notes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No notes found for the given tags.");
        }

        Collections.reverse(notes);


        CollectionModel<EntityModel<Note>> notesModel = CollectionModel.wrap(notes);
        for (Note note : notes) {
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(note.getId())).withSelfRel();
            Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(note.getId(), null)).withRel("update");
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(note.getId(), null)).withRel("delete");
            notesModel.add(selfLink, updateLink, deleteLink);
        }

        return ResponseEntity.status(HttpStatus.OK).body(notesModel);
    }
}
