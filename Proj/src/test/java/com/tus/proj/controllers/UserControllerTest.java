package com.tus.proj.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.proj.config.JacksonConfig;
import com.tus.proj.controller.UserController;
import com.tus.proj.service.JwtService;
import com.tus.proj.service.NoteService;
import com.tus.proj.service.UserService;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRole;
import com.tus.proj.user_managment.dto.CreateUserRequestDTO;
import com.tus.proj.user_managment.dto.EditUserRequestDTO;
import com.tus.proj.user_managment.dto.LoginRequestDTO;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable all security filters, including CSRF
@Import({UserControllerTest.TestConfig.class, JacksonConfig.class})
class UserControllerTest {

    @TestConfiguration
    @Import(JacksonConfig.class) 
    public static class TestConfig {
        @Bean
        UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        NoteService noteService() {
            return Mockito.mock(NoteService.class);
        }

        @Bean
        JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper to create a mock user.
    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("Password1");
        user.setRole(UserRole.USER);
        return user;
    }


    @Test
    void testCreateUser_Success() throws Exception {
        CreateUserRequestDTO requestDTO = new CreateUserRequestDTO();
        requestDTO.setUsername("validUser");
        requestDTO.setPassword("ValidPass1");
        requestDTO.setRole(UserRole.USER);

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("validUser");
        createdUser.setPassword("ValidPass1");
        createdUser.setRole(UserRole.USER);

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testCreateUser_InvalidUsername() throws Exception {
        CreateUserRequestDTO requestDTO = new CreateUserRequestDTO();
        requestDTO.setUsername("ab"); // too short
        requestDTO.setPassword("ValidPass1");
        requestDTO.setRole(UserRole.USER);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUser_InvalidPassword() throws Exception {
        CreateUserRequestDTO requestDTO = new CreateUserRequestDTO();
        requestDTO.setUsername("validUser");
        requestDTO.setPassword("short"); // invalid password (too short, no uppercase, etc.)
        requestDTO.setRole(UserRole.USER);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = "Admin")
    void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setRole(UserRole.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setRole(UserRole.ADMIN);

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk());

    }


    @Test
    @WithMockUser(roles = "Admin")
    void testDeleteUser_Success() throws Exception {
        Long userId = 1L;
        when(userService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/users/delete/{id}", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "Admin")
    void testDeleteUser_NotFound() throws Exception {
        Long userId = 1L;
        when(userService.deleteUser(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/users/delete/{id}", userId))
            .andExpect(status().isNotFound());
    }


    @Test
    void testLoginValidation_Success_User() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("testUser");
        requestDTO.setPassword("ValidPass1");

        User user = getMockUser();

        when(userService.authenticate("testUser", "ValidPass1")).thenReturn(user);
        when(jwtService.generateToken("testUser", UserRole.USER)).thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.Create.href").exists())
            .andExpect(jsonPath("$._links.Tags.href").exists());
    }

    @Test
    void testLoginValidation_Success_Admin() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("adminUser");
        requestDTO.setPassword("ValidPass1");

        User admin = new User();
        admin.setId(2L);
        admin.setUsername("adminUser");
        admin.setRole(UserRole.ADMIN);

        when(userService.authenticate("adminUser", "ValidPass1")).thenReturn(admin);
        when(jwtService.generateToken("adminUser", UserRole.ADMIN)).thenReturn("admin-jwt-token");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testLoginValidation_Success_Moderator() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("modUser");
        requestDTO.setPassword("ValidPass1");

        User moderator = new User();
        moderator.setId(3L);
        moderator.setUsername("modUser");
        moderator.setRole(UserRole.MODERATOR);

        when(userService.authenticate("modUser", "ValidPass1")).thenReturn(moderator);
        when(jwtService.generateToken("modUser", UserRole.MODERATOR)).thenReturn("mod-jwt-token");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.Tags.href").exists());
    }

    @Test
    void testLoginValidation_Failure() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("wrongUser");
        requestDTO.setPassword("wrongPassword");

        when(userService.authenticate("wrongUser", "wrongPassword"))
            .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Invalid username or password"));
    }


    @Test
    @WithMockUser(roles = "Admin")
    void testEditUser_Success() throws Exception {
        Long userId = 1L;
        EditUserRequestDTO requestDTO = new EditUserRequestDTO();
        requestDTO.setUsername("updatedUser");
        requestDTO.setPassword("UpdatedPass1");
        requestDTO.setRole("USER");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUser");
        existingUser.setRole(UserRole.USER);

        when(userService.getUserById(userId)).thenReturn(Optional.of(existingUser));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername(requestDTO.getUsername());
        updatedUser.setRole(UserRole.USER);

        when(userService.editUser(eq(userId), eq(requestDTO.getUsername()), 
                eq(requestDTO.getPassword()), any())).thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/api/users/edit/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.update.href").exists())
            .andExpect(jsonPath("$._links.delete.href").exists());
    }

    @Test
    @WithMockUser(roles = "Admin")
    void testEditUser_NotFound() throws Exception {
        Long userId = 1L;
        EditUserRequestDTO requestDTO = new EditUserRequestDTO();
        requestDTO.setUsername("updatedUser");
        requestDTO.setPassword("UpdatedPass1");
        requestDTO.setRole("USER");

        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/edit/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "Admin")
    void testGetUserById_Success() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        user.setRole(UserRole.USER);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.update.href").exists())
            .andExpect(jsonPath("$._links.delete.href").exists());
    }

    @Test
    @WithMockUser(roles = "Admin")
    void testGetUserById_NotFound() throws Exception {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", userId))
            .andExpect(status().isNotFound());
    }


    @Test
    void testGetUserIdByUsername_Success() throws Exception {
        String username = "testUser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/username/{username}", username))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }

    @Test
    void testGetUserIdByUsername_NotFound() throws Exception {
        String username = "unknownUser";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/username/{username}", username))
            .andExpect(status().isNotFound());
    }
}