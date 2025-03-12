package com.tus.proj.user_managment;

public class LoginResponse {
    private String message;
    private Integer userId;
    private String username;
    private String role;  // Add role field

    public LoginResponse(String message, Integer userId, String username, String role) {
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {  // Getter for role
        return role;
    }

    public void setRole(String role) {  // Setter for role
        this.role = role;
    }
}
