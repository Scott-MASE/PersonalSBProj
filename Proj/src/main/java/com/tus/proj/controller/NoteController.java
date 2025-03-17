package com.tus.proj.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tus.proj.note_managment.Note;
import com.tus.proj.note_managment.dto.CreateNoteRequestDTO;
import com.tus.proj.note_managment.dto.DeleteNoteRequestDTO;
import com.tus.proj.note_managment.dto.NoteDTO;
import com.tus.proj.note_managment.dto.UpdateNoteContentRequestDTO;
import com.tus.proj.note_managment.dto.UpdateNoteMetaRequestDTO;
import com.tus.proj.service.NoteService;
import com.tus.proj.service.UserService;
import com.tus.proj.user_managment.User;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
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

    // Helper method to retrieve the authenticated user.
    private Optional<User> getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username);
    }

    // adds the User type HATEOAS links
    private static EntityModel<Note> generateHATEOASLinks(Note savedNote) {
        EntityModel<Note> noteModel = EntityModel.of(savedNote);
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(savedNote.getId()))
                .withSelfRel();
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
    
    private EntityModel<Note> generateModHATEOASLinks(Note note) {
        EntityModel<Note> noteModel = EntityModel.of(note);


        noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMetaMod(note.getId(), null))
                .withRel("updateMeta"));
        noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteContentMod(note.getId(), null))
                .withRel("updateContent"));
        noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deletePublicNote(note.getId(), null))
                .withRel("delete"));

        return noteModel;
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/create")
    public ResponseEntity<?> createNote(@RequestBody CreateNoteRequestDTO noteRequest) {
        if (noteRequest.getTitle() == null || noteRequest.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
        }

        Optional<User> opUser = getAuthenticatedUser();
        if (opUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = opUser.get();

        Note note = new Note(
            noteRequest.getTitle(), 
            noteRequest.getContent(), 
            noteRequest.getPriority(),
            noteRequest.getDeadline(), 
            user, 
            noteRequest.getTag(), 
            noteRequest.getAccess()
        );
        Note savedNote = noteService.saveNote(note);
        EntityModel<Note> noteModel = generateHATEOASLinks(savedNote);
        return ResponseEntity.status(HttpStatus.CREATED).body(noteModel);
    }

    // Unused; 
//    @PreAuthorize("hasRole('Admin')")
//    @GetMapping("/getAll")
//    public ResponseEntity<CollectionModel<EntityModel<Note>>> getAllNotes() {
//        List<Note> notes = noteService.getAllNotes();
//        Collections.reverse(notes);
//        CollectionModel<EntityModel<Note>> notesModel = CollectionModel.wrap(notes);
//        for (Note note : notes) {
//            Link selfLink = WebMvcLinkBuilder
//                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(note.getId()))
//                    .withSelfRel();
//            Link updateLink = WebMvcLinkBuilder
//                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(note.getId(), null))
//                    .withRel("update");
//            Link deleteLink = WebMvcLinkBuilder
//                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(note.getId(), null))
//                    .withRel("delete");
//            notesModel.add(selfLink, updateLink, deleteLink);
//        }
//        return ResponseEntity.ok(notesModel);
//    }

    // Retrieves all notes of the logged in user with a custom order. can also return other users public notes
    @PreAuthorize("hasRole('User')")
    @GetMapping("/getAll/loggedUser/{order}/{pUsername}")
    public ResponseEntity<List<EntityModel<NoteDTO>>> getAllNotesById(@PathVariable int order, @PathVariable String pUsername) {
        List<Note> notes;
        if ("null".equals(pUsername)) {
            Optional<User> opUser = getAuthenticatedUser();
            if (opUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            notes = noteService.getAllNotesByUserId(opUser.get().getId());
        } else {
            Optional<User> opUser = userService.findByUsername(pUsername);
            if (opUser.isEmpty()) {
                // Fall back to authenticated user if provided username not found
                Optional<User> authUser = getAuthenticatedUser();
                if (authUser.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                notes = noteService.getAllNotesByUserId(authUser.get().getId());
            } else {
                notes = noteService.getPublicNotesByUsername(pUsername);
            }
        }
        if (notes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(List.of());
        }
        List<NoteDTO> noteDTOs = new ArrayList<>(notes.stream().map(NoteDTO::new).toList());
        switch (order) {
            case 0:
                noteDTOs.sort(Comparator.comparing(NoteDTO::getId).reversed());
                break;
            case 1:
                noteDTOs.sort(Comparator.comparing(NoteDTO::getPriority));
                break;
            case 2:
                noteDTOs.sort(Comparator.comparing(NoteDTO::getPriority).reversed());
                break;
            case 3:
                noteDTOs.sort(Comparator.comparing(note -> note.getTitle().toLowerCase()));
                break;
            case 4:
                noteDTOs.sort(Comparator.comparing(NoteDTO::getDeadline));
                break;
            default:
                break;
        }
        List<EntityModel<NoteDTO>> noteModels = noteDTOs.stream().map(noteDTO -> {
            EntityModel<NoteDTO> noteModel = EntityModel.of(noteDTO);
            noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class)
                    .getNoteById(noteDTO.getId()))
                    .withSelfRel());
            noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class)
                    .updateNoteMeta(noteDTO.getId(), null))
                    .withRel("updateMeta"));
            noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class)
                    .updateNoteContent(noteDTO.getId(), null))
                    .withRel("updateContent"));
            noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class)
                    .deleteNote(noteDTO.getId(), null))
                    .withRel("delete"));

            return noteModel;
        }).toList();

        return ResponseEntity.ok(noteModels);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable int id) {
        Optional<User> opUser = getAuthenticatedUser();
        if (opUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
        Optional<User> opUser = getAuthenticatedUser();
        if (opUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = opUser.get();
        Note note = existingNote.get();
        if (note.getUser().getId() != user.getId()) {
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
        return ResponseEntity.ok(generateHATEOASLinks(savedNote));
    }

    @PreAuthorize("hasRole('User')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteNote(@PathVariable int id, @RequestBody DeleteNoteRequestDTO deleteRequest) {
        if (!"confirmed".equalsIgnoreCase(deleteRequest.getUserConfirmation())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deletion not confirmed");
        }
        Optional<User> opUser = getAuthenticatedUser();
        if (opUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
        Optional<User> opUser = getAuthenticatedUser();
        if (opUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = opUser.get();
        Note note = existingNote.get();
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
        return ResponseEntity.ok(generateHATEOASLinks(savedNote));
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/getTags/loggedUser")
    public ResponseEntity<?> getAllTags() {
        Optional<User> opUser = getAuthenticatedUser();
        if (opUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = opUser.get();
        List<String> tags = noteService.getAllUniqueTagsByUserId(user.getId());
        if (tags.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tags found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/getTags/loggedUser/{tags}")
    public ResponseEntity<?> getNotesByTags(@PathVariable List<String> tags) {
        Optional<User> opUser = getAuthenticatedUser();
        if (opUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = opUser.get();
        List<Note> notes = noteService.getNotesByTagListAndUserId(tags, user.getId());
        if (notes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No notes found for the given tags.");
        }
        Collections.reverse(notes);
        CollectionModel<EntityModel<Note>> notesModel = CollectionModel.wrap(notes);
        for (Note note : notes) {
            Link selfLink = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(note.getId()))
                    .withSelfRel();
            Link updateLink = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMeta(note.getId(), null))
                    .withRel("update meta");
            Link deleteLink = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deleteNote(note.getId(), null))
                    .withRel("delete");
            Link contentLink = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteContent(note.getId(), null))
                    .withRel("update content");
            notesModel.add(selfLink, updateLink, deleteLink,contentLink);
        }
        return ResponseEntity.status(HttpStatus.OK).body(notesModel);
    }

//    @PreAuthorize("hasRole('User')")
//    @GetMapping("/getPublic/{username}")
//    public ResponseEntity<List<Note>> getPublicNotesByUsername(@PathVariable String username) {
//        Optional<User> opUser = userService.findByUsername(username);
//        if (opUser.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        User user = opUser.get();
//        List<Note> publicNotes = noteService.getPublicNotesByUserId(user.getId());
//        if (publicNotes.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(publicNotes);
//        }
//        return ResponseEntity.ok(publicNotes);
//    }

    @PreAuthorize("hasRole('Moderator')")
    @GetMapping("/getPublic/mod/{order}/{pUsername}")
    public ResponseEntity<List<EntityModel<NoteDTO>>>  getAllPublicNotes(@PathVariable int order, @PathVariable String pUsername) {
        List<Note> notes;
        if ("null".equals(pUsername)) {
            notes = noteService.getAllPublicNotes();
        } else {
            Optional<User> opUser = userService.findByUsername(pUsername);
            if (opUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                notes = noteService.getPublicNotesByUsername(pUsername);
            }
        }
        if (notes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(List.of());
        }
        List<NoteDTO> noteDTOs = new ArrayList<>(notes.stream().map(NoteDTO::new).toList());
        switch (order) {
            case 0:
                noteDTOs.sort(Comparator.comparing(NoteDTO::getId).reversed());
                break;
            case 1:
                noteDTOs.sort(Comparator.comparing(NoteDTO::getPriority).reversed());
                break;
            case 2:
                noteDTOs.sort(Comparator.comparing(NoteDTO::getPriority));
                break;
            case 3:
                noteDTOs.sort(Comparator.comparing(note -> note.getTitle().toLowerCase()));
                break;
            case 4:
                noteDTOs.sort(Comparator.comparing(NoteDTO::getDeadline));
                break;
            default:
                break;
        }

        
        List<EntityModel<NoteDTO>> noteModels = noteDTOs.stream().map(noteDTO -> {
            EntityModel<NoteDTO> noteModel = EntityModel.of(noteDTO);
            noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class)
                    .updateNoteMetaMod(noteDTO.getId(), null))
                    .withRel("updateMeta"));
            noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class)
                    .updateNoteContentMod(noteDTO.getId(), null))
                    .withRel("updateContent"));
            noteModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class)
                    .deletePublicNote(noteDTO.getId(), null))
                    .withRel("delete"));

            return noteModel;
        }).toList();

        return ResponseEntity.ok(noteModels);
    }

    @PreAuthorize("hasRole('Moderator')")
    @DeleteMapping("/{id}/delete/mod")
    public ResponseEntity<String> deletePublicNote(@PathVariable int id, @RequestBody DeleteNoteRequestDTO deleteRequest) {
        if (!"confirmed".equalsIgnoreCase(deleteRequest.getUserConfirmation())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deletion not confirmed");
        }
        Optional<Note> oPnote = noteService.getNoteById(id);
        if (oPnote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }
        noteService.deleteNote(id);
        return ResponseEntity.ok("Note deleted successfully");
    }

    @PreAuthorize("hasRole('Moderator')")
    @GetMapping("/getTags/publicTags")
    public ResponseEntity<?> getAllPublicTags() {
        List<String> tags = noteService.getAllUniquePublicTags();
        if (tags.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tags found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    @PreAuthorize("hasRole('Moderator')")
    @GetMapping("/getTags/public/{tags}")
    public ResponseEntity<?> getPublicNotesByTags(@PathVariable List<String> tags) {
        List<Note> notes = noteService.getPublicNotesByTags(tags);
        if (notes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No notes found for the given tags.");
        }
        CollectionModel<EntityModel<Note>> notesModel = CollectionModel.wrap(notes);
        for (Note note : notes) {
            Link selfLink = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getNoteById(note.getId()))
                    .withSelfRel();
            Link updateLink = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).updateNoteMetaMod(note.getId(), null))
                    .withRel("update");
            Link deleteLink = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).deletePublicNote(note.getId(), null))
                    .withRel("delete");
            notesModel.add(selfLink, updateLink, deleteLink);
        }
        return ResponseEntity.status(HttpStatus.OK).body(notesModel);
    }

    @PreAuthorize("hasRole('Moderator')")
    @PutMapping("/{id}/mod/meta")
    public ResponseEntity<?> updateNoteMetaMod(@PathVariable int id, @RequestBody UpdateNoteMetaRequestDTO noteRequest) {
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
        note.setAccess(noteRequest.getAccess());
        Note savedNote = noteService.updateNote(id, note);
        return ResponseEntity.ok(generateModHATEOASLinks(savedNote));
    }

    @PreAuthorize("hasRole('Moderator')")
    @PutMapping("/{id}/mod/content")
    public ResponseEntity<?> updateNoteContentMod(@PathVariable int id,
            @RequestBody UpdateNoteContentRequestDTO updateRequest) {
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
        return ResponseEntity.ok(generateModHATEOASLinks(savedNote));
    }
}
