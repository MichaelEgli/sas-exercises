package com.bfh.domi.order.customer.controller;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.model.Address;
import com.bfh.domi.order.customer.dto.CustomerInfo;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.customer.exception.EmailAlreadyExistsException;
import com.bfh.domi.order.customer.model.CreditCard;
import com.bfh.domi.order.customer.model.CreditCardType;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Sql("/test-data-04-orders.sql")
public class CustomerControllerIT {

    @Autowired
    private CustomerController customerController;

    @Autowired
    private CustomerService customerService; // only for accessing EMAIL_ALREADY_USED_MESSAGE

    Long VALID_CUSTOMER_ID = 10000L;
    Long INVALID_CUSTOMER_ID = 99999L;
    String VALID_CUSTOMER_FIRSTNAME = "Anna";
    String VALID_CUSTOMER_FRAGMENT = "LlEr";
    String NO_CUSTOMER_FOUND_NAME_FRAGMENT = "Nonexistent";

    @Test
    void findCustomerById() throws Exception {

        Customer customer = customerController.findCustomer(VALID_CUSTOMER_ID);
        assertThat(customer.getId()).isEqualTo(VALID_CUSTOMER_ID);
    }

    @Test
    public void customerNotFoundException() {

        assertThatThrownBy(() -> customerController.findCustomer(INVALID_CUSTOMER_ID))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("customer " + INVALID_CUSTOMER_ID + " not found");
    }

    @Test
    void searchCustomersByName() throws Exception {

        // One Customer by firstname found
        List<CustomerInfo> customer = customerController.searchCustomers(VALID_CUSTOMER_FIRSTNAME);
        assertThat(customer.size()).isEqualTo(1);
        assertThat(customer.getFirst().firstName()).isEqualTo(VALID_CUSTOMER_FIRSTNAME);

        // Two Customers by lastname found
        List<CustomerInfo> customers = customerController.searchCustomers(VALID_CUSTOMER_FRAGMENT);
        assertThat(customers.size()).isEqualTo(2);

        // No Customers found
        assertThatThrownBy(() -> customerController.searchCustomers(NO_CUSTOMER_FOUND_NAME_FRAGMENT))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("No customers found with name containing: " + NO_CUSTOMER_FOUND_NAME_FRAGMENT);
    }

    @Test
    void registerCustomer() throws Exception {

        String FIRSTNAME = "Michael";
        String LASTNAME = "Rothenbühler";
        String CREDIT_CARD = "7575 7575 7575 7575";
        String ADDRESS_COUNTRY = "Switzerland";
        String EMAIL = "michael.rothenbuehler@gmail.com";

        Address address = new Address();
        address.setStreet("Grabmattweg 16");
        address.setStateProvince("BE");
        address.setPostalCode("3176");
        address.setCity("Neuenegg");
        address.setCountry(ADDRESS_COUNTRY);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.VISA);
        creditCard.setNumber(CREDIT_CARD);
        creditCard.setExpirationMonth(10);
        creditCard.setExpirationYear(2028);

