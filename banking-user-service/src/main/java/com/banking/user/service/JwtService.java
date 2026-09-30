package com.banking.user.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService 
{
	private final SecretKey secretKey;
	private final long expirationTime;
	
	public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expirationTime)
	{
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationTime = expirationTime;
	}
	
	public String generateToken(String username, String role)
	{
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expirationTime);
		
		return Jwts.builder()
				.subject(username)
				.claim("role", role)
				.issuedAt(now)
				.expiration(expiration)
				.signWith(secretKey)
				.compact();
	}

	public String extractUsername(String token)
	{
		return getClaims(token).getSubject();
	}
	
	public String extractRole(String token)
	{
		return getClaims(token).get("role", String.class);
	}
	
	public boolean isTokenValid(String token)
	{
		try
		{
			getClaims(token);
			return true;
			
		} catch (Exception e)
		{
			return false;
		}
	}
	
	private Claims getClaims(String token)
	{
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
