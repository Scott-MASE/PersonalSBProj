package com.tus.proj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tus.proj.service.JwtService;
import com.tus.proj.service.NoteService;
import com.tus.proj.service.UserService;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRole;
import com.tus.proj.user_managment.dto.CreateUserRequestDTO;
import com.tus.proj.user_managment.dto.EditUserRequestDTO;
import com.tus.proj.user_managment.dto.LoginRequestDTO;
import com.tus.proj.user_managment.dto.LoginResponseDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final NoteService noteService;
    private final JwtService jwtService;

    public UserController(UserService userService, NoteService noteService,JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.noteService = noteService;
    }

    @PostMapping("/register")
    public ResponseEntity<EntityModel<User>> createUser(@RequestBody CreateUserRequestDTO request) {
        User user = new User();
        if (request.getUsername().length() >= 3) {
            user.setUsername(request.getUsername());
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (isValidPassword(request.getPassword())) {
            user.setPassword(request.getPassword());
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setRole(request.getRole());

        User createdUser = userService.createUser(user);

        EntityModel<User> userModel = EntityModel.of(createdUser);

        // Create self link and add it to the EntityModel
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel();
        userModel.add(selfLink);

        return new ResponseEntity<>(userModel, HttpStatus.CREATED);
    }

    private boolean isValidPassword(String password) {
        boolean containsUpperCase = false, containsLowerCase = false, containsNumber = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                containsUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                containsLowerCase = true;
            } else if (Character.isDigit(c)) {
                containsNumber = true;
            }
        }
        return ((password.length() >= 8) && containsUpperCase && containsLowerCase && containsNumber);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping
    public ResponseEntity<List<EntityModel<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<EntityModel<User>> userModels = users.stream()
            .map(user -> {
                EntityModel<User> userModel = EntityModel.of(user);
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(user.getId())).withSelfRel();
                Link editLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).editUser(user.getId(), null)).withRel("update");
                Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).deleteUser(user.getId())).withRel("delete");
                userModel.add(selfLink, editLink, deleteLink);
                return userModel;
            })
            .toList();

        return new ResponseEntity<>(userModels, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        boolean deleted = userService.deleteUser(id);

        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginValidation(@RequestBody LoginRequestDTO request) {
        try {
            // Authenticate the user
            User user = userService.authenticate(request.getUsername(), request.getPassword());
            Long userId = user.getId();
            String jwt = jwtService.generateToken(user.getUsername(), user.getRole());

            // Create the LoginResponseDTO object
            LoginResponseDTO loginResponse = new LoginResponseDTO();
            loginResponse.setJwt(jwt);

            // Create the HATEOAS links based on the role
            EntityModel<LoginResponseDTO> responseModel = EntityModel.of(loginResponse);

            // If the user is a regular User
            if (user.getRole() == UserRole.USER) {
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NoteController.class).getAllNotesById(0, "James")).withSelfRel(); // Example Note endpoint


                responseModel.add(selfLink);
            }

            if (user.getRole() == UserRole.ADMIN) {
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers()).withSelfRel();


                responseModel.add(selfLink);
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }


    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<EntityModel<User>> editUser(@PathVariable Long id, @RequestBody EditUserRequestDTO request) {
        Optional<User> existingUser = userService.getUserById(id);

        if (!existingUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        UserRole role = null;
        if (request.getRole() != null) {
            try {
                role = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        Optional<User> updatedUser = userService.editUser(id, request.getUsername(), request.getPassword(), role);

        if (updatedUser.isPresent()) {
        	User user = updatedUser.get();
            EntityModel<User> userModel = EntityModel.of(updatedUser.get());
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(user.getId())).withSelfRel();
            Link editLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).editUser(user.getId(), null)).withRel("update");
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).deleteUser(user.getId())).withRel("delete");
            userModel.add(selfLink, editLink, deleteLink);

            return ResponseEntity.ok(userModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        return user.map(existingUser -> {
            EntityModel<User> userModel = EntityModel.of(existingUser);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(existingUser.getId())).withSelfRel();
            Link editLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).editUser(existingUser.getId(), null)).withRel("update");
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).deleteUser(existingUser.getId())).withRel("delete");
            userModel.add(selfLink, editLink, deleteLink);
            return ResponseEntity.ok(userModel);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Long> getUserIdByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);

        return user.map(u -> ResponseEntity.ok(u.getId()))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    
    
}
