package com.bfh.domi.order.customer.service;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.testdata.TestdataCreator;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.customer.exception.EmailAlreadyExistsException;
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
@SpringBootTest(properties = {
        "spring.jms.listener.auto-startup=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data-04-orders.sql")
public class CustomerServiceIT {

    @Autowired
    private CustomerService customerService;

    Long VALID_CUSTOMER_ID = TestdataCreator.getCustomerId();
    Long INVALID_CUSTOMER_ID = TestdataCreator.getCustomerIdNotExistent();
    String VALID_CUSTOMER_LASTNAME_FRAGMENT = TestdataCreator.getLastnameFragment();
    String VALID_CUSTOMER_FIRSTNAME_FRAGMENT = TestdataCreator.getFirstname();
    String VALID_CUSTOMER_FIRSTNAME_LASTNAME_FRAGMENT_MIXED = TestdataCreator.getFirstnameLastnameFragementMixed();

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
    }

    @Test
    public void registerCustomer() throws EmailAlreadyExistsException {
        var registeredCustomer = customerService.registerCustomer(TestdataCreator.getNewCustomer());
        assertThat(registeredCustomer.getFirstName()).isEqualTo("Michael");
    }

    @Test
    public void registerCustomerEmailAlreadyExists() {
        Customer customer = TestdataCreator.getNewCustomer();
        customer.setEmail(TestdataCreator.getEmailAlreadyUsed());

        assertThatThrownBy(() -> customerService.registerCustomer(customer))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(customerService.EMAIL_ALREADY_USED_MESSAGE);
    }

    @Test
    public void updateCustomer() throws CustomerNotFoundException, EmailAlreadyExistsException {
        var customerAddressBeforeUpdate = customerService.findCustomer(VALID_CUSTOMER_ID);
        assertThat(customerAddressBeforeUpdate.getAddress().getCity()).isEqualTo("Zurich");

        var customerAddressAfterUpdate = customerService.updateCustomer(TestdataCreator.getUpdateCustomer());
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
        Customer customer = TestdataCreator.getUpdateCustomer();
        customer.setEmail(TestdataCreator.getEmailAlreadyUsed());

        assertThatThrownBy(() -> customerService.updateCustomer(customer))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(customerService.EMAIL_ALREADY_USED_MESSAGE);
    }

    @Test
    public void updateCustomerCanUpdateHisOwnEmail() throws CustomerNotFoundException, EmailAlreadyExistsException {
        String UPDATE_EMAIL_SAME_CUSTOMER = "anna2.schmidt@example.com";
        Customer customer = TestdataCreator.getUpdateCustomer();
        customer.setEmail(UPDATE_EMAIL_SAME_CUSTOMER);

        var customerEmailBeforeUpdate = customerService.findCustomer(VALID_CUSTOMER_ID);
        assertThat(customerEmailBeforeUpdate.getEmail().equals("anna.schmidt@example.com"));

        var customerEmailAfterUpdate = customerService.updateCustomer(customer);
        assertThat(customerEmailAfterUpdate.getEmail().equals(UPDATE_EMAIL_SAME_CUSTOMER));
    }
}