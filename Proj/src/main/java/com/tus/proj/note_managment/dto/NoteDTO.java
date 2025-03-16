package com.tus.proj.note_managment.dto;

import java.time.LocalDate;

import com.tus.proj.note_managment.Note;

public class NoteDTO {
    private int id;
    private String title;
    private String content;
    private String tag;
    private String access;
    private String priority;
    private LocalDate deadline;
    private String username; // Send only user ID

    public NoteDTO(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.tag = note.getTag();
        this.access = note.getAccess().name();
        this.priority = note.getPriority().name();
        this.deadline = note.getDeadline();
        this.username = note.getUser().getUsername();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
    
    
    
}
