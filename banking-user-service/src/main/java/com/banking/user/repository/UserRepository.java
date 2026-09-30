package com.banking.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.user.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String userName);
	Optional<User> findByEmail(String email);
	
	boolean existsByUsername(String userName);
	
	boolean existsByEmail(String email);
}
