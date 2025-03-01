package com.tus.proj.user_managment;

import jakarta.persistence.*;
import java.util.List;

import com.tus.proj.note_managment.Note;


@Entity
@Table(name = "users") // Renamed to avoid conflicts with reserved keywords
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id; 
    
    private String username;
    private String password;

    @Enumerated(EnumType.STRING) // Stores role as a string in the database
    private UserRole role; // New field for user role

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) 
    private List<Note> notes; // One user has many notes

    // Constructors
    public User() {}

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
