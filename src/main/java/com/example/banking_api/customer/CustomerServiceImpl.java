package com.example.banking_api.customer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(CustomerDto customerDto) {
        // Create a new Customer entity from the DTO
        Customer customer = new Customer(
                customerDto.getName(),
                customerDto.getStreet(),
                customerDto.getCity(),
                customerDto.getState(),
                customerDto.getPostal_code(),
                customerDto.getPhone_number()
        );

        // Save the Customer entity to the repository
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }


}
