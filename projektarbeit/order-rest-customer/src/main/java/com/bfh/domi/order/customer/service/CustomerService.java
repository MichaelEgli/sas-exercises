package com.bfh.domi.order.customer.service;

import com.bfh.domi.order.customer.dto.CustomerInfo;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.customer.exception.EmailAlreadyExistsException;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    public final String EMAIL_ALREADY_USED_MESSAGE = "email address already used by another customer";

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findCustomer(long id) throws CustomerNotFoundException {
        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("customer " + id + " not found"));
    }

    public List<CustomerInfo> searchCustomer(String name) throws CustomerNotFoundException {
        List<CustomerInfo> results = customerRepository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                name, name);
        if (results == null || results.isEmpty()) {
            throw new CustomerNotFoundException("No customers found with name containing: " + name);
        }
        return results;
    }

    public Customer registerCustomer(Customer customer) throws EmailAlreadyExistsException {
        Optional<Customer> emailExisting = customerRepository.findByEmail(customer.getEmail());
        if (emailExisting.isPresent()) {
            throw new EmailAlreadyExistsException(EMAIL_ALREADY_USED_MESSAGE);
        } else {
            return customerRepository.saveAndFlush(customer);
        }
    }

    public Customer updateCustomer(Customer customer) throws CustomerNotFoundException, EmailAlreadyExistsException {
        Optional<Customer> customerExisting = customerRepository.findById(customer.getId());
        List<CustomerInfo> emailExisting = customerRepository.findCustomersByEmail(customer.getEmail());
        if (customerExisting.isPresent()) {
            if (customerExisting.get().getEmail().equals(customer.getEmail())) {
                return customerRepository.saveAndFlush(customer);
            } else {
                if (emailExisting.isEmpty()) {
                    return customerRepository.saveAndFlush(customer);
                } else {
                    throw new EmailAlreadyExistsException(EMAIL_ALREADY_USED_MESSAGE);
                }
            }
        } else {
            throw new CustomerNotFoundException(
                    "customer " + customer.getId() + " not found");
        }
    }

}