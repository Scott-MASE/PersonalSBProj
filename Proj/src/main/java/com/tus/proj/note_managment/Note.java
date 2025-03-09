package com.tus.proj.note_managment;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String content;
    private String tag;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deadline;

    private int userId;  // Changed from User to userId as an integer

    public Note() {}

    public Note(String title, String content, Priority priority, LocalDateTime deadline, int userId, String tag) {
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.deadline = deadline;
        this.userId = userId;
        this.tag = tag;
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public int getUserId() {
        return userId;  // Getter for userId
    }

    public void setUserId(int userId) {
        this.userId = userId;  // Setter for userId
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
