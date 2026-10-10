package com.banking.account.exception;

public class DuplicateAccountException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public DuplicateAccountException(String msg)
	{
		super(msg);
	}
}
