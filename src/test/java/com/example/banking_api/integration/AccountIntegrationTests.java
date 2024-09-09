package com.example.banking_api.integration;

import com.example.banking_api.account.*;
import com.example.banking_api.customer.Customer;
import com.example.banking_api.customer.CustomerRepository;
import jakarta.transaction.Transactional;
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
public class AccountIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Long accountId;
    private Long customerId;

    @BeforeEach
    void setUp() {
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
        customerId = customer.getCustomer_id();
        System.out.println("Customer ID: " + customerId);

        // Set up initial account data
        Account account = new Account();
        account.setBranch_code("00101");
        account.setBalance(1000.00);
        account.setAccount_type(AccountType.valueOf("SAVINGS"));
        account.setCustomer(customer);

        accountRepository.save(account);
        accountId = account.getAccount_id();
        System.out.println("Account ID: " + accountId);

    }

    @Test
    void testCreateAccount() {
        AccountDto accountDto = new AccountDto();
        accountDto.setBranch_code("00232");
        accountDto.setBalance(2000.00);
        accountDto.setAccount_type("CHECKING");
        accountDto.setCustomer_id(customerId);

        ResponseEntity<Account> response = restTemplate.postForEntity("/api/v1/accounts", accountDto, Account.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Account createdAccount = response.getBody();
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBranch_code()).isEqualTo("00232");
        assertThat(createdAccount.getBalance()).isEqualTo(2000.00);
    }
    @Test
    void testGetBalanceForExistingAccount() {
        String url = "/api/v1/accounts/" + accountId + "/balance";
        ResponseEntity<Double> response = restTemplate.getForEntity(url, Double.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(1000.0);
    }

    @Test
    void testGetBalanceForNonExistentAccount() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/accounts/999/balance", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Account not found");
    }

    @Test
    void testCreateAccountWithInvalidData() {
        AccountDto accountDto = new AccountDto();
        accountDto.setBranch_code("12");
        accountDto.setBalance(100.00);
        accountDto.setAccount_type("CHECKING");
        accountDto.setCustomer_id(accountId);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/accounts", accountDto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains(
                "Branch code must be exactly 5 characters",
                "Balance must be at least 500"
        );
    }
    @Test
    void testCreateAccountWithNonExistentCustomer() {
        // Create an AccountDto with a non-existent customer ID
        AccountDto accountDto = new AccountDto();
        accountDto.setBranch_code("12345");
        accountDto.setBalance(1000.00);
        accountDto.setAccount_type("CHECKING");
        accountDto.setCustomer_id(999L);  // Non-existent customer ID

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/accounts", accountDto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        String responseBody = response.getBody();
        assertThat(responseBody).contains("Customer with ID 999 not found");
    }



}
