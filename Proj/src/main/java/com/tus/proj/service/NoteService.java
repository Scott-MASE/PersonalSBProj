package com.tus.proj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tus.proj.note_managment.Access;
import com.tus.proj.note_managment.Note;
import com.tus.proj.note_managment.NoteRepository;

@Service
public class NoteService {
	
	private final NoteRepository noteRepository;
	
	@Autowired
	public NoteService(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}
	
	public List<Note> getNotesByTagListAndUserId(List<String> tags, Long id) {
	    List<Note> allNotes = new ArrayList<>();
	    List<Note> userNotes = new ArrayList<>();
	    
	    for (String tag : tags) {
	        List<Note> notes = noteRepository.findByTag(tag);
	        
	        allNotes.addAll(notes);
	    }
	    
	    for(Note note: allNotes) {
	    	if(note.getUser().getId() == id) {
	    		userNotes.add(note);
	    	}
	    }
	    
	    return userNotes;
	}
	
	public List<String> getAllUniqueTagsByUserId(Long id){
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
	
	public List<Note> getAllNotesByUserId(Long id){
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
	
    public void deleteNotesByUserId(Long userId) {
        noteRepository.deleteByUserId(userId);
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
	
	public List<Note> getPublicNotesByUserId(Long userId) {
	    List<Note> allNotes = noteRepository.findByUserId(userId);
	    return allNotes.stream()
	                   .filter(note -> note.getAccess() == Access.PUBLIC)
	                   .collect(Collectors.toList());
	}
	
    public List<Note> getPublicNotesByUsername(String username) {
        return noteRepository.findPublicNotesByUsername(username);
    }
    
    public List<Note> getAllPublicNotes() {
        return noteRepository.findAllPublicNotes();
    }
    
	public List<String> getAllUniquePublicTags() {
		return noteRepository.findDistinctPublicTags();
	}
	
	public List<Note> getPublicNotesByTags(List<String> tags) {
	    return noteRepository.findPublicNotesByTags(tags);
	}
}
