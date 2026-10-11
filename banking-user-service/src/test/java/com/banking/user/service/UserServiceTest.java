package com.banking.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.banking.user.dto.UserProfileResponse;
import com.banking.user.dto.UserRegistrationRequest;
import com.banking.user.dto.UserRegistrationResponse;
import com.banking.user.entity.User;
import com.banking.user.exception.UserAlreadyExistsException;
import com.banking.user.exception.UserNotFoundException;
import com.banking.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User createUser()
	{
		return User.builder()
				.userId(1L)
				.username("testuser")
				.email("test@gmail.com")
				.passwordHash("encoded-password")
				.firstName("Test")
				.lastName("User")
				.phoneNumber("9876543210")
				.role("CUSTOMER")
				.status("ACTIVE")
				.build();
	}
	
	private UserRegistrationRequest createRegistrationRequest()
	{
		UserRegistrationRequest request = new UserRegistrationRequest();
		request.setUsername("newuser");
		request.setEmail("newuser@gmail.com");
		request.setPassword("Password123!");
		request.setFirstName("New");
		request.setLastName("User");
		request.setPhoneNumber("1234567890");
		
		return request;
	}

	@Test
	void getUserProfile_WhenUserExists_ReturnProfile()
	{
		User user = createUser();

		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		UserProfileResponse response = userService.getUserProfile("testuser");

		assertEquals(1L, response.getUserId());
		assertEquals("testuser", response.getUsername());
		assertEquals("test@gmail.com", response.getEmail());
		assertEquals("Test", response.getFirstName());
		assertEquals("User", response.getLastName());
		assertEquals("9876543210", response.getPhoneNumber());
		assertEquals("CUSTOMER", response.getRole());
		assertEquals("ACTIVE", response.getStatus());

		verify(userRepository).findByUsername("testuser");
	}

	@Test
	void getUserProfile_WhenUserDoesNotExist_ThrowsException()
	{
		when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, ()-> userService.getUserProfile("unknown"));

		verify(userRepository).findByUsername("unknown");
	}

	@Test
	void getUserById_WhenUserExists_ReturnsProfile()
	{
		User user = createUser();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		UserProfileResponse response = userService.getUserById(1l);

		assertEquals(1L, response.getUserId());
		assertEquals("testuser", response.getUsername());
		assertEquals("test@gmail.com", response.getEmail());
		assertEquals("CUSTOMER", response.getRole());
		assertEquals("ACTIVE", response.getStatus());

		verify(userRepository).findById(1L);
	}

	@Test
	void getUserById_WhenUserDoesNotExist_ThrowsException()
	{
		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, ()-> userService.getUserById(99L));

		verify(userRepository).findById(99L);
	}
	
	@Test
	void registerUser_WhenValidRequest_ReturnsRegistrationResponse()
	{
		UserRegistrationRequest request = createRegistrationRequest();

		User savedUser = User.builder()
				.userId(2L)
				.username("newuser")
				.email("newuser@gmail.com")
				.passwordHash("encoded-password")
				.firstName("New")
				.lastName("User")
				.phoneNumber("1234567890")
				.role("CUSTOMER")
				.status("ACTIVE")
				.build();
		
		when(userRepository.existsByUsername("newuser")).thenReturn(false);
		when(userRepository.existsByEmail("newuser@gmail.com")).thenReturn(false);
		when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		
		UserRegistrationResponse response = userService.registerUser(request);
		
		assertEquals(2L, response.getUserId());
		assertEquals("newuser", response.getUsername());
		assertEquals("newuser@gmail.com", response.getEmail());
		assertEquals("New", response.getFirstName());
		assertEquals("User", response.getLastName());
		assertEquals("CUSTOMER", response.getRole());
		assertEquals("ACTIVE", response.getStatus());
		
		verify(passwordEncoder).encode("Password123!");
		verify(userRepository).save(any(User.class));
		
	}
	
	@Test
	void registerUser_WhenUsernameAlreadyExists_ThrowsException()
	{
		UserRegistrationRequest request = createRegistrationRequest();
		
		when(userRepository.existsByUsername("newuser")).thenReturn(true);
		
		assertThrows(UserAlreadyExistsException.class, ()->userService.registerUser(request));
		
		verify(userRepository, never()).save(any(User.class));
		verify(userRepository, never()).existsByEmail(any(String.class));
		
	}
	
	@Test
	void registerUser_WhenEmailAlreadyExists_ThrowsException()
	{
		UserRegistrationRequest request = createRegistrationRequest();
		when(userRepository.existsByUsername("newuser")).thenReturn(false);
		when(userRepository.existsByEmail("newuser@gmail.com")).thenReturn(true);
		
		assertThrows(UserAlreadyExistsException.class, ()-> userService.registerUser(request));
		verify(userRepository, never()).save(any(User.class));
		verify(passwordEncoder, never()).encode(any(String.class));
		
	}
	
	@Test
	void registerUser_EncodesPasswordBeforeSavingUser()
	{
		UserRegistrationRequest request = createRegistrationRequest();

		User savedUser = User.builder()
				.userId(2L)
				.username("newuser")
				.email("newuser@gmail.com")
				.passwordHash("encoded-password")
				.firstName("New")
				.lastName("User")
				.phoneNumber("1234567890")
				.role("CUSTOMER")
				.status("ACTIVE")
				.build();
		
		when(userRepository.existsByUsername("newuser")).thenReturn(false);
		when(userRepository.existsByEmail("newuser@gmail.com")).thenReturn(false);
		when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		
		userService.registerUser(request);
		
		verify(passwordEncoder).encode("Password123!");
	}
}

