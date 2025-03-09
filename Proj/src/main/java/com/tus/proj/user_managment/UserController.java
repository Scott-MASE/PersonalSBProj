package com.tus.proj.user_managment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tus.proj.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        // Validate username
        if (request.getUsername().length() < 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username must be at least 2 characters long.");
        }

        // Validate password
        if (!isValidPassword(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must meet security requirements.");
        }

        // Check if username exists
        if (userService.findUserByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken.");
        }

        // Save user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(UserRole.USER); // Set role to USER

        User createdUser = userService.saveUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginValidation(@RequestParam String username,
                                                         @RequestParam String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                // Generate JWT Token
                String token = userService.generateJwtToken(user);

                // Create LoginResponse with message and userId
                LoginResponse loginResponse = new LoginResponse("Success", user.getId());

                return ResponseEntity.ok(loginResponse);
            } else {
                // Invalid password
                LoginResponse loginResponse = new LoginResponse("Invalid password", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            }
        } else {
            // User not found
            LoginResponse loginResponse = new LoginResponse("User not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(loginResponse);
        }
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[*@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    @GetMapping("username")
    public ResponseEntity<String> getCurrentUsername(Authentication authentication) {
        // Get the currently logged-in user's username from the authentication object
        String username = authentication.getName(); // Extracts the username from the SecurityContext

        return ResponseEntity.ok(username);
    }
}
