package com.banking.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banking.user.dto.UserProfileResponse;
import com.banking.user.dto.UserRegistrationRequest;
import com.banking.user.dto.UserRegistrationResponse;
import com.banking.user.entity.User;
import com.banking.user.exception.UserAlreadyExistsException;
import com.banking.user.exception.UserNotFoundException;
import com.banking.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService 
{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	public UserRegistrationResponse registerUser(UserRegistrationRequest request)
	{
		if(userRepository.existsByUsername(request.getUsername()))
		{
			throw new UserAlreadyExistsException("Username already exists");
		}

		if(userRepository.existsByEmail(request.getEmail()))
		{
			throw new UserAlreadyExistsException("Email already exists");
		}

		User user = User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.phoneNumber(request.getPhoneNumber())
				.role("CUSTOMER")
				.status("ACTIVE")
				.build();

		User savedUser = userRepository.save(user);

		return UserRegistrationResponse.builder()
				.userId(savedUser.getUserId())
				.username(savedUser.getUsername())
				.email(savedUser.getEmail())
				.firstName(savedUser.getFirstName())
				.lastName(savedUser.getLastName())
				.phoneNumber(savedUser.getPhoneNumber())
				.role(savedUser.getRole())
				.status(savedUser.getStatus())
				.build();
	}
	
	public UserProfileResponse getUserProfile(String username)
	{
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
		
		return 	UserProfileResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.email(user.getEmail())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.phoneNumber(user.getPhoneNumber())
				.role(user.getRole())
				.status(user.getStatus())
				.build();
	}
	
	public UserProfileResponse getUserById(Long usedId)
	{
		User user = userRepository.findById(usedId).orElseThrow(() -> new UserNotFoundException("User not found"));

		return UserProfileResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.phoneNumber(user.getPhoneNumber())
				.role(user.getRole())
				.status(user.getStatus())
				.build();
	}
}
