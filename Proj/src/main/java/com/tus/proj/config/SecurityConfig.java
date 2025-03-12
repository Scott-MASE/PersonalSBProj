package com.tus.proj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tus.proj.filter.JWTAuthenticationFilter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	private JWTAuthenticationFilter jwtFilter;


	public SecurityConfig(JWTAuthenticationFilter jwtFilter) {
	    this.jwtFilter = jwtFilter;
	}

    // Define a PasswordEncoder bean for your application.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure the security filter chain.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	return http
    	        .csrf(csrf -> csrf.disable())
    	        .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // Allow H2 Console frames
    	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    	        // Authorise requests configuration
    	        .authorizeHttpRequests(authorize -> authorize
    	            // Allow all requests to the H2 console
    	            .requestMatchers("/h2-console/**").permitAll()
    	            .requestMatchers("api/users/login").permitAll()
    	            .requestMatchers("api/users/register").permitAll()
    	            .requestMatchers("api/users/username/{username}").permitAll()

    	            .requestMatchers("/api/notes/getTags", "/api/notes/1/getAll").hasRole("User")  // Allow USER to access these endpoints

    	            .requestMatchers("/sonarqube/**").permitAll()  // Allow SonarQube
    	            .requestMatchers("/actuator/**").permitAll()   // Allow monitoring endpoints (optional)
    	            // Allow public access to specified endpoints and static resources
    	            .requestMatchers(
    	                "/",
    	                "/index.html", 
    	                "/main.js", 
    	                "/styles.css", 
    	                "/content/**", 
    	                "/assets/**",
    	                "/images/**",
    	                "/javascript/**"
    	                
    	            ).permitAll()
    	            .anyRequest().authenticated()
    	        ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
    	    }
}




