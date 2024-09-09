package com.example.banking_api.shared.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", responseObj);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(map);
    }
    public static ResponseEntity<Object> generateErrorResponse(String message, HttpStatus status, Map<String, Object> errorDetails) {
        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "status", status.value(),
                        "message", message,
                        "errorDetails", errorDetails
                ));
    }
}
