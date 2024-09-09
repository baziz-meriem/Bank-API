package com.example.banking_api.transaction;

import com.example.banking_api.account.Account;
import com.example.banking_api.account.AccountRepository;
import com.example.banking_api.shared.exception.InsufficientFundsException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }
    @Transactional
    public Transaction createTransaction(Long sourceAccountId, Long targetAccountId, Double amount) {
        // Validate input parameters
        if (sourceAccountId == null || targetAccountId == null) {
            throw new IllegalArgumentException("Source and target account ID cannot be null");
        }

        // Fetch accounts from the database
        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account targetAccount = accountRepository.findById(targetAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found"));

        // Check if the source account has sufficient funds
        if (sourceAccount.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds in source account");
        }

        // Update account balances
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        // Save the updated accounts
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        TransactionType transactionType = !sourceAccount.getAccount_id().equals(targetAccount.getAccount_id())
                                            ? TransactionType.TRANSFER
                                            : TransactionType.DEPOSIT;

        // Create and save the transaction record
        Transaction transaction = new Transaction(sourceAccount, targetAccount, amount, transactionType);
        return transactionRepository.save(transaction);
    }


    @Override
    public List<Transaction> getTransferHistory(Long accountId) {
        return transactionRepository.findAllByAccountId(accountId);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }


}
