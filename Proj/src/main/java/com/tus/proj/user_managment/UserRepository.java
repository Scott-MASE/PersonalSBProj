package com.tus.proj.user_managment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // Find a user by username (useful for authentication)
    Optional<User> findByUsername(String username);

    // Check if a username already exists
    boolean existsByUsername(String username);
}
