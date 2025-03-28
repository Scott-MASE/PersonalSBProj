package com.tus.proj.user_managment.dto;

import com.tus.proj.user_managment.UserRole;

public class UpdateUserRequestDTO {
    private String username;
    private String password;
    private UserRole role;

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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
