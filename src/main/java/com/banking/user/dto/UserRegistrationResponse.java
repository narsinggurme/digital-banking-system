package com.banking.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegistrationResponse 
{
	private Long userId;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String role;
	private String status;
}
