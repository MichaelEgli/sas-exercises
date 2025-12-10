package com.bfh.domi.order.customer.service;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.model.Address;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;

import com.bfh.domi.order.customer.exception.EmailAlreadyExistsException;
import com.bfh.domi.order.customer.model.CreditCard;
import com.bfh.domi.order.customer.model.CreditCardType;
import com.bfh.domi.order.customer.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data-04-orders.sql")
public class CustomerServiceIT {

    @Autowired
    private CustomerService customerService;

    Long VALID_CUSTOMER_ID = 10000L;
    Long INVALID_CUSTOMER_ID = 99999L;
    String VALID_CUSTOMER_LASTNAME_FRAGMENT = "LlEr";
    String VALID_CUSTOMER_FIRSTNAME_FRAGMENT = "Anna";
    String VALID_CUSTOMER_FIRSTNAME_LASTNAME_FRAGMENT_MIXED = "i";
    String NO_CUSTOMER_FOUND_NAME_FRAGMENT = "Nonexistent";

    @Test
    public void findCustomerById() throws CustomerNotFoundException {

        var results = customerService.findCustomer(VALID_CUSTOMER_ID);
        assertThat(results.getId()).isEqualTo(VALID_CUSTOMER_ID);
    }

    @Test
    public void customerNotFoundException() {

        assertThatThrownBy(() -> customerService.findCustomer(INVALID_CUSTOMER_ID))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("customer " + INVALID_CUSTOMER_ID + " not found");
    }

    @Test
    public void searchCustomerName() throws CustomerNotFoundException {

        // LASTNAME
        var resultsLastname = customerService.searchCustomer(VALID_CUSTOMER_LASTNAME_FRAGMENT);
        assertThat(resultsLastname.size()).isEqualTo(2);

        // FIRSTNAME
        var resultsFirstname = customerService.searchCustomer(VALID_CUSTOMER_FIRSTNAME_FRAGMENT);
        assertThat(resultsFirstname.size()).isEqualTo(1);

        // MIXED
        var resultsMixed = customerService.searchCustomer(VALID_CUSTOMER_FIRSTNAME_LASTNAME_FRAGMENT_MIXED);
        assertThat(resultsMixed.size()).isEqualTo(2);

        // NO_CUSTOMER_FOUND_RESULT
        assertThatThrownBy(() -> customerService.searchCustomer(NO_CUSTOMER_FOUND_NAME_FRAGMENT))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("No customers found with name containing: " + NO_CUSTOMER_FOUND_NAME_FRAGMENT);
    }

    @Test
    public void registerCustomer() throws EmailAlreadyExistsException {

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
        customer.setEmail("michael.rothenbuehler@gmail.com");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        var registeredCustomer = customerService.registerCustomer(customer);
        assertThat(registeredCustomer.getFirstName()).isEqualTo(FIRSTNAME);
    }

    @Test
    public void registerCustomerEmailAlreadyExists() {

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
        customer.setEmail("susi.mueller@example.com");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        assertThatThrownBy(() -> customerService.registerCustomer(customer))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(customerService.EMAIL_ALREADY_USED_MESSAGE);
    }

    @Test
    public void updateCustomer() throws CustomerNotFoundException, EmailAlreadyExistsException {

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

        var customerAddressBeforeUpdate = customerService.findCustomer(VALID_CUSTOMER_ID);
        assertThat(customerAddressBeforeUpdate.getAddress().getCity()).isEqualTo("Zurich");

        var customerAddressAfterUpdate = customerService.updateCustomer(customer);
        assertThat(customerAddressAfterUpdate.getAddress().getCity()).isEqualTo("Neuenegg");
    }

    @Test
    public void updateCustomerNotExisting() {

        assertThatThrownBy(() -> customerService.findCustomer(INVALID_CUSTOMER_ID))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("customer " + INVALID_CUSTOMER_ID + " not found");
    }

    @Test
    public void updateCustomerEmailAlreadyExists() {

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

        assertThatThrownBy(() -> customerService.updateCustomer(customer))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(customerService.EMAIL_ALREADY_USED_MESSAGE);
    }

    @Test
    public void updateCustomerCanUpdateHisOwnEmail() throws CustomerNotFoundException, EmailAlreadyExistsException {

        String UPDATE_EMAIL_SAME_CUSTOMER = "anna2.schmidt@example.com";
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
        customer.setEmail(UPDATE_EMAIL_SAME_CUSTOMER);
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        var customerEmailBeforeUpdate = customerService.findCustomer(VALID_CUSTOMER_ID);
        assertThat(customerEmailBeforeUpdate.getEmail().equals("anna.schmidt@example.com"));

        var customerEmailAfterUpdate = customerService.updateCustomer(customer);
        assertThat(customerEmailAfterUpdate.getEmail().equals(UPDATE_EMAIL_SAME_CUSTOMER));
    }

}