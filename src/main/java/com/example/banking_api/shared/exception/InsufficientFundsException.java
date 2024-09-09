package com.example.banking_api.shared.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends CustomException {
    public InsufficientFundsException(String insufficientFundsInSourceAccount) {
        super(insufficientFundsInSourceAccount);
    }
}
