package com.example.banking_api.unit.customer;

import com.example.banking_api.customer.Customer;
import com.example.banking_api.customer.CustomerDto;
import com.example.banking_api.customer.CustomerService;
import com.example.banking_api.customer.CustomerController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerIntegrationTestsUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllCustomers() throws Exception {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setCity("New York");
        customer.setState("NY");
        customer.setStreet("123 Main St");
        customer.setPhone_number("123-456-7890");
        customer.setPostal_code("12345");
        given(customerService.getAllCustomers()).willReturn(Collections.singletonList(customer));

        // Performing the GET request and validating the response
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("All customers fetched successfully")))
                .andExpect(jsonPath("$.data[0].name", is("John Doe")));
    }

    @Test
    public void testCreateCustomer() throws Exception {
        // Creating and initializing a mock DTO and entity
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("John Doe");
        customerDto.setCity("New York");
        customerDto.setState("NY");
        customerDto.setStreet("123 Main St");
        customerDto.setPhone_number("1234567891");
        customerDto.setPostal_code("12345");

        Customer createdCustomer = new Customer();
        createdCustomer.setName("John Doe");
        createdCustomer.setCity("New York");
        createdCustomer.setState("NY");
        createdCustomer.setStreet("123 Main St");
        createdCustomer.setPhone_number("1234567891");
        createdCustomer.setPostal_code("12345");

        // Mocking the service method to return the created customer
        given(customerService.createCustomer(customerDto)).willReturn(createdCustomer);

        // Performing the POST request and validating the response
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Customer created successfully")));

    }
}
