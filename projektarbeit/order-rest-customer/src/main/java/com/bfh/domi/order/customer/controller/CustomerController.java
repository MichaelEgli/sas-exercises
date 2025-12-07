package com.bfh.domi.order.customer.controller;

import com.bfh.domi.order.customer.dto.CustomerInfo;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/customers")
public class CustomerController  {

    public final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public Customer findCustomer(@PathVariable long id) throws CustomerNotFoundException {
        return customerService.findCustomer(id);
    }

    @GetMapping(path = "?name", produces = APPLICATION_JSON_VALUE)
    public List<CustomerInfo> searchCustomers(@RequestParam String name) throws CustomerNotFoundException {
        return customerService.searchCustomer(name);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Customer> registerCustomer(@RequestBody Customer customer) throws Exception {
        Customer registeredCustomer = customerService.registerCustomer(customer);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(registeredCustomer.getId()).toUri();
        return ResponseEntity.created(location).body(registeredCustomer);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer) throws Exception {
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return ResponseEntity.ok(updatedCustomer);
        }
}
