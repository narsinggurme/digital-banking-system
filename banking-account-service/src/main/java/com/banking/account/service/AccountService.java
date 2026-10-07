package com.banking.account.service;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.banking.account.dto.AccountCreateRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService
{
	private final AccountRepository accountRepository;

	public AccountResponse createAccount(AccountCreateRequest request)
	{
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
	private String generateAccountNumber()
	{
		return String.valueOf(ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L));
	}

}
