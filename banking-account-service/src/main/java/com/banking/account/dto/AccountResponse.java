package com.banking.account.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.banking.account.entity.AccountStatus;
import com.banking.account.entity.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponse
{
	private Long id;
	private String accountNumber;
	private Long customerId;
	private AccountType accountType;
	private BigDecimal balance;
	private String currency;
	private AccountStatus status;
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
}
