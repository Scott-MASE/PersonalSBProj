package com.tus.proj.note_managment;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tus.proj.user_managment.User;

import jakarta.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Changed from userId to User object

    public Note() {}

    public Note(String title, String content, Priority priority, LocalDateTime deadline, User user, String tag) {
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.deadline = deadline;
        this.user = user;
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

    public User getUser() {  // Changed from getUserId to getUser
        return user;
    }

    public void setUser(User user) {  // Changed from setUserId to setUser
        this.user = user;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
