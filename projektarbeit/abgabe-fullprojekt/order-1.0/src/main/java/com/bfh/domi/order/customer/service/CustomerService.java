package com.bfh.domi.order.customer.service;

import com.bfh.domi.order.common.logging.LoggingService;
import com.bfh.domi.order.customer.dto.CustomerInfo;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.customer.exception.EmailAlreadyExistsException;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    public final String EMAIL_ALREADY_USED_MESSAGE = "email address already used by another customer";
    private final LoggingService loggingService;

    public CustomerService(CustomerRepository customerRepository, LoggingService loggingService) {
        this.customerRepository = customerRepository;
        this.loggingService = loggingService;
    }

    public Customer findCustomer(long id) throws CustomerNotFoundException {
        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("customer " + id + " not found"));
    }

    public List<CustomerInfo> searchCustomer(String name) {
        return customerRepository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Transactional(rollbackFor = IllegalStateException.class)
    public Customer registerCustomer(Customer customer) throws EmailAlreadyExistsException {
        Optional<Customer> emailExisting = customerRepository.findByEmail(customer.getEmail());
        if (emailExisting.isPresent()) {
            throw new EmailAlreadyExistsException(EMAIL_ALREADY_USED_MESSAGE);
        } else {
            try {
                return customerRepository.save(customer);
            } catch (Exception e) {
                loggingService.log(e.getMessage());
                throw new IllegalStateException(e);
            }
        }
    }

    @Transactional(rollbackFor = IllegalStateException.class)
    public Customer updateCustomer(Customer customer) throws CustomerNotFoundException, EmailAlreadyExistsException {
        Optional<Customer> customerExisting = customerRepository.findById(customer.getId());
        List<CustomerInfo> emailExisting = customerRepository.findCustomersByEmail(customer.getEmail());
        if (customerExisting.isPresent()) {
            if (customerExisting.get().getEmail().equals(customer.getEmail())) {
                try {
                    return customerRepository.save(customer);
                } catch (Exception e) {
                    loggingService.log(e.getMessage());
                    throw new IllegalStateException(e);
                }
            } else {
                if (emailExisting.isEmpty()) {
                    try {
                        return customerRepository.save(customer);
                    } catch (Exception e) {
                        loggingService.log(e.getMessage());
                        throw new IllegalStateException(e);
                    }
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