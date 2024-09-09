package com.example.banking_api.unit.customer;

import com.example.banking_api.customer.Customer;
import com.example.banking_api.customer.CustomerDto;
import com.example.banking_api.customer.CustomerRepository;
import com.example.banking_api.customer.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceUnitTests {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("John Doe");
        customerDto.setStreet("123 Main St");
        customerDto.setCity("New York");
        customerDto.setState("NY");
        customerDto.setPostal_code("10001");
        customerDto.setPhone_number("123-456-7890");

        Customer customer = new Customer("John Doe", "123 Main St", "New York", "NY", "10001", "123-456-7890");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);


        Customer createdCustomer = customerService.createCustomer(customerDto);


        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getName()).isEqualTo(customerDto.getName());
        assertThat(createdCustomer.getStreet()).isEqualTo(customerDto.getStreet());
        assertThat(createdCustomer.getCity()).isEqualTo(customerDto.getCity());
        assertThat(createdCustomer.getState()).isEqualTo(customerDto.getState());
        assertThat(createdCustomer.getPostal_code()).isEqualTo(customerDto.getPostal_code());
        assertThat(createdCustomer.getPhone_number()).isEqualTo(customerDto.getPhone_number());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testGetAllCustomers() {
        // Arrange
        Customer customer1 = new Customer("John Doe", "123 Main St", "New York", "NY", "10001", "123-456-7890");
        Customer customer2 = new Customer("Jane Doe", "456 Elm St", "Los Angeles", "CA", "90001", "987-654-3210");
        List<Customer> customers = Arrays.asList(customer1, customer2);

        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.getAllCustomers();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(customer1, customer2);

        verify(customerRepository, times(1)).findAll();
    }
}
