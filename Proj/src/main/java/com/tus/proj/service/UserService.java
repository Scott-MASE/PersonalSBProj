package com.tus.proj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;  // Inject JwtUtil

    @Autowired
    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;  // Initialize JwtUtil
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public User saveUser(User user) {
		userRepository.save(user);
		return user;
	}
	
	public boolean deleteUser(int id) {
	    if (userRepository.existsById(id)) { // Check if user exists
	        userRepository.deleteById(id);
	        return true; // Successfully deleted
	    }
	    return false; // User not found
	}
	
	public Optional<User> authenticate(String userName, String password) {
	    Optional<User> userOptional = userRepository.findByUsername(userName);

	    if (userOptional.isPresent()) {
	        User user = userOptional.get();

	        if (user.getPassword().equals(password)) {
	            return Optional.of(user); 
	        }
	    }

	    return Optional.empty();  
	}
	
    public String generateJwtToken(User user) {
        return jwtService.generateToken(user.getUsername(), user.getRole());
    }

}

