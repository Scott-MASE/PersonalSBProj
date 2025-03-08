package com.tus.proj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import com.tus.proj.filter.JWTAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	private JWTAuthenticationFilter jwtFilter;
	
	public SecurityConfig(JWTAuthenticationFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}


    // Configure the security filter chain.
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/h2-console/**").permitAll()  // Allow access to H2 console
	            .anyRequest().permitAll()  // Allow all other requests
	        )
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
	        .formLogin(form -> form.disable()) // Disable form login
	        .httpBasic(httpBasic -> httpBasic.disable()) // Disable HTTP Basic auth
	        .headers(headers -> headers
	            .frameOptions().disable() // Allow iframe embedding for H2 console
	        );
	        
	    return http.build();
	}


}
