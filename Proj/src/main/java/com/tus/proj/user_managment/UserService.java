package com.tus.proj.user_managment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

}

