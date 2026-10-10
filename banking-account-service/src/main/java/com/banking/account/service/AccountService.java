package com.banking.account.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.banking.account.dto.AccountCreateRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.AccountStatusUpdateRequest;
import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.exception.AccountNotFoundException;
import com.banking.account.exception.DuplicateAccountException;
import com.banking.account.exception.InvalidAccountStatusTransitionException;
import com.banking.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService
{
	private final AccountRepository accountRepository;

	public AccountResponse createAccount(AccountCreateRequest request)
	{
		
		if (accountRepository.existsByCustomerIdAndAccountType(request.getCustomerId(), request.getAccountType()))
		{
			throw new DuplicateAccountException("Customer already has an account of type: " + request.getAccountType());
		}
		
		String accountNumber;
		do
		{
			accountNumber = generateAccountNumber();
		} while (accountRepository.existsByAccountNumber(accountNumber));

		Account account = Account.builder()
				.accountNumber(accountNumber)
				.customerId(request.getCustomerId())
				.accountType(request.getAccountType())
				.balance(request.getInitialDeposit())
				.currency(request.getCurrency().toUpperCase())
				.status(AccountStatus.ACTIVE)
				.build();

		Account savedAccount = accountRepository.save(account);

		return AccountResponse.builder()
				.id(savedAccount.getId())
				.accountNumber(savedAccount.getAccountNumber())
				.customerId(savedAccount.getCustomerId())
				.accountType(savedAccount.getAccountType())
				.balance(savedAccount.getBalance())
				.currency(savedAccount.getCurrency())
				.status(savedAccount.getStatus())
				.createdAt(savedAccount.getCreatedAt())
				.updatedAt(savedAccount.getUpdatedAt())
				.build();
	}

	public AccountResponse getAccountById(Long id)
	{
		Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
		return AccountResponse.builder()
				.id(account.getId())
				.accountNumber(account.getAccountNumber())
				.customerId(account.getCustomerId())
				.accountType(account.getAccountType())
				.balance(account.getBalance())
				.currency(account.getCurrency())
				.status(account.getStatus())
				.createdAt(account.getCreatedAt())
				.updatedAt(account.getUpdatedAt())
				.build();
	}

	public List<AccountResponse> getAllAccounts()
	{
		return accountRepository.findAll()
				.stream()
				.map(account -> AccountResponse.builder()
						.id(account.getId())
						.accountNumber(account.getAccountNumber())
						.customerId(account.getCustomerId())
						.accountType(account.getAccountType())
						.balance(account.getBalance())
						.currency(account.getCurrency())
						.status(account.getStatus())
						.createdAt(account.getCreatedAt())
						.updatedAt(account.getUpdatedAt())
						.build())
				.toList();
	}

	public AccountResponse getAccountByAccountNumber(String accountNumber)
	{
		Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new AccountNotFoundException("Account not found with this account number: "+ accountNumber));

		return AccountResponse.builder()
				.id(account.getId())
				.accountNumber(account.getAccountNumber())
				.customerId(account.getCustomerId())
				.accountType(account.getAccountType())
				.balance(account.getBalance())
				.currency(account.getCurrency())
				.status(account.getStatus())
				.createdAt(account.getCreatedAt())
				.updatedAt(account.getUpdatedAt())
				.build();
	}
	
	public AccountResponse updateAccountStatus(Long id, AccountStatusUpdateRequest request)
	{
		Account account = accountRepository.findById(id).orElseThrow(()-> new AccountNotFoundException("Account not found with this id: " + id));
		
		validateStatusTransition(account.getStatus(), request.getStatus());

		account.setStatus(request.getStatus());

		Account updatedAccount = accountRepository.save(account);

		return AccountResponse.builder()
				.id(updatedAccount.getId())
				.accountNumber(updatedAccount.getAccountNumber())
				.customerId(updatedAccount.getCustomerId())
				.accountType(updatedAccount.getAccountType())
				.balance(updatedAccount.getBalance())
				.currency(updatedAccount.getCurrency())
				.status(updatedAccount.getStatus())
				.createdAt(updatedAccount.getCreatedAt())
				.updatedAt(updatedAccount.getUpdatedAt())
				.build();
	}
	
	public List<AccountResponse> getAccountsByCustomerId(Long customerId)
	{
		return accountRepository.findByCustomerId(customerId)
				.stream()
				.map(account -> AccountResponse.builder()
						.id(account.getId())
						.accountNumber(account.getAccountNumber())
						.customerId(account.getCustomerId())
						.accountType(account.getAccountType())
						.balance(account.getBalance())
						.currency(account.getCurrency())
						.status(account.getStatus())
						.createdAt(account.getCreatedAt())
						.updatedAt(account.getUpdatedAt())
						.build()).toList();
	}
	
	private void validateStatusTransition(AccountStatus currentStatus, AccountStatus newStatus)
	{
		boolean isAllowed = switch (currentStatus)
		{
		case ACTIVE -> newStatus == AccountStatus.BLOCKED || newStatus == AccountStatus.CLOSED;
		case BLOCKED -> newStatus == AccountStatus.CLOSED || newStatus == AccountStatus.ACTIVE;
		case CLOSED -> false;
		};
		
		if(!isAllowed)
		{
			throw new InvalidAccountStatusTransitionException("Invalid account status transition from " +currentStatus + " to "+ newStatus);
		}
	}

	private String generateAccountNumber()
	{
		return String.valueOf(ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L));
	}

}
