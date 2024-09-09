package com.example.banking_api.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequestDTO {
    @NotNull(message = "Source account id cannot be null")
    private Long source_account_id;
    @NotNull(message = "Target account id cannot be null")
    private Long target_account_id;
    @NotNull(message = "Amount cannot be Null")
    private Double amount;

}
