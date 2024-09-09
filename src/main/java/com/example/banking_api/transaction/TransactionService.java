package com.example.banking_api.transaction;

import com.example.banking_api.account.Account;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Long sourceAccountId, Long targetAccountId, Double amount);
    List<Transaction> getTransferHistory(Long accountId);
    List<Transaction> getAllTransactions();
    }