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


        EntityModel<Note> noteModel = EntityModel.of(savedNote);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(savedNote.getId())).withSelfRel();
        Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(savedNote.getId(), null)).withRel("update");
        Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(savedNote.getId())).withRel("delete");
        noteModel.add(selfLink, updateLink, deleteLink);

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
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(note.getId())).withRel("delete");
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
            EntityModel<Note> noteModel = EntityModel.of(note.get());
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(id)).withSelfRel();
            Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(id, null)).withRel("update");
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(id)).withRel("delete");
            noteModel.add(selfLink, updateLink, deleteLink);

            return ResponseEntity.ok(noteModel);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }
    }

    @PreAuthorize("hasRole('User')")
    @PutMapping("/{id}/meta")
    public ResponseEntity<?> updateNoteMeta(@PathVariable int id, @RequestBody CreateNoteRequestDTO noteRequest) {
        Optional<Note> existingNote = noteService.getNoteById(id);
        if (existingNote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        Note note = existingNote.get();
        note.setDeadline(noteRequest.getDeadline());
        note.setPriority(noteRequest.getPriority());
        note.setTag(noteRequest.getTag());
        note.setTitle(noteRequest.getTitle());

        Note savedNote = noteService.updateNote(id, note);

        // Adding HATEOAS links
        EntityModel<Note> noteModel = EntityModel.of(savedNote);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(savedNote.getId())).withSelfRel();
        Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(savedNote.getId(), null)).withRel("update");
        Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(savedNote.getId())).withRel("delete");
        noteModel.add(selfLink, updateLink, deleteLink);

        return ResponseEntity.ok(noteModel);
    }

    @PreAuthorize("hasRole('User')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteNote(@PathVariable int id) {
        Optional<Note> note = noteService.getNoteById(id);
        if (note.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        noteService.deleteNote(id);
        return ResponseEntity.ok("Note deleted successfully");
    }

    @PreAuthorize("hasRole('User')")
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

        // Adding HATEOAS links
        EntityModel<Note> noteModel = EntityModel.of(savedNote);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(savedNote.getId())).withSelfRel();
        Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(savedNote.getId(), null)).withRel("update");
        Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(savedNote.getId())).withRel("delete");
        noteModel.add(selfLink, updateLink, deleteLink);

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
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(note.getId())).withRel("delete");
            notesModel.add(selfLink, updateLink, deleteLink);
        }

        return ResponseEntity.status(HttpStatus.OK).body(notesModel);
    }
}
