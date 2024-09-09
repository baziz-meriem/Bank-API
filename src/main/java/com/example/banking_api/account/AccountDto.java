package com.example.banking_api.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccountDto {
    @NotBlank(message = "Branch code cannot be empty")
    @Size(min = 5, max = 5, message = "Branch code must be exactly 5 characters")
    private String branch_code;

    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "500.0", message = "Balance must be at least 500")
    private Double balance;

    @NotBlank(message = "Account type cannot be empty")
    private String account_type;

    @NotNull(message = "Customer ID cannot be null")
    private Long customer_id;

}

