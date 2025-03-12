package com.tus.proj.user_managment;

import java.util.List;
import java.util.Optional;

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
import com.tus.proj.service.UserService;




@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final JwtService jwtService;

	public UserController(UserService userService, JwtService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
	}


	@PostMapping("/register")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
		// Convert the DTO to a User entity
		User user = new User();
		if (request.getUsername().length() >= 3) {
			user.setUsername(request.getUsername());
		} else {
			return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
		}

		if (isValidPassword(request.getPassword())) {
			user.setPassword(request.getPassword());
		} else {
			return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
		}
		user.setRole(request.getRole());

		User createdUser = userService.createUser(user);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
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

	// New endpoint to get all users
	@PreAuthorize("hasRole('Admin')")
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('Admin')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable int id) {
		boolean deleted = userService.deleteUser(id);
		if (deleted) {
			return ResponseEntity.noContent().build(); // 204 No Content on success
		} else {
			return ResponseEntity.notFound().build(); // 404 if user not found
		}
	}


	@PostMapping("/login")  
    public ResponseEntity<?> loginValidation(@RequestBody LoginRequest request) {
    	try {
        	User user = userService.authenticate(request.getUsername(), request.getPassword());
        	String jwt = jwtService.generateToken(user.getUsername(), user.getRole());
        	LoginResponse loginResponse = new LoginResponse();
        	loginResponse.setJwt(jwt);
            return ResponseEntity.ok(loginResponse); // 200 OK with true indicating success
        } catch (BadCredentialsException e) {
        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // 401 Unauthorized with false indicating failure
    }

    }
	
	@PreAuthorize("hasRole('Admin')")
	@PutMapping("/edit/{id}")
	public ResponseEntity<User> editUser(@PathVariable int id, @RequestBody EditUserRequest request) {
	    Optional<User> existingUser = userService.getUserById(id);
	    
	    if (!existingUser.isPresent()) { // Fixing the null check
	        return ResponseEntity.notFound().build();
	    }

	    // Convert role string to enum if necessary
	    UserRole role = null;
	    if (request.getRole() != null) {
	        try {
	            role = UserRole.valueOf(request.getRole().toUpperCase()); // Ensuring case sensitivity match
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.badRequest().build(); // Invalid role value
	        }
	    }

	    Optional<User> updatedUser = userService.editUser(id, request.getUsername(), request.getPassword(), role);

	    return updatedUser.map(ResponseEntity::ok)
	                      .orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@PreAuthorize("hasRole('Admin')")
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable int id) {
	    Optional<User> user = userService.getUserById(id);
	    
	    return user.map(ResponseEntity::ok)
	               .orElseGet(() -> ResponseEntity.notFound().build());
	}


}
