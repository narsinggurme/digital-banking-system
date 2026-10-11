package com.banking.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.banking.user.dto.LoginRequest;
import com.banking.user.dto.LoginResponse;
import com.banking.user.entity.User;
import com.banking.user.exception.InvalidCredentialsException;
import com.banking.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest
{
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private JwtService jwtService;
	
	@InjectMocks
	private AuthService authService;
	
	private LoginRequest createLoginRequest()
	{
		LoginRequest request = new LoginRequest();
		request.setUsername("testuser");
		request.setPassword("Password123!");

		return request;
	}
	
	private User createUser()
	{
		return User.builder()
				.userId(2L)
				.username("testuser")
				.email("testuser@gmail.com")
				.passwordHash("encoded-password")
				.firstName("Test")
				.lastName("User")
				.phoneNumber("9876543210")
				.role("CUSTOMER")
				.status("ACTIVE")
				.build();
	}
	
	@Test
	void login_WhenCredentialsAreValid_ReturnsLoginResponse()
	{
		LoginRequest request = createLoginRequest();
		User user = createUser();
		
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);
		when(jwtService.generateToken("testuser", "CUSTOMER")).thenReturn("mock-jwt-token");
		
		LoginResponse response = authService.login(request);
		
		assertEquals("mock-jwt-token", response.getAccessToken());
		assertEquals("Bearer", response.getTokenType());
		assertEquals(2L, response.getUserId());
		assertEquals("testuser", response.getUsername());
		assertEquals("CUSTOMER", response.getRole());
		
		verify(passwordEncoder).matches("Password123!", "encoded-password");
		verify(jwtService).generateToken("testuser", "CUSTOMER");
	}
	
	@Test
	void login_WhenUsernameDoesNotExist_ThrowsException()
	{
		LoginRequest request = createLoginRequest();
		
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
		
		assertThrows(InvalidCredentialsException.class, ()-> authService.login(request));
	}
	
	@Test
	void login_WhenPasswordIsIncorrect_ThrowsException()
	{
		LoginRequest request = createLoginRequest();
		User user = createUser();
		
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(false);
		
		assertThrows(InvalidCredentialsException.class, ()-> authService.login(request));
	}

}
