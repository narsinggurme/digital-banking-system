package com.banking.user.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.banking.user.config.SecurityConfig;
import com.banking.user.dto.UserProfileResponse;
import com.banking.user.security.JwtAuthenticationFilter;
import com.banking.user.service.JwtService;
import com.banking.user.service.UserService;

@WebMvcTest(UserController.class)
@Import(
{ SecurityConfig.class, JwtAuthenticationFilter.class })
class UserControllerProfileSecurityTest
{
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtService jwtService;

	@Test
	void getProfile_withoutToken_returns401() throws Exception
	{
		mockMvc.perform(get("/api/v1/users/profile")).andExpect(status().isUnauthorized());
	}

	@Test
	void getProfile_withInvalidToken_returns401() throws Exception
	{
		when(jwtService.isTokenValid("bad-token")).thenReturn(false);

		mockMvc.perform(get("/api/v1/users/profile").header("Authorization", "Bearer bad-token"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void getProfile_withUserRole_returns200() throws Exception
	{
		stubToken("user-token", "john", "USER");

		mockMvc.perform(get("/api/v1/users/profile").header("Authorization", "Bearer user-token"))
				.andExpect(status().isOk());
	}

	@Test
	void getUserById_withUserRole_returns403() throws Exception
	{
		stubToken("user-token", "john", "USER");

		mockMvc.perform(get("/api/v1/users/1").header("Authorization", "Bearer user-token"))
				.andExpect(status().isForbidden());
	}

	@Test
	void getUserById_withAdminRole_returns200() throws Exception
	{
		stubToken("admin-token", "admin", "ADMIN");

		mockMvc.perform(get("/api/v1/users/1").header("Authorization", "Bearer admin-token"))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	void adminEndpoint_WhenAdmin_ReturnsOk() throws Exception
	{
		mockMvc.perform(get("/api/v1/users/admin"))
				.andExpect(status().isOk())
				.andExpect(content().string("Only ADMIN can access this end point"));
	}

	@Test
	@WithMockUser(roles = "CUSTOMER")
	void adminEndpoint_WhenCustomer_ReturnsForbidden() throws Exception
	{
		mockMvc.perform(get("/api/v1/users/admin"))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminEndpoint_WhenUnauthenticated_ReturnsUnauthorized() throws Exception
	{
		mockMvc.perform(get("/api/v1/users/admin"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = "CUSTOMER")
	void getUserById_WhenCustomer_ReturnsForbidden() throws Exception
	{
		mockMvc.perform(get("/api/v1/users/1"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getUserById_WhenAdmin_ReturnsUser() throws Exception
	{
		UserProfileResponse response = UserProfileResponse.builder()
				.userId(1L).username("testuser").email("test@gmail.com")
				.firstName("Test").lastName("User").phoneNumber("9876543210")
				.role("CUSTOMER").status("ACTIVE").build();

		when(userService.getUserById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/v1/users/1"))
				.andExpect(status().isOk());
	}

	private void stubToken(String token, String username, String role)
	{
		when(jwtService.isTokenValid(token)).thenReturn(true);
		when(jwtService.extractUsername(token)).thenReturn(username);
		when(jwtService.extractRole(token)).thenReturn(role);
	}
}