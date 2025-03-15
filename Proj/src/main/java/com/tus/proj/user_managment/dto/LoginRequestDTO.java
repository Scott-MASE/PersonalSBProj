package com.tus.proj.user_managment.dto;

public class LoginRequestDTO {
    private String username;
    private String password;

    // Default constructor
    public LoginRequestDTO() {}

    // Getters and Setters
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
}
