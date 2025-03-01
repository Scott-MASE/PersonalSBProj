package com.tus.proj.note_managment;

import com.tus.proj.user_managment.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING) 
    private Priority priority;

    private LocalDateTime deadline; 

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) 
    private User user;

    // Constructors
    public Note() {}

    public Note(String title, String content, Priority priority, LocalDateTime deadline, User user) {
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.deadline = deadline;
        this.user = user;
    }

    // Getters and Setters
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
