package com.banking.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	

}
