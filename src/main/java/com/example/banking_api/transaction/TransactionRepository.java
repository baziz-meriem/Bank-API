package com.example.banking_api.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.account_id = :accountId OR t.targetAccount.account_id = :accountId")
    List<Transaction> findAllByAccountId(@Param("accountId") Long accountId);
}
