package com.tus.proj.user_managment;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private static final Logger logger = Logger.getLogger(UserController.class.getName());
	
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")  // ✅ Must match frontend AJAX request URL
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
    	
    	logger.info("triggered reg");
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
        user.setRole(UserRole.USER); // ✅ Ensure role is set

        User createdUser = userService.saveUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[*@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
