package com.banking.account.dto;

import java.math.BigDecimal;

import com.banking.account.entity.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreateRequest {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotNull(message = "Account Type is required")
    private AccountType accountType;

    @NotNull(message = "Initial deposit is required")
    @PositiveOrZero(message = "Initial deposit cannot be negative")
    private BigDecimal initialDeposit;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must contain exactly 3 characters")
    private String currency;
}