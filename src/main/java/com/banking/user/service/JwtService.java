package com.banking.user.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService 
{
	private final SecretKey secretKey = Keys.hmacShaKeyFor( "my-super-secret-key-for-banking-service-2026".getBytes());
	private final long expirationTime = 1000*60*60; //1 hour
	
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
