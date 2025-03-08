package com.tus.proj.service;


import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import com.tus.proj.user_managment.UserRole;

import io.jsonwebtoken.Claims;

public interface IJwtService {
	
	String generateToken(String username, UserRole role);

	Claims extractAllClaims(String token);

	String extractUsername(String token);

	<T> T extractClaim(String token, Function<Claims, T> claimsResolver);

	boolean isTokenValid(String token, UserDetails userDetails);
}

