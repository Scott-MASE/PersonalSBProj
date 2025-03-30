package com.tus.proj.user_managment.dto;

import com.tus.proj.user_managment.UserRole;

import lombok.Data;

@Data
public class UpdateUserRequestDTO {
    private String username;
    private String password;
    private UserRole role;
}
