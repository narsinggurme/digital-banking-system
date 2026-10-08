package com.banking.account.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse
{
	private OffsetDateTime timestamp;
	private int status;
	private String error;
	private String message;
	private String path;

}
