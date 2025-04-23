package com.tus.proj.user_managment;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tus.proj.service.UserService;




@Component
public class DataInitializer implements ApplicationRunner {

 private final UserRepository userRepository;
 private final UserService userService;
 private static final String USERNAME = "admin";
 private static final String PASSWORD = "admin";

 public DataInitializer(UserRepository userRepository, UserService userService) {
     this.userRepository = userRepository;
     this.userService = userService;
 }

 @Override
 public void run(ApplicationArguments args) {
     // Check if an admin user exists (using username "admin")
     if (userRepository.findByUsername(USERNAME).isEmpty()) {
         // Create a new admin user. The password will be encrypted by the UserService.
         User admin = new User(USERNAME, PASSWORD, UserRole.ADMIN);
         userService.createUser(admin);
         
         User user = new User("user", "user", UserRole.USER);
         userService.createUser(user);
         
         User user2 = new User("user2", "user", UserRole.USER);
         userService.createUser(user2);
         
         User user3 = new User("mod", "mod", UserRole.MODERATOR);
         userService.createUser(user3);
         
     }
 }
}
