package com.tus.proj.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tus.proj.user_managment.UserRole;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService implements IJwtService {
	private static final String SECRET_KEY = "4981f57ac11c2e710c94954e6e3620bc9930bc49404cf85294a7af28fed7ff4c"; // Use 																								// key

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes); // Securely generates an HMAC SHA-256 key
	}

	public String generateToken(String username, UserRole role) {
		return Jwts.builder().setSubject(username).claim("role", role.getDisplayName())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1-hour expiration
				.signWith(getSigningKey(), SignatureAlgorithm.HS256) // SHA-256 signing
				.compact();
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}
}