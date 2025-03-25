package com.tus.proj.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.tus.proj.config.JacksonConfig;
import com.tus.proj.controller.NoteController;
import com.tus.proj.note_managment.Note;
import com.tus.proj.note_managment.dto.CreateNoteRequestDTO;
import com.tus.proj.note_managment.dto.DeleteNoteRequestDTO;
import com.tus.proj.note_managment.dto.UpdateNoteContentRequestDTO;
import com.tus.proj.note_managment.dto.UpdateNoteMetaRequestDTO;
import com.tus.proj.service.JwtService;
import com.tus.proj.service.NoteService;
import com.tus.proj.service.UserService;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRole;
import com.tus.proj.note_managment.Access;
import com.tus.proj.note_managment.Priority;




import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc(addFilters = false)  // Disable all security filters, including CSRF
@WebMvcTest(NoteController.class)
@Import(NoteControllerTest.TestConfig.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // for converting DTO to JSON

    @TestConfiguration
    @Import(JacksonConfig.class)  // Import JacksonConfig to reuse its ObjectMapper bean
    static class TestConfig {
        @Bean
        public NoteService noteService() {
            return Mockito.mock(NoteService.class);
        }

        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }
    
    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("123");
        user.setRole(UserRole.USER);
        return user;
    }

    private List<Note> getMockNotes() {
        User user = getMockUser();
        return List.of(
            new Note("A", "Content A", Priority.LOW, LocalDate.parse("2025-12-31"), user, "tag1", Access.PUBLIC),
            new Note("B", "Content B", Priority.MEDIUM, LocalDate.parse("2025-10-20"), user, "tag2", Access.PUBLIC),
            new Note("C", "Content C", Priority.HIGH, LocalDate.parse("2025-08-15"), user, "tag3", Access.PRIVATE)
        );
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testCreateNote_Success() throws Exception {
        // Create sample DTO payload
        CreateNoteRequestDTO requestDTO = new CreateNoteRequestDTO();
        requestDTO.setTitle("Test Note");
        requestDTO.setContent("This is a test note");
        requestDTO.setPriority(Priority.MEDIUM);
        requestDTO.setDeadline(LocalDate.parse("2025-12-31"));
        requestDTO.setTag("test");
        requestDTO.setAccess(Access.PRIVATE);

        // Create a dummy authenticated user returned by the userService
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("123");
        user.setRole(UserRole.USER);
        System.out.println("find by user here:");
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Simulate note creation by noteService
        Note savedNote = new Note(
            requestDTO.getTitle(),
            requestDTO.getContent(),
            requestDTO.getPriority(),
            requestDTO.getDeadline(),
            user,
            requestDTO.getTag(),
            requestDTO.getAccess()
        );
        savedNote.setId(1); // assume an ID is assigned upon saving
        when(noteService.saveNote(any(Note.class))).thenReturn(savedNote);

        // Convert DTO to JSON
        String noteJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/notes/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(noteJson))
            .andExpect(status().isCreated());

    }
    
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllNotesById_IdDescending() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(getMockUser()));
        when(noteService.getAllNotesByUserId(1L)).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getAll/loggedUser/0/null"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllNotesById_PriorityAscending() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(getMockUser()));
        when(noteService.getAllNotesByUserId(1L)).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getAll/loggedUser/1/null"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllNotesById_PriorityDescending() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(getMockUser()));
        when(noteService.getAllNotesByUserId(1L)).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getAll/loggedUser/2/null"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllNotesById_TitleAscending() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(getMockUser()));
        when(noteService.getAllNotesByUserId(1L)).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getAll/loggedUser/3/null"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllNotesById_DeadlineAscending() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(getMockUser()));
        when(noteService.getAllNotesByUserId(1L)).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getAll/loggedUser/4/null"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllNotesById_UserNotFound() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notes/getAll/loggedUser/0/null"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllNotesById_EmptyList() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(getMockUser()));
        when(noteService.getAllNotesByUserId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notes/getAll/loggedUser/0/null"))
            .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllNotesById_publicName() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(getMockUser()));
        when(noteService.getAllNotesByUserId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notes/getAll/loggedUser/0/John"))
            .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNoteById_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Note note = new Note();
        note.setId(1);
        note.setTitle("Test Note");
        note.setUser(user);

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.of(note));

        mockMvc.perform(get("/api/notes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Note"))
            .andExpect(jsonPath("$._links.self.href").exists());  // Check HATEOAS link
    }

    // Forbidden Case: Authenticated user does not own the note
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNoteById_Forbidden() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("otherUser");

        Note note = new Note();
        note.setId(1);
        note.setTitle("Test Note");
        note.setUser(anotherUser);  // Note belongs to a different user

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.of(note));

        mockMvc.perform(get("/api/notes/1"))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You are not authorized to access or modify this note."));
    }

    // Not Found Case: Note does not exist
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNoteById_NotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.empty());  // Note not found

        mockMvc.perform(get("/api/notes/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Note not found"));
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testUpdateNoteMeta_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Note note = new Note();
        note.setId(1);
        note.setUser(user);

        UpdateNoteMetaRequestDTO updateRequest = new UpdateNoteMetaRequestDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");
        updateRequest.setPriority(Priority.HIGH);
        updateRequest.setTag("updated-tag");
        updateRequest.setDeadline(LocalDate.parse("2025-12-31"));
        updateRequest.setAccess(Access.PRIVATE);

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.of(note));
        when(noteService.updateNote(anyInt(), any(Note.class))).thenReturn(note);

        mockMvc.perform(put("/api/notes/1/meta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$._links.self.href").exists());  // Check HATEOAS link
    }

    // Forbidden Case: Authenticated user does not own the note
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testUpdateNoteMeta_Forbidden() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("otherUser");

        Note note = new Note();
        note.setId(1);
        note.setUser(anotherUser);  // Note belongs to a different user

        UpdateNoteMetaRequestDTO updateRequest = new UpdateNoteMetaRequestDTO();
        updateRequest.setTitle("Unauthorized Update");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.of(note));

        mockMvc.perform(put("/api/notes/1/meta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You are not authorized to access or modify this note."));
    }

    // Not Found Case: Note does not exist
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testUpdateNoteMeta_NotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        UpdateNoteMetaRequestDTO updateRequest = new UpdateNoteMetaRequestDTO();
        updateRequest.setTitle("Nonexistent Note Update");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.empty());  // Note not found

        mockMvc.perform(put("/api/notes/1/meta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Note not found"));
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testDeleteNote_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Note note = new Note();
        note.setId(1);
        note.setUser(user);

        DeleteNoteRequestDTO deleteRequest = new DeleteNoteRequestDTO();
        deleteRequest.setUserConfirmation("confirmed");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.of(note));

        mockMvc.perform(delete("/api/notes/1/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("Note deleted successfully"));

    }

    // Bad Request Case: Deletion not confirmed
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testDeleteNote_BadRequest() throws Exception {
        DeleteNoteRequestDTO deleteRequest = new DeleteNoteRequestDTO();
        deleteRequest.setUserConfirmation("not confirmed");

        mockMvc.perform(delete("/api/notes/1/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Deletion not confirmed"));
    }

    // Forbidden Case: Authenticated user does not own the note
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testDeleteNote_Forbidden() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("otherUser");

        Note note = new Note();
        note.setId(1);
        note.setUser(anotherUser);  // Note belongs to another user

        DeleteNoteRequestDTO deleteRequest = new DeleteNoteRequestDTO();
        deleteRequest.setUserConfirmation("confirmed");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.of(note));

        mockMvc.perform(delete("/api/notes/1/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You are not authorized to access or modify this note."));
    }

    // Not Found Case: Note does not exist
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testDeleteNote_NotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        DeleteNoteRequestDTO deleteRequest = new DeleteNoteRequestDTO();
        deleteRequest.setUserConfirmation("confirmed");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.empty());  // Note not found

        mockMvc.perform(delete("/api/notes/1/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Note not found"));
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testUpdateNoteContent_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Note note = new Note();
        note.setId(1);
        note.setUser(user);
        note.setContent("Original content");

        UpdateNoteContentRequestDTO updateRequest = new UpdateNoteContentRequestDTO();
        updateRequest.setContent("Updated content");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.of(note));
        when(noteService.updateNote(anyInt(), any(Note.class))).thenReturn(note);

        mockMvc.perform(put("/api/notes/1/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk());

        verify(noteService, times(1)).updateNote(1, note);
    }

    // Forbidden Case: Authenticated user does not own the note
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testUpdateNoteContent_Forbidden() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("otherUser");

        Note note = new Note();
        note.setId(1);
        note.setUser(anotherUser);

        UpdateNoteContentRequestDTO updateRequest = new UpdateNoteContentRequestDTO();
        updateRequest.setContent("Attempted unauthorized update");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.of(note));

        mockMvc.perform(put("/api/notes/1/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You are not authorized to access or modify this note."));

    }

    // Not Found Case: Note does not exist
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testUpdateNoteContent_NotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        UpdateNoteContentRequestDTO updateRequest = new UpdateNoteContentRequestDTO();
        updateRequest.setContent("Some content");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNoteById(1)).thenReturn(Optional.empty());  // Note not found

        mockMvc.perform(put("/api/notes/1/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Note not found"));

        verify(noteService, never()).updateNote(anyInt(), any(Note.class));
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllTags_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        List<String> tags = Arrays.asList("work", "personal", "important");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getAllUniqueTagsByUserId(user.getId())).thenReturn(tags);

        mockMvc.perform(get("/api/notes/getTags/loggedUser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("work"))
            .andExpect(jsonPath("$[1]").value("personal"))
            .andExpect(jsonPath("$[2]").value("important"));
    }

    // Not Found Case: No authenticated user
    @Test
    @WithMockUser(username = "invalidUser", roles = {"User"})
    void testGetAllTags_NoAuthenticatedUser() throws Exception {
        when(userService.findByUsername("invalidUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notes/getTags/loggedUser"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(""));
    }

    // Not Found Case: No tags found for authenticated user
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetAllTags_NoTagsFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getAllUniqueTagsByUserId(user.getId())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notes/getTags/loggedUser"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("No tags found"));
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNotesByTags_OrderByIdDescending() throws Exception {
        User user = getMockUser();
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNotesByTagListAndUserId(anyList(), eq(user.getId()))).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getTags/loggedUser/tag1,tag2/0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.noteDTOList[0].title").value("A"))
            .andExpect(jsonPath("$._embedded.noteDTOList[1].title").value("B"))
            .andExpect(jsonPath("$._embedded.noteDTOList[2].title").value("C"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNotesByTags_OrderByPriorityAscending() throws Exception {
        User user = getMockUser();
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNotesByTagListAndUserId(anyList(), eq(user.getId()))).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getTags/loggedUser/tag1,tag2/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.noteDTOList[0].priority").value("HIGH"))
            .andExpect(jsonPath("$._embedded.noteDTOList[1].priority").value("MEDIUM"))
            .andExpect(jsonPath("$._embedded.noteDTOList[2].priority").value("LOW"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNotesByTags_OrderByPriorityDescending() throws Exception {
        User user = getMockUser();
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNotesByTagListAndUserId(anyList(), eq(user.getId()))).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getTags/loggedUser/tag1,tag2/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.noteDTOList[0].priority").value("LOW"))
            .andExpect(jsonPath("$._embedded.noteDTOList[1].priority").value("MEDIUM"))
            .andExpect(jsonPath("$._embedded.noteDTOList[2].priority").value("HIGH"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNotesByTags_OrderByTitle() throws Exception {
        User user = getMockUser();
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNotesByTagListAndUserId(anyList(), eq(user.getId()))).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getTags/loggedUser/tag1,tag2/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.noteDTOList[0].title").value("A"))
            .andExpect(jsonPath("$._embedded.noteDTOList[1].title").value("B"))
            .andExpect(jsonPath("$._embedded.noteDTOList[2].title").value("C"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNotesByTags_OrderByDeadline() throws Exception {
        User user = getMockUser();
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNotesByTagListAndUserId(anyList(), eq(user.getId()))).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getTags/loggedUser/tag1,tag2/4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.noteDTOList[0].deadline[0]").value(2025))
            .andExpect(jsonPath("$._embedded.noteDTOList[0].deadline[1]").value(8))
            .andExpect(jsonPath("$._embedded.noteDTOList[0].deadline[2]").value(15))
            .andExpect(jsonPath("$._embedded.noteDTOList[1].deadline[0]").value(2025))
            .andExpect(jsonPath("$._embedded.noteDTOList[1].deadline[1]").value(10))
            .andExpect(jsonPath("$._embedded.noteDTOList[1].deadline[2]").value(20))
            .andExpect(jsonPath("$._embedded.noteDTOList[2].deadline[0]").value(2025))
            .andExpect(jsonPath("$._embedded.noteDTOList[2].deadline[1]").value(12))
            .andExpect(jsonPath("$._embedded.noteDTOList[2].deadline[2]").value(31));
    }

    @Test
    @WithMockUser(username = "invalidUser", roles = {"User"})
    void testGetNotesByTags_NoAuthenticatedUser() throws Exception {
        when(userService.findByUsername("invalidUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notes/getTags/loggedUser/tag1,tag2/0"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"User"})
    void testGetNotesByTags_NoNotesFound() throws Exception {
        User user = getMockUser();
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getNotesByTagListAndUserId(anyList(), eq(user.getId()))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notes/getTags/loggedUser/unknown/0"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("No notes found for the given tags."));
    }
    
    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testGetAllPublicNotes_ValidUsername() throws Exception {
        User user = getMockUser(); // Mock user for pUsername
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getPublicNotesByUsername("testUser")).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getPublic/mod/0/testUser"))
            .andExpect(status().isOk());

    }

    
    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testGetAllPublicNotes_AllPublicNotes() throws Exception {
        when(noteService.getAllPublicNotes()).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getPublic/mod/0/null"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testGetAllPublicNotes_NoNotesFound() throws Exception {
        User user = getMockUser();
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(noteService.getPublicNotesByUsername("testUser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notes/getPublic/mod/0/testUser"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testGetAllPublicNotes_InvalidUsername() throws Exception {
        when(userService.findByUsername("invalidUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notes/getPublic/mod/0/invalidUser"))
            .andExpect(status().isNotFound());
    }

    
    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testGetAllPublicNotes_SortedByPriority() throws Exception {
        when(noteService.getAllPublicNotes()).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getPublic/mod/1/null"))
            .andExpect(status().isOk());

    }
    
    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testGetAllPublicNotes_SortedByPriorityReversed() throws Exception {
        when(noteService.getAllPublicNotes()).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getPublic/mod/2/null"))
            .andExpect(status().isOk());

    }
    
    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testGetAllPublicNotes_SortedByTitle() throws Exception {
        when(noteService.getAllPublicNotes()).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getPublic/mod/3/null"))
            .andExpect(status().isOk());

    }
    
    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testGetAllPublicNotes_SortedByDeadline() throws Exception {
        when(noteService.getAllPublicNotes()).thenReturn(getMockNotes());

        mockMvc.perform(get("/api/notes/getPublic/mod/4/null"))
            .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testDeletePublicNote_Success() throws Exception {
        int noteId = 1;
        DeleteNoteRequestDTO deleteRequest = new DeleteNoteRequestDTO();
        deleteRequest.setUserConfirmation("confirmed");

        // Mock the note service to return a valid note when searching by ID
        when(noteService.getNoteById(noteId)).thenReturn(Optional.of(new Note()));

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/notes/{id}/delete/mod", noteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Note deleted successfully"));

        // Verify that the delete service method is called once
        verify(noteService, times(1)).deleteNote(noteId);
    }

    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testDeletePublicNote_DeletionNotConfirmed() throws Exception {
        int noteId = 1;
        DeleteNoteRequestDTO deleteRequest = new DeleteNoteRequestDTO();
        deleteRequest.setUserConfirmation("incorrect"); // Simulate incorrect confirmation

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/notes/{id}/delete/mod", noteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deletion not confirmed"));

    }
    
    @Test
    @WithMockUser(username = "moderatorUser", roles = {"Moderator"})
    void testDeletePublicNote_NoteNotFound() throws Exception {
        int noteId = 1;
        DeleteNoteRequestDTO deleteRequest = new DeleteNoteRequestDTO();
        deleteRequest.setUserConfirmation("confirmed");

        // Mock the note service to return an empty Optional, simulating a not found note
        when(noteService.getNoteById(noteId)).thenReturn(Optional.empty());

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/notes/{id}/delete/mod", noteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found"));
    }






}
