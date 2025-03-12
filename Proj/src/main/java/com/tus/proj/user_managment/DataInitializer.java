package com.tus.proj.user_managment;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tus.proj.service.UserService;




@Component
public class DataInitializer implements ApplicationRunner {

 private final UserRepository userRepository;
 private final UserService userService;
 private final String username = "admin";
 private final String password = "admin";

 public DataInitializer(UserRepository userRepository, UserService userService) {
     this.userRepository = userRepository;
     this.userService = userService;
 }

 @Override
 public void run(ApplicationArguments args) throws Exception {
     // Check if an admin user exists (using username "admin")
     if (userRepository.findByUsername(username).isEmpty()) {
         // Create a new admin user. The password will be encrypted by the UserService.
         User admin = new User("admin", "admin", UserRole.ADMIN);
         userService.createUser(admin);
         
         User user = new User("James", "user", UserRole.USER);
         userService.createUser(user);
         
         User user2 = new User("Jim", "user", UserRole.USER);
         userService.createUser(user2);
         
     }
 }
}
