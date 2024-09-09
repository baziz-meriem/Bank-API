package com.example.banking_api.integration;

import com.example.banking_api.customer.Customer;
import com.example.banking_api.customer.CustomerDto;
import com.example.banking_api.customer.CustomerRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void testGetAllCustomers() {
        // Set up initial data
        Customer customer = new Customer(
                "John Doe",
                "123 Elm St",
                "Springfield",
                "IL",
                "62704",
                "1234567891"
        );
        customerRepository.save(customer);

        // Test the GET /api/v1/customers endpoint
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/customers", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("All customers fetched successfully");
    }

    @Test
    void testCreateCustomer() {
        // Create a new customer DTO
        CustomerDto customer = new CustomerDto();
        customer.setName("Jane Doe");
        customer.setStreet("456 Oak St");
        customer.setCity("Springfield");
        customer.setState("IL");
        customer.setPostal_code("62704");
        customer.setPhone_number("1234567890");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/customers", customer, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("Customer created successfully");
    }

    @Test
    void testCreateCustomerWithInvalidInput() {
        // Create a new customer DTO with both an invalid phone number and postal code and missing name
        CustomerDto customer = new CustomerDto();
        customer.setStreet("456 Oak St");
        customer.setCity("Springfield");
        customer.setState("IL");
        customer.setPostal_code("123"); // Invalid postal code (not 5 digits)
        customer.setPhone_number("12345"); // Invalid phone number (not 10 digits)


        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/customers", customer, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Postal code must be exactly 5 digits");
        assertThat(response.getBody()).contains("Phone number must be exactly 10 digits");
        assertThat(response.getBody()).contains("Name cannot be empty");
    }
}
