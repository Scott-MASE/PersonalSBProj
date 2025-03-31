package com.tus.proj.services;

import com.tus.proj.note_managment.Access;
import com.tus.proj.note_managment.Note;
import com.tus.proj.note_managment.NoteRepository;
import com.tus.proj.note_managment.Priority;
import com.tus.proj.service.NoteService;
import com.tus.proj.user_managment.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note note1;
    private Note note2;
    private User user1;
    private User user2;

    @BeforeEach
     void setUp() {
        // Create dummy users
        user1 = new User();
        user1.setId(1L);

        user2 = new User();
        user2.setId(2L);

        // Create dummy notes
        note1 = new Note();
        note1.setId(1);
        note1.setTitle("Note 1");
        note1.setContent("Content 1");
        note1.setTag("tag1");
        note1.setUser(user1);
        note1.setAccess(Access.PUBLIC);
        note1.setPriority(Priority.MEDIUM);
        note1.setDeadline(LocalDate.parse("2025-12-31")); // assuming deadline is a String

        note2 = new Note();
        note2.setId(2);
        note2.setTitle("Note 2");
        note2.setContent("Content 2");
        note2.setTag("tag2");
        note2.setUser(user2);
        note2.setAccess(Access.PRIVATE);
        note2.setPriority(Priority.LOW);
        note2.setDeadline(LocalDate.parse("2025-11-20"));
    }

    @Test
     void testGetNotesByTagListAndUserId() {
        // Arrange: return one note for each tag
        when(noteRepository.findByTag("tag1")).thenReturn(Collections.singletonList(note1));
        when(noteRepository.findByTag("tag2")).thenReturn(Collections.singletonList(note2));

        // Act: filter by userId = 1 (should return only note1)
        List<Note> result = noteService.getNotesByTagListAndUserId(Arrays.asList("tag1", "tag2"), 1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(note1, result.get(0));
    }

    @Test
     void testGetAllUniqueTagsByUserId() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        when(noteRepository.findDistinctTagsByUserId(1L)).thenReturn(tags);

        List<String> result = noteService.getAllUniqueTagsByUserId(1L);
        assertEquals(tags, result);
    }

    @Test
     void testSaveNote() {
        when(noteRepository.save(note1)).thenReturn(note1);

        Note saved = noteService.saveNote(note1);
        assertEquals(note1, saved);
    }

    @Test
     void testGetAllNotes() {
        List<Note> notes = Arrays.asList(note1, note2);
        when(noteRepository.findAll()).thenReturn(notes);

        List<Note> result = noteService.getAllNotes();
        assertEquals(notes, result);
    }

    @Test
     void testGetAllNotesByUserId() {
        List<Note> notes = Collections.singletonList(note1);
        when(noteRepository.findByUserId(1L)).thenReturn(notes);

        List<Note> result = noteService.getAllNotesByUserId(1L);
        assertEquals(notes, result);
    }

    @Test
     void testGetNoteById() {
        when(noteRepository.findById(1)).thenReturn(Optional.of(note1));

        Optional<Note> result = noteService.getNoteById(1);
        assertTrue(result.isPresent());
        assertEquals(note1, result.get());
    }

    @Test
     void testDeleteNote() {
        // Act
        noteService.deleteNote(1);

        // Assert that deleteById was called once
        verify(noteRepository, times(1)).deleteById(1);
    }

    @Test
     void testDeleteNotesByUserId() {
        // Act
        noteService.deleteNotesByUserId(1L);

        // Assert that deleteByUserId was called once
        verify(noteRepository, times(1)).deleteByUserId(1L);
    }

    @Test
     void testUpdateNoteSuccess() {
        // Arrange: Prepare an updated note with new values
        Note updatedNote = new Note();
        updatedNote.setTitle("Updated Title");
        updatedNote.setContent("Updated Content");
        updatedNote.setPriority(Priority.HIGH);
        updatedNote.setDeadline(LocalDate.parse("2025-10-20"));
        updatedNote.setTag("updatedTag");

        when(noteRepository.findById(1)).thenReturn(Optional.of(note1));
        when(noteRepository.save(any(Note.class))).thenReturn(note1);

        // Act
        Note result = noteService.updateNote(1, updatedNote);

        // Assert: verify that note1 was updated
        assertEquals("Updated Title", note1.getTitle());
        assertEquals("Updated Content", note1.getContent());
        assertEquals(Priority.HIGH, note1.getPriority());
        assertEquals(LocalDate.parse("2025-10-20"), note1.getDeadline());
        assertEquals("updatedTag", note1.getTag());
        assertEquals(note1, result);
    }

    @Test
     void testUpdateNoteNotFound() {
        // Arrange: repository returns empty
        when(noteRepository.findById(1)).thenReturn(Optional.empty());
        Note updatedNote = new Note();

        // Act & Assert: expect RuntimeException with message "Note not found"
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            noteService.updateNote(1, updatedNote);
        });
        assertEquals("Note not found", exception.getMessage());
    }

    @Test
     void testGetPublicNotesByUserId() {
        // Arrange: for a given user, return both a public and a private note
        Note publicNote = note1; // note1 is PUBLIC
        // Simulate repository call for user with id 1 returns only publicNote
        when(noteRepository.findByUserId(1L)).thenReturn(Collections.singletonList(publicNote));

        // Act
        List<Note> result = noteService.getPublicNotesByUserId(1L);

        // Assert: Only public note should be returned
        assertEquals(1, result.size());
        assertEquals(publicNote, result.get(0));
    }

    @Test
     void testGetPublicNotesByUsername() {
        List<Note> notes = Arrays.asList(note1);
        when(noteRepository.findPublicNotesByUsername("john")).thenReturn(notes);

        List<Note> result = noteService.getPublicNotesByUsername("john");
        assertEquals(notes, result);
    }

    @Test
     void testGetAllPublicNotes() {
        List<Note> notes = Arrays.asList(note1);
        when(noteRepository.findAllPublicNotes()).thenReturn(notes);

        List<Note> result = noteService.getAllPublicNotes();
        assertEquals(notes, result);
    }

    @Test
     void testGetAllUniquePublicTags() {
        List<String> tags = Arrays.asList("tag1");
        when(noteRepository.findDistinctPublicTags()).thenReturn(tags);

        List<String> result = noteService.getAllUniquePublicTags();
        assertEquals(tags, result);
    }

    @Test
     void testGetPublicNotesByTags() {
        List<Note> notes = Arrays.asList(note1);
        List<String> tags = Arrays.asList("tag1");
        when(noteRepository.findPublicNotesByTags(tags)).thenReturn(notes);

        List<Note> result = noteService.getPublicNotesByTags(tags);
        assertEquals(notes, result);
    }
}
