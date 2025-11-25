package com.bfh.domi.order.services;

import com.bfh.domi.order.dto.CustomerInfo;
import com.bfh.domi.order.entity.Customer;
import com.bfh.domi.order.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findCustomer(long id) throws CustomerNotFoundException {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));
    }

    public List<CustomerInfo> searchCustomer(String name) {
        return customerRepository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    public Customer registerCustomer(Customer customer) {
        Optional<Customer> emailExisting = customerRepository.findByEmail(customer.getEmail());
        if (emailExisting.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        } else {
            return customerRepository.saveAndFlush(customer);
        }
    }

}
