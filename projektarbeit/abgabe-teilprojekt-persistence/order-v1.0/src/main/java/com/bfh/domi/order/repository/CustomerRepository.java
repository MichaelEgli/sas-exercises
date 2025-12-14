package com.bfh.domi.order.repository;

import com.bfh.domi.order.dto.CustomerInfo;
import com.bfh.domi.order.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Query 1: Finde den Kunden (Customer) mit einer bestimmten E-Mail-Adresse
    Optional<Customer> findByEmail(String email);

    // Query 2: Finde Informationen (CustomerInfo) zu allen Kunden, deren Vor- oder Nachname
    // einen bestimmten Namen enthält. Gross-/Kleinschreibung soll ignoriert werden.
    List<CustomerInfo> findAllByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstNameFragment, String lastNameFragment);

}
