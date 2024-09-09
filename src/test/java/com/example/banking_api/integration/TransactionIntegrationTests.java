package com.example.banking_api.integration;

import com.example.banking_api.account.Account;
import com.example.banking_api.account.AccountRepository;
import com.example.banking_api.account.AccountType;
import com.example.banking_api.customer.Customer;
import com.example.banking_api.customer.CustomerRepository;
import com.example.banking_api.transaction.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Account sourceAccount;
    private Account targetAccount;
    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = new Customer(
                "John Doe",
                "123 Elm St",
                "Springfield",
                "IL",
                "62704",
                "1234567891"
        );
        customer = customerRepository.save(customer);

        // Create accounts
        Account sourceAccount = new Account();
        sourceAccount.setBranch_code("12345");
        sourceAccount.setBalance(5000.00);
        sourceAccount.setAccount_type(AccountType.SAVINGS);
        sourceAccount.setCustomer(customer);
        sourceAccount = accountRepository.save(sourceAccount);

        Account targetAccount = new Account();
        targetAccount.setBranch_code("12345");
        targetAccount.setBalance(1000.00);
        targetAccount.setAccount_type(AccountType.CHECKING);
        targetAccount.setCustomer(customer);
        targetAccount = accountRepository.save(targetAccount);

        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;


        // Create a transaction
        Transaction transaction = new Transaction(
                sourceAccount,
                targetAccount,
                1000.00,
                TransactionType.TRANSFER
        );
        transactionRepository.save(transaction);
    }

    @Test
    void testCreateTransaction() {
        TransactionRequestDTO transactionRequest = new TransactionRequestDTO();

        transactionRequest.setSource_account_id(sourceAccount.getAccount_id());
        transactionRequest.setTarget_account_id(targetAccount.getAccount_id());
        transactionRequest.setAmount(500.00);

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/v1/transactions", transactionRequest, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testCreateTransaction_InvalidSourceAccount() {
        TransactionRequestDTO transactionRequest = new TransactionRequestDTO();
        transactionRequest.setSource_account_id(999L); // Non-existent account
        transactionRequest.setTarget_account_id(targetAccount.getAccount_id());
        transactionRequest.setAmount(500.00);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/transactions", transactionRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Source account not found");
    }
    @Test
    void testCreateTransaction_InsufficientFunds() {
        TransactionRequestDTO transactionRequest = new TransactionRequestDTO();
        transactionRequest.setSource_account_id(sourceAccount.getAccount_id()); // Source account with insufficient funds
        transactionRequest.setTarget_account_id(targetAccount.getAccount_id());
        transactionRequest.setAmount(10000.00); // Amount greater than balance

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/v1/transactions", transactionRequest, Map.class);
        Map<String,Object> responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("message")).isEqualTo("Insufficient funds");
    }

    @Test
    void testGetTransferHistory() {
        String url = String.format("/api/v1/transactions/history/%d", sourceAccount.getAccount_id());
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String,Object> responseBody = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("message")).isEqualTo("Transaction history fetched successfully");
    }

    @Test
    void testGetAllTransactions() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/v1/transactions", Map.class);
        // Extract data from the response
        Map<String, Object> responseBody = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("message")).isEqualTo("All transactions fetched successfully");
        assertThat(responseBody.get("status")).isEqualTo(200);
    }
}
