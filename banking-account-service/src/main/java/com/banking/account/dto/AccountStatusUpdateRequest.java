package com.banking.account.dto;

import com.banking.account.entity.AccountStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountStatusUpdateRequest 
{
	@NotNull(message = "Status is required")
	private AccountStatus status; 

}
