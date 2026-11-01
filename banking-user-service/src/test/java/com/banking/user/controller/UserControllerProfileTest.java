
package com.banking.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.banking.user.dto.UserProfileResponse;
import com.banking.user.service.UserService;

class UserControllerProfileTest 
{

	@Test
	void getProfile_WhenAuthenticated_ReturnsProfile() 
	{
		UserService userService = mock(UserService.class);
		UserController controller = new UserController(userService);
		Authentication authentication = mock(Authentication.class);

		when(authentication.getName()).thenReturn("testuser");

		UserProfileResponse response = UserProfileResponse.builder()
				.userId(1L)
				.username("testuser")
				.email("test@gmail.com")
				.firstName("Test")
				.lastName("User")
				.phoneNumber("9876543210")
				.role("CUSTOMER")
				.status("ACTIVE")
				.build();

		when(userService.getUserProfile("testuser"))
		.thenReturn(response);

		ResponseEntity<UserProfileResponse> result = controller.getProfile(authentication);

		assertEquals(200, result.getStatusCode().value());
		assertEquals("testuser", result.getBody().getUsername());
		assertEquals("test@gmail.com", result.getBody().getEmail());

		verify(userService).getUserProfile("testuser");
	}
}
