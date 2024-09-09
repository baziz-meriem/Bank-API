package com.example.banking_api.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customer_id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 20, message = "Name must not exceed 20 characters")
    private String name;

    @NotBlank(message = "Street cannot be empty")
    @Size(max = 100, message = "Street must not exceed 100 characters")
    private String street;

    @NotBlank(message = "City cannot be empty")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @NotBlank(message = "State cannot be empty")
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;

    @NotBlank(message = "Postal code cannot be empty")
    @Pattern(regexp = "\\d{5}", message = "Postal code must be exactly 5 digits")
    @Size(min=5,max=5)
    private String postal_code;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phone_number;


    public Customer(String name, String street, String city, String state, String postalCode, String phoneNumber) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postal_code = postalCode;
        this.phone_number = phoneNumber;
    }
}