package com.tus.proj.note_managment;

import java.time.LocalDate;
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
    private LocalDate deadline;

    private Long userId;  // Changed from User to userId as an integer

    public Note() {}

    public Note(String title, String content, Priority priority, LocalDate deadline, Long userId, String tag) {
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate localDate) {
        this.deadline = localDate;
    }

    public Long getUserId() {
        return userId;  // Getter for userId
    }

    public void setUserId(Long userId) {
        this.userId = userId;  // Setter for userId
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
