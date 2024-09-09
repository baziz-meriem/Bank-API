package com.example.banking_api.account;

import com.example.banking_api.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long account_id;


    @NotBlank(message = "Branch code cannot be empty")
    @Size(min = 5, max = 5, message = "Branch code must be exactly 5 characters")
    private String branch_code;

    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "500.0",message = "Balance must be at least 500")
    @Min(500)
    private Double balance;

    @NotNull(message = "Account type cannot be null")
    @Enumerated(EnumType.STRING)
    private AccountType account_type;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
