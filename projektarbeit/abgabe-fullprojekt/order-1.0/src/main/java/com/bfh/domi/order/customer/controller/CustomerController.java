package com.bfh.domi.order.customer.controller;

import com.bfh.domi.order.customer.dto.RegisterCustomerDto;
import com.bfh.domi.order.customer.dto.CustomerInfo;
import com.bfh.domi.order.customer.dto.UpdateCustomerDto;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.customer.exception.EmailAlreadyExistsException;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/customers")
@Validated
public class CustomerController {

    public final CustomerService customerService;
    public final ConversionService conversionService;

    public CustomerController(CustomerService customerService, ConversionService conversionService) {
        this.customerService = customerService;
        this.conversionService = conversionService;
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Find a customer by ID")
    @ApiResponse(responseCode = "200", description = "Customer found successfully")
    @ApiResponse(responseCode = "404", description = "Customer with the given ID not found")
    public Customer findCustomer(@PathVariable @Positive long id) throws CustomerNotFoundException {
        return customerService.findCustomer(id);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Search customers by name")
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No customers found with the given name")
    public ResponseEntity<List<CustomerInfo>> searchCustomers(@RequestParam String name) throws CustomerNotFoundException {
        List<CustomerInfo> customers = customerService.searchCustomer(name);
        return ResponseEntity.ok(customers);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new customer")
    @ApiResponse(responseCode = "201", description = "Customer registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid customer data provided")
    @ApiResponse(responseCode = "409", description = "Email address already used by another customer")
    public ResponseEntity<Customer> registerCustomer(@Valid @RequestBody RegisterCustomerDto registerCustomerDto) throws EmailAlreadyExistsException {
        Customer newCustomer = conversionService.convert(registerCustomerDto, Customer.class);
        Customer registeredCustomer = customerService.registerCustomer(newCustomer);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(registeredCustomer.getId()).toUri();
        return ResponseEntity.created(location).body(registeredCustomer);
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an existing customer")
    @ApiResponse(responseCode = "200", description = "Customer updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid customer data provided")
    @ApiResponse(responseCode = "404", description = "Customer with the given ID not found")
    @ApiResponse(responseCode = "409", description = "Email address to changed already used by another customer")
    public ResponseEntity<Customer> updateCustomer(@PathVariable @Positive long id, @Valid @RequestBody UpdateCustomerDto updateCustomerDto) throws EmailAlreadyExistsException, CustomerNotFoundException {
        if (id != (updateCustomerDto.id())) {
            throw new IllegalArgumentException("ID in path and body do not match");
        }
        Customer updatedCustomer = conversionService.convert(updateCustomerDto, Customer.class);
        Customer savedCustomer = customerService.updateCustomer(updatedCustomer);
        return ResponseEntity.ok(savedCustomer);
    }
}
