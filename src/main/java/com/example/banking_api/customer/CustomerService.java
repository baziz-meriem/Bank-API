package com.example.banking_api.customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(CustomerDto customer);
    List<Customer> getAllCustomers();

}
