package com.bfh.domi.order.customer.dto;

import com.bfh.domi.order.customer.model.CreditCard;
import com.bfh.domi.order.common.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterCustomerDto(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotNull(message = "Address is required")
        @Valid
        Address address,

        @NotNull(message = "Credit card is required")
        @Valid
        CreditCard creditCard
) {}

