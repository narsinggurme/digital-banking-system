package com.banking.account.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.account.dto.AccountCreateRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.AccountStatusUpdateRequest;
import com.banking.account.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController
{
	private final AccountService accountService;

	@PostMapping
	public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreateRequest request)
	{
		AccountResponse response = accountService.createAccount(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {

	    AccountResponse response = accountService.getAccountById(id);

	    return ResponseEntity.ok(response);
	}

	@GetMapping("/test-error")
	public ResponseEntity<AccountResponse> testError()
	{
		throw new RuntimeException("Test unexpected error.");
	}

	@GetMapping
	public ResponseEntity<List<AccountResponse>> getAllAccounts()
	{
		List<AccountResponse> allAccounts = accountService.getAllAccounts();

		return ResponseEntity.ok(allAccounts);
	}

	@GetMapping("/number/{accountNumber}")
	public ResponseEntity<AccountResponse> getAccountByAccountNumber(@PathVariable String accountNumber)
	{
		AccountResponse accountNumber2 = accountService.getAccountByAccountNumber(accountNumber);

		return ResponseEntity.ok(accountNumber2);
	}
	
	@PatchMapping("/{id}/status")
	public ResponseEntity<AccountResponse> updateAccountStatus(@PathVariable Long id, @Valid @RequestBody AccountStatusUpdateRequest request)
	{
		AccountResponse response = accountService.updateAccountStatus(id, request);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<AccountResponse>> getAccountsByCustomerId(@PathVariable Long customerId)
	{
		 List<AccountResponse> accountsByCustomerId = accountService.getAccountsByCustomerId(customerId);
		 
		 return ResponseEntity.ok(accountsByCustomerId);
	}
	
}
