package com.tus.proj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
	
	public List<Note> getNotesByTagListAndUserId(List<String> tags, int id) {
	    List<Note> allNotes = new ArrayList<>();
	    List<Note> userNotes = new ArrayList<>();
	    
	    for (String tag : tags) {
	        List<Note> notes = noteRepository.findByTag(tag);
	        
	        allNotes.addAll(notes);
	    }
	    
	    for(Note note: allNotes) {
	    	if(note.getUserId() == id) {
	    		userNotes.add(note);
	    	}
	    }
	    
	    return userNotes;
	}
	
	public List<String> getAllUniqueTagsByUserId(int id){
		return noteRepository.findDistinctTagsByUserId(id);
		
	}

	
	// Create or update a note
	public Note saveNote(Note note) {
		return noteRepository.save(note);
	}
	
	// Retrieve all notes
	public List<Note> getAllNotes() {
		return noteRepository.findAll();
	}
	
	public List<Note> getAllNotesByUserId(int id){
		return noteRepository.findByUserId(id);
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
