package com.example.banking_api.account;


import jakarta.transaction.Transactional;

public interface AccountService{

    Account createAccount(AccountDto account);

    Double getBalance(Long accountId);
}
