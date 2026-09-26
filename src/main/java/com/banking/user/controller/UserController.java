package com.banking.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.user.dto.UserRegistrationRequest;
import com.banking.user.dto.UserRegistrationResponse;
import com.banking.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController 
{
	
	private final UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<UserRegistrationResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request)
	{
		UserRegistrationResponse response = userService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/profile")
	public ResponseEntity<String> getProfile()
	{
		return ResponseEntity.ok("Authenticated user can access this endpoint");
	}
	
	@GetMapping("/admin")
	public ResponseEntity<String> adminOnly()
	{
		return ResponseEntity.ok("Only ADMIN can access this end point");
	}
	

}
