package com.tus.proj.note_managment.dto;

import java.time.LocalDate;

import com.tus.proj.note_managment.Note;

import lombok.Data;

@Data
public class NoteDTO {
    private int id;
    private String title;
    private String content;
    private String tag;
    private String access;
    private String priority;
    private LocalDate deadline;
    private String username; 

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

    
    
    
}
