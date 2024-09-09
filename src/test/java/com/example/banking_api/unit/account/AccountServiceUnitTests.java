package com.example.banking_api.unit.account;

import com.example.banking_api.account.*;
import com.example.banking_api.customer.Customer;
import com.example.banking_api.customer.CustomerRepository;
import com.example.banking_api.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceUnitTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount_Success() {
        // Arrange
        Long customerId = 1L;
        AccountDto accountDto = new AccountDto();
        accountDto.setCustomer_id(customerId);
        accountDto.setBranch_code("001");
        accountDto.setBalance(1000.0);
        accountDto.setAccount_type("SAVINGS");

        Customer customer = new Customer();
        customer.setCustomer_id(customerId);

        Account account = new Account();
        account.setCustomer(customer);
        account.setBranch_code("001");
        account.setBalance(1000.0);
        account.setAccount_type(AccountType.SAVINGS);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        Account createdAccount = accountService.createAccount(accountDto);

        // Assert
        assertNotNull(createdAccount);
        assertEquals(accountDto.getBranch_code(), createdAccount.getBranch_code());
        assertEquals(accountDto.getBalance(), createdAccount.getBalance());
        assertEquals(AccountType.SAVINGS, createdAccount.getAccount_type());
        assertEquals(customer, createdAccount.getCustomer());
        verify(customerRepository).findById(customerId);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testCreateAccount_CustomerNotFound() {
        // Arrange
        Long customerId = 1L;
        AccountDto accountDto = new AccountDto();
        accountDto.setCustomer_id(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                accountService.createAccount(accountDto)
        );
        assertTrue(exception.getMessage().contains("Customer with ID " + customerId + " not found"));

        verify(customerRepository).findById(customerId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testGetBalance_Success() {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setAccount_id(accountId);
        account.setBalance(1500.0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act
        Double balance = accountService.getBalance(accountId);

        // Assert
        assertNotNull(balance);
        assertEquals(1500.0, balance);
        verify(accountRepository).findById(accountId);
    }

    @Test
    void testGetBalance_AccountNotFound() {
        // Arrange
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                accountService.getBalance(accountId)
        );
        assertEquals("Account not found", exception.getMessage());

        verify(accountRepository).findById(accountId);
    }
}
