package com.banking.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banking.account.dto.AccountCreateRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.AccountStatusUpdateRequest;
import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.entity.AccountType;
import com.banking.account.repository.AccountRepository;
import com.banking.account.service.AccountService;
import com.banking.account.exception.AccountNotFoundException;
import com.banking.account.exception.DuplicateAccountException;
import com.banking.account.exception.InvalidAccountStatusTransitionException;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest
{

	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private AccountService accountService;

	private Account account;

	@BeforeEach
	void setUp()
	{
		account = Account.builder().id(1L).accountNumber("123456789012").customerId(101L)
				.accountType(AccountType.SAVINGS).balance(new BigDecimal("5000.00")).currency("INR")
				.status(AccountStatus.ACTIVE).build();
	}

	// --------------------------------------------------
	// CREATE ACCOUNT TESTS
	// --------------------------------------------------

	@Test
	void createAccount_WhenValidRequest_ReturnsAccountResponse()
	{

		AccountCreateRequest request = new AccountCreateRequest();
		request.setCustomerId(101L);
		request.setAccountType(AccountType.SAVINGS);
		request.setInitialDeposit(new BigDecimal("5000.00"));
		request.setCurrency("inr");

		when(accountRepository.existsByCustomerIdAndAccountType(101L, AccountType.SAVINGS)).thenReturn(false);

		when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

		when(accountRepository.save(any(Account.class))).thenReturn(account);

		AccountResponse response = accountService.createAccount(request);

		assertNotNull(response);
		assertEquals(1L, response.getId());
		assertEquals("123456789012", response.getAccountNumber());
		assertEquals(101L, response.getCustomerId());
		assertEquals(AccountType.SAVINGS, response.getAccountType());
		assertEquals(new BigDecimal("5000.00"), response.getBalance());
		assertEquals("INR", response.getCurrency());
		assertEquals(AccountStatus.ACTIVE, response.getStatus());

		verify(accountRepository).save(any(Account.class));
	}

	@Test
	void createAccount_WhenAccountTypeAlreadyExists_ThrowsDuplicateAccountException()
	{

		AccountCreateRequest request = new AccountCreateRequest();
		request.setCustomerId(101L);
		request.setAccountType(AccountType.SAVINGS);

		when(accountRepository.existsByCustomerIdAndAccountType(101L, AccountType.SAVINGS)).thenReturn(true);

		assertThrows(DuplicateAccountException.class, () -> accountService.createAccount(request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	void createAccount_WhenGeneratedAccountNumberAlreadyExists_RetriesGeneration()
	{

		AccountCreateRequest request = new AccountCreateRequest();
		request.setCustomerId(101L);
		request.setAccountType(AccountType.SAVINGS);
		request.setInitialDeposit(new BigDecimal("5000.00"));
		request.setCurrency("INR");

		when(accountRepository.existsByCustomerIdAndAccountType(101L, AccountType.SAVINGS)).thenReturn(false);

		when(accountRepository.existsByAccountNumber(anyString())).thenReturn(true, false);

		when(accountRepository.save(any(Account.class))).thenReturn(account);

		AccountResponse response = accountService.createAccount(request);

		assertNotNull(response);

		verify(accountRepository, times(2)).existsByAccountNumber(anyString());
		verify(accountRepository).save(any(Account.class));
	}

	// --------------------------------------------------
	// GET ACCOUNT BY ID TESTS
	// --------------------------------------------------

	@Test
	void getAccountById_WhenAccountExists_ReturnsAccountResponse()
	{

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

		AccountResponse response = accountService.getAccountById(1L);

		assertNotNull(response);
		assertEquals(1L, response.getId());
		assertEquals("123456789012", response.getAccountNumber());
		assertEquals(101L, response.getCustomerId());

		verify(accountRepository).findById(1L);
	}

	@Test
	void getAccountById_WhenAccountDoesNotExist_ThrowsException()
	{

		when(accountRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(1L));
	}

	// --------------------------------------------------
	// GET ALL ACCOUNTS TESTS
	// --------------------------------------------------

	@Test
	void getAllAccounts_WhenAccountsExist_ReturnsAccountList()
	{

		Account secondAccount = Account.builder().id(2L).accountNumber("987654321012").customerId(102L)
				.accountType(AccountType.CURRENT).balance(new BigDecimal("10000.00")).currency("INR")
				.status(AccountStatus.ACTIVE).build();

		when(accountRepository.findAll()).thenReturn(List.of(account, secondAccount));

		List<AccountResponse> responses = accountService.getAllAccounts();

		assertEquals(2, responses.size());
		assertEquals(1L, responses.get(0).getId());
		assertEquals(2L, responses.get(1).getId());

		verify(accountRepository).findAll();
	}

	@Test
	void getAllAccounts_WhenNoAccountsExist_ReturnsEmptyList()
	{

		when(accountRepository.findAll()).thenReturn(List.of());

		List<AccountResponse> responses = accountService.getAllAccounts();

		assertNotNull(responses);
		assertTrue(responses.isEmpty());
	}

	// --------------------------------------------------
	// GET ACCOUNT BY ACCOUNT NUMBER TESTS
	// --------------------------------------------------

	@Test
	void getAccountByAccountNumber_WhenAccountExists_ReturnsAccountResponse()
	{

		when(accountRepository.findByAccountNumber("123456789012")).thenReturn(Optional.of(account));

		AccountResponse response = accountService.getAccountByAccountNumber("123456789012");

		assertNotNull(response);
		assertEquals("123456789012", response.getAccountNumber());

		verify(accountRepository).findByAccountNumber("123456789012");
	}

	@Test
	void getAccountByAccountNumber_WhenAccountDoesNotExist_ThrowsException()
	{

		when(accountRepository.findByAccountNumber("123456789012")).thenReturn(Optional.empty());

		assertThrows(AccountNotFoundException.class, () -> accountService.getAccountByAccountNumber("123456789012"));
	}

	// --------------------------------------------------
	// UPDATE ACCOUNT STATUS TESTS
	// --------------------------------------------------

	@Test
	void updateAccountStatus_WhenActiveToBlocked_ReturnsUpdatedAccount()
	{

		AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
		request.setStatus(AccountStatus.BLOCKED);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

		when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

		AccountResponse response = accountService.updateAccountStatus(1L, request);

		assertEquals(AccountStatus.BLOCKED, response.getStatus());

		verify(accountRepository).save(account);
	}

	@Test
	void updateAccountStatus_WhenActiveToClosed_ReturnsUpdatedAccount()
	{

		AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
		request.setStatus(AccountStatus.CLOSED);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

		when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

		AccountResponse response = accountService.updateAccountStatus(1L, request);

		assertEquals(AccountStatus.CLOSED, response.getStatus());
	}

	@Test
	void updateAccountStatus_WhenBlockedToActive_ReturnsUpdatedAccount()
	{

		account.setStatus(AccountStatus.BLOCKED);

		AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
		request.setStatus(AccountStatus.ACTIVE);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

		when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

		AccountResponse response = accountService.updateAccountStatus(1L, request);

		assertEquals(AccountStatus.ACTIVE, response.getStatus());
	}

	@Test
	void updateAccountStatus_WhenBlockedToClosed_ReturnsUpdatedAccount()
	{

		account.setStatus(AccountStatus.BLOCKED);

		AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
		request.setStatus(AccountStatus.CLOSED);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

		when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

		AccountResponse response = accountService.updateAccountStatus(1L, request);

		assertEquals(AccountStatus.CLOSED, response.getStatus());
	}

	@Test
	void updateAccountStatus_WhenActiveToActive_ThrowsException()
	{

		AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
		request.setStatus(AccountStatus.ACTIVE);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

		assertThrows(InvalidAccountStatusTransitionException.class,
				() -> accountService.updateAccountStatus(1L, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	void updateAccountStatus_WhenClosedToActive_ThrowsException()
	{

		account.setStatus(AccountStatus.CLOSED);

		AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
		request.setStatus(AccountStatus.ACTIVE);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

		assertThrows(InvalidAccountStatusTransitionException.class,
				() -> accountService.updateAccountStatus(1L, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	void updateAccountStatus_WhenAccountDoesNotExist_ThrowsException()
	{

		AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
		request.setStatus(AccountStatus.BLOCKED);

		when(accountRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AccountNotFoundException.class, () -> accountService.updateAccountStatus(1L, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	// --------------------------------------------------
	// GET ACCOUNTS BY CUSTOMER ID TESTS
	// --------------------------------------------------

	@Test
	void getAccountsByCustomerId_WhenAccountsExist_ReturnsAccountList()
	{

		when(accountRepository.findByCustomerId(101L)).thenReturn(List.of(account));

		List<AccountResponse> responses = accountService.getAccountsByCustomerId(101L);

		assertEquals(1, responses.size());
		assertEquals(101L, responses.get(0).getCustomerId());

		verify(accountRepository).findByCustomerId(101L);
	}

	@Test
	void getAccountsByCustomerId_WhenNoAccountsExist_ReturnsEmptyList()
	{

		when(accountRepository.findByCustomerId(101L)).thenReturn(List.of());

		List<AccountResponse> responses = accountService.getAccountsByCustomerId(101L);

		assertNotNull(responses);
		assertTrue(responses.isEmpty());
	}
}
