package com.example.banking_api.unit.transaction;

import com.example.banking_api.account.Account;
import com.example.banking_api.account.AccountRepository;
import com.example.banking_api.shared.exception.InsufficientFundsException;
import com.example.banking_api.transaction.Transaction;
import com.example.banking_api.transaction.TransactionRepository;
import com.example.banking_api.transaction.TransactionServiceImpl;
import com.example.banking_api.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceUnitTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    private Account sourceAccount;
    private Account targetAccount;

    @Test
    void testCreateTransaction_Success() {
        // Arrange
        Long sourceAccountId = 1L;
        Long targetAccountId = 2L;
        Double amount = 100.0;

        Account sourceAccount = new Account();
        sourceAccount.setAccount_id(sourceAccountId);
        sourceAccount.setBalance(500.0);

        Account targetAccount = new Account();
        targetAccount.setAccount_id(targetAccountId);
        targetAccount.setBalance(200.0);

        Transaction transaction = new Transaction(sourceAccount, targetAccount, amount, TransactionType.TRANSFER);
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = transactionService.createTransaction(sourceAccountId, targetAccountId, amount);
        this.targetAccount = targetAccount;
        this.sourceAccount = sourceAccount;
        // Assert
        assertNotNull(result);
        assertEquals(sourceAccountId, result.getSourceAccount().getAccount_id());
        assertEquals(targetAccountId, result.getTargetAccount().getAccount_id());
        assertEquals(amount, result.getAmount());
        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(targetAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_InsufficientFunds() {
        // Arrange
        Long sourceAccountId = 1L;
        Long targetAccountId = 2L;
        Double amount = 1000.0; // Amount greater than sourceAccount balance

        Account sourceAccount = new Account();
        sourceAccount.setAccount_id(sourceAccountId);
        sourceAccount.setBalance(500.0);

        Account targetAccount = new Account();
        targetAccount.setAccount_id(targetAccountId);
        targetAccount.setBalance(200.0);

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () ->
                transactionService.createTransaction(sourceAccountId, targetAccountId, amount)
        );

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_SourceAccountNotFound() {
        // Arrange
        Long sourceAccountId = 1L;
        Long targetAccountId = 2L;
        Double amount = 100.0;

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(sourceAccountId, targetAccountId, amount)
        );

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_TargetAccountNotFound() {
        // Arrange
        Long sourceAccountId = 1L;
        Long targetAccountId = 2L;
        Double amount = 100.0;

        Account sourceAccount = new Account();
        sourceAccount.setAccount_id(sourceAccountId);
        sourceAccount.setBalance(500.0);

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(sourceAccountId, targetAccountId, amount)
        );

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testGetTransferHistory() {
        List<Transaction> transactions = List.of(
                new Transaction(sourceAccount, targetAccount, 100.0, TransactionType.TRANSFER),
                new Transaction(targetAccount, sourceAccount, 50.0, TransactionType.TRANSFER)
        );

        when(transactionRepository.findAllByAccountId(1L)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransferHistory(1L);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(transactions, result);
    }
}
