package com.banking.account.exception;

public class InvalidAccountStatusTransitionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InvalidAccountStatusTransitionException(String msg)
	{
		super(msg);
	}

}
