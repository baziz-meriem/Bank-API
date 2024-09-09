package com.example.banking_api.customer;

import com.example.banking_api.shared.response.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Object> createCustomer(@RequestBody @Valid CustomerDto customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseHandler.generateResponse("Customer created successfully", HttpStatus.CREATED, createdCustomer);
    }

    @GetMapping
    public ResponseEntity<Object> getAllCustomers() {
        List <Customer> customers = customerService.getAllCustomers();
        return ResponseHandler.generateResponse("All customers fetched successfully", HttpStatus.OK, customers);
    }

}