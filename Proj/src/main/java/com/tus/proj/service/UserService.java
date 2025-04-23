package com.tus.proj.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tus.proj.note_managment.NoteRepository;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRepository;
import com.tus.proj.user_managment.UserRole;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.noteRepository = noteRepository;
    }
    
    public User createUser(User user) {
        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional
    public boolean deleteUser(Long id) {
        
        // Prevent deletion of the system admin user (ID 1)
        if (id == 1L) {
            throw new IllegalStateException("Cannot delete the user with ID 1 (sys admin)");
        }

        // Check if the user exists
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            // Delete all notes associated with the user
            noteRepository.deleteByUserId(id);
            
            // Delete the user
            userRepository.deleteById(id);

            return true;
        }

        return false; // Return false if user is not found
    }
    
    public User authenticate(String userName, String rawPassword) throws BadCredentialsException {
        Optional<User> userOptional = userRepository.findByUsername(userName);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return user; 
            }
        }
        
        throw new BadCredentialsException("Invalid username or password");
    }
    

    public Optional<User> editUser(Long id, String username, String password, UserRole role) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (username != null && username.length() >= 3) {
                user.setUsername(username);
            }

            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password)); // Encrypt updated password
            }

            if (role != null) {
                user.setRole(role);
            }

            return Optional.of(userRepository.save(user)); // Save and return updated user
        }

        return Optional.empty(); // User not found
    }

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public boolean existsById(Long id) {
		return userRepository.existsById(id);
	}

}

