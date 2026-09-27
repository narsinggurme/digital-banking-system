package com.banking.user;

import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator
{
	public static void main(String[] args)
	{
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String passowrd = "Admin@123";
		@Nullable
		String hash = encoder.encode(passowrd);
		
		System.out.println("Password: "+passowrd);
		System.out.println("Bcrypt Hash: "+ hash);
	}
}
