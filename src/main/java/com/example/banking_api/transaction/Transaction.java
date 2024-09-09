package com.example.banking_api.transaction;

import com.example.banking_api.account.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@Setter
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaction_id;

    @ManyToOne
    @JoinColumn(name = "source_account_id", updatable = false)
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "target_account_id", updatable = false)
    private Account targetAccount;

    @NotNull(message = "Amount cannot be null")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction type cannot be null")
    private TransactionType type;


    public Transaction(Account sourceAccount, Account targetAccount, Double amount,TransactionType transactionType) {
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.type = transactionType;
    }

}
