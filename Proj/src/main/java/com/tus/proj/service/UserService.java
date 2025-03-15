package com.tus.proj.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRepository;
import com.tus.proj.user_managment.UserRole;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    
    
    public boolean deleteUser(Long id) {
    	
    	if (id == 1L) {
    		throw new IllegalStateException("Cannot delete the user with ID 1 (sys admin)");
        }
    	
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
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
		// TODO Auto-generated method stub
		return userRepository.findByUsername(username);
	}

}

