package com.banking.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest 
{
	@NotBlank(message = "Username is required")
	@Size(min = 4, max = 50, message = "Username nust be between 4 and 50 characters")
	private String username;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email")
	private String email;
	
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
	private String password;
	
	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name cannot exceeds 50 characters")
	private String firstName;
	
	@NotBlank(message = "Last name is required")
	@Size(max = 50, message = "Last name cannot exceeds 50 characters")
	private String lastName;
	
	@Size(max = 15, message = "Phone number cannot exceed 15 characters")
	private String phoneNumber;
}
