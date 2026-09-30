package com.banking.user.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "username", nullable = false, unique = true,  length = 50)
	private String username;
	
	@Column(nullable = false, unique = true, length = 100)
	private String email;
	
	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;
	
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;
	
	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;
	
	@Column(name = "phone_number", unique = true, length = 15)
	private String phoneNumber;
	
	@Column(nullable = false, length = 20)
	private String role;
	
	@Column(nullable = false, length = 20)
	private String status;
	
	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() 
	{
	    OffsetDateTime now = OffsetDateTime.now();
	    createdAt = now;
	    updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate()
	{
	    updatedAt = OffsetDateTime.now();
	}

}
