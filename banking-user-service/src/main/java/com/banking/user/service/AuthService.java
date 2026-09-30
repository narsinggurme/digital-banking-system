package com.banking.user.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banking.user.dto.LoginRequest;
import com.banking.user.dto.LoginResponse;
import com.banking.user.entity.User;
import com.banking.user.exception.InvalidCredentialsException;
import com.banking.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService 
{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	
	public LoginResponse login(LoginRequest request)
	{
		User user = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> new InvalidCredentialsException("Invalid username or password"));
		
		if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
		{
			throw new InvalidCredentialsException("Invalid username or password");
		}
		
		String token = jwtService.generateToken(user.getUsername(), user.getRole());
		
		return LoginResponse.builder()
				.accessToken(token)
				.tokenType("Bearer")
				.userId(user.getUserId())
				.username(user.getUsername())
				.role(user.getRole())
				.build();
	}
}
