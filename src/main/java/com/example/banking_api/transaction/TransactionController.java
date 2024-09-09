package com.example.banking_api.transaction;

import com.example.banking_api.shared.response.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Object> createTransaction(@RequestBody @Valid TransactionRequestDTO transactionRequest) {
        Transaction createdTransaction = transactionService.createTransaction(
                transactionRequest.getSource_account_id(),
                transactionRequest.getTarget_account_id(),
                transactionRequest.getAmount()
        );
        return ResponseHandler.generateResponse("Transaction created successfully", HttpStatus.CREATED, createdTransaction);
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<Object> getTransferHistory(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransferHistory(accountId);
        return ResponseHandler.generateResponse("Transaction history fetched successfully", HttpStatus.OK, transactions);
    }

    @GetMapping
    public ResponseEntity<Object> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseHandler.generateResponse("All transactions fetched successfully", HttpStatus.OK, transactions);
    }

}