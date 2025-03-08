package com.tus.proj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.tus.proj.note_managment.Note;
import com.tus.proj.note_managment.NoteRepository;

@Service
public class NoteService {
	
	private final NoteRepository noteRepository;
	
	@Autowired
	public NoteService(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}
	
	// Create or update a note
	public Note saveNote(Note note) {
		return noteRepository.save(note);
	}
	
	// Retrieve all notes
	public List<Note> getAllNotes() {
		return noteRepository.findAll();
	}
	
	// Retrieve a single note by ID
	public Optional<Note> getNoteById(int id) {
		return noteRepository.findById(id);
	}
	
	// Delete a note by ID
	public void deleteNote(int id) {
		noteRepository.deleteById(id);
	}
	
	// Update an existing note
	public Note updateNote(int id, Note updatedNote) {
		return noteRepository.findById(id).map(note -> {
			note.setTitle(updatedNote.getTitle());
			note.setContent(updatedNote.getContent());
			note.setPriority(updatedNote.getPriority());
			note.setDeadline(updatedNote.getDeadline());
			note.setTag(updatedNote.getTag());
			return noteRepository.save(note);
		}).orElseThrow(() -> new RuntimeException("Note not found"));
	}
}
