package com.banking.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.account.entity.Account;
import com.banking.account.entity.AccountType;


public interface AccountRepository extends JpaRepository<Account, Long>
{
	Optional<Account> findByAccountNumber(String accountNumber);

	boolean existsByAccountNumber(String accountNumber);

	boolean existsByCustomerId(Long customerId);
	
	List<Account> findByCustomerId(Long customerId);
	
	boolean existsByCustomerIdAndAccountType(Long customerId, AccountType accountType);

}