        Customer customer = new Customer();
        customer.setFirstName(FIRSTNAME);
        customer.setLastName(LASTNAME);
        customer.setEmail(EMAIL);
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        var response = customerController.registerCustomer(customer);
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CREATED);
        Customer registeredCustomer = response.getBody();
        assert registeredCustomer != null;
        assertThat(registeredCustomer.getLastName()).isEqualTo(LASTNAME);
    }

    @Test
    void registerCustomerWithExistingEmail() throws EmailAlreadyExistsException {

        String EXISTING_EMAIL = "susi.mueller@example.com";
        String FIRSTNAME = "Michael";
        String LASTNAME = "Rothenbühler";
        String CREDIT_CARD = "7575 7575 7575 7575";
        String ADDRESS_COUNTRY = "Switzerland";

        // Customer
        Address address = new Address();
        address.setStreet("Grabmattweg 16");
        address.setStateProvince("BE");
        address.setPostalCode("3176");
        address.setCity("Neuenegg");
        address.setCountry(ADDRESS_COUNTRY);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.VISA);
        creditCard.setNumber(CREDIT_CARD);
        creditCard.setExpirationMonth(10);
        creditCard.setExpirationYear(2028);

        Customer customer = new Customer();
        customer.setFirstName(FIRSTNAME);
        customer.setLastName(LASTNAME);
        customer.setEmail(EXISTING_EMAIL);
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        assertThatThrownBy(() -> customerController.registerCustomer(customer))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(customerService.EMAIL_ALREADY_USED_MESSAGE);
    }

    @Test
    public void updateCustomer() throws Exception {

        String FIRSTNAME = "Anna";
        String LASTNAME = "Schmidt";
        String CREDIT_CARD = "4111 1111 1111 1111";
        String ADDRESS_COUNTRY = "Switzerland";

        // Customer
        Address address = new Address();
        address.setStreet("Grabmattweg 16");
        address.setStateProvince("BE");
        address.setPostalCode("3176");
        address.setCity("Neuenegg");
        address.setCountry(ADDRESS_COUNTRY);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.VISA);
        creditCard.setNumber(CREDIT_CARD);
        creditCard.setExpirationMonth(12);
        creditCard.setExpirationYear(2026);

        Customer customer = new Customer();
        customer.setId(VALID_CUSTOMER_ID);
        customer.setFirstName(FIRSTNAME);
        customer.setLastName(LASTNAME);
        customer.setEmail("anna.schmidt@example.com");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        var response = customerController.updateCustomer(customer);
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
        Customer registeredCustomer = response.getBody();
        assert registeredCustomer != null;
        assertThat(registeredCustomer.getAddress().getCity()).contains("Neuenegg");
    }

    @Test
    void updateCustomerInvalidCustomerId() {

        String FIRSTNAME = "Michael";
        String LASTNAME = "Rothenbühler";
        String CREDIT_CARD = "7575 7575 7575 7575";
        String ADDRESS_COUNTRY = "Switzerland";
        String EMAIL = "michael.rothenbuehler@gmail.com";

        Address address = new Address();
        address.setStreet("Grabmattweg 16");
        address.setStateProvince("BE");
        address.setPostalCode("3176");
        address.setCity("Neuenegg");
        address.setCountry(ADDRESS_COUNTRY);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.VISA);
        creditCard.setNumber(CREDIT_CARD);
        creditCard.setExpirationMonth(10);
        creditCard.setExpirationYear(2028);

        Customer customer = new Customer();
        customer.setId(INVALID_CUSTOMER_ID);
        customer.setFirstName(FIRSTNAME);
        customer.setLastName(LASTNAME);
        customer.setEmail(EMAIL);
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        assertThatThrownBy(() -> customerController.updateCustomer(customer))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("customer " + INVALID_CUSTOMER_ID + " not found");
    }

    @Test
    void updateCustomerWithExistingEmail() throws EmailAlreadyExistsException {

        String EMAIL_ALREADY_EXISTS_DIFFERENT_CUSTOMER = "susi.mueller@example.com";

        String FIRSTNAME = "Anna";
        String LASTNAME = "Schmidt";
        String CREDIT_CARD = "4111 1111 1111 1111";
        String ADDRESS_COUNTRY = "Switzerland";

        // Customer
        Address address = new Address();
        address.setStreet("Grabmattweg 16");
        address.setStateProvince("BE");
        address.setPostalCode("3176");
        address.setCity("Neuenegg");
        address.setCountry(ADDRESS_COUNTRY);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.VISA);
        creditCard.setNumber(CREDIT_CARD);
        creditCard.setExpirationMonth(12);
        creditCard.setExpirationYear(2026);

        Customer customer = new Customer();
        customer.setId(VALID_CUSTOMER_ID); //
        customer.setFirstName(FIRSTNAME);
        customer.setLastName(LASTNAME);
        customer.setEmail(EMAIL_ALREADY_EXISTS_DIFFERENT_CUSTOMER);
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        assertThatThrownBy(() -> customerController.updateCustomer(customer))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(customerService.EMAIL_ALREADY_USED_MESSAGE);
    }
}

