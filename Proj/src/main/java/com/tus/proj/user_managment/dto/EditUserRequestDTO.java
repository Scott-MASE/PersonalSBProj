package com.tus.proj.user_managment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequestDTO {
    private String username;
    private String password;
    private String role; // Should be a string since frontend sends it as a string

}
