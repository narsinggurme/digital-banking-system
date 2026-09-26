package com.banking.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.banking.user.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig 
{
	private final  JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
    {
    	http
    	.csrf(csrf -> csrf.disable())
    	.formLogin(form-> form.disable())

    	.exceptionHandling(exception ->  exception
    			.authenticationEntryPoint((request,response, authExceptiion) -> 
    			response.sendError(
    					HttpServletResponse.SC_UNAUTHORIZED))
    			)
    	
    	.authorizeHttpRequests(auth -> auth
    			.requestMatchers(HttpMethod.POST, "/api/v1/users/register", "/api/v1/auth/login").permitAll()
    			.requestMatchers("/error").permitAll()
    			.requestMatchers("/api/v1/users/admin").hasRole("ADMIN")
    			.anyRequest().authenticated()
    			)
    	.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    	return http.build();
    }
}