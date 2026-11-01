package com.banking.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.banking.user.config.SecurityConfig;
import com.banking.user.dto.UserProfileResponse;
import com.banking.user.dto.UserRegistrationResponse;
import com.banking.user.security.JwtAuthenticationFilter;
import com.banking.user.service.JwtService;
import com.banking.user.service.UserService;

@WebMvcTest(controllers = UserController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest
{
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtAuthenticationFilter authenticationFilter;

	@MockitoBean
	private JwtService jwtService;

	@Test
	void registerUser_WhenValidRequest_ReturnsCreated() throws Exception
	{
		UserRegistrationResponse response = UserRegistrationResponse.builder().userId(1L).username("testuser")
				.email("testuser@gmail.com").firstName("Test").lastName("User").phoneNumber("9876543210")
				.role("CUSTOMER").status("ACTIVE").build();

		when(userService.registerUser(any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON).content("""
				{
					"username": "testuser",
					"email":"testuser@gmail.com",
					"password":"Password123!",
					"firstName":"Test",
					"lastName":"User",
					"phoneNumber":"9876543210"
				}
				""")).andExpect(status().isCreated()).andExpect(jsonPath("$.userId").value(1))
				.andExpect(jsonPath("$.username").value("testuser")).andExpect(jsonPath("$.role").value("CUSTOMER"));
	}

	@Test
	void registerUser_WhenUsernameIsMissing_ReturnsBadRequest() throws Exception
	{
		mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON).content("""
				 {
				     "email": "test@gmail.com",
				     "password": "Password123!",
				     "firstName": "Test",
				     "lastName": "User"
				   }

				""")).andExpect(status().isBadRequest());
	}
	

	@Test
	void getUserById_WhenAdmin_ReturnsUser() throws Exception
	{
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

		when(userService.getUserById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/v1/users/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.userId").value(1))
		.andExpect(jsonPath("$.username").value("testuser"));	
	}
}
