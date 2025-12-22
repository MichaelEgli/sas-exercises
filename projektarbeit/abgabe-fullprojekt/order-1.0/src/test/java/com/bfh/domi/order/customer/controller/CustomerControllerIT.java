package com.bfh.domi.order.customer.controller;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.testdata.TestdataCreator;
import com.bfh.domi.order.customer.dto.CustomerInfo;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.service.CustomerService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.jms.listener.auto-startup=false"
})
@Sql("/test-data-04-orders.sql")
class CustomerControllerIT {

    @Autowired
    private CustomerService customerService; // only for accessing EMAIL_ALREADY_USED_MESSAGE

    private static final String BASE_PATH = "/customers";  // Todo: use property
    private final RestClient restClient;

    Long VALID_CUSTOMER_ID = TestdataCreator.getCustomerId();
    Long INVALID_CUSTOMER_ID = TestdataCreator.getCustomerIdNotExistent();
    String VALID_CUSTOMER_LASTNAME_FRAGMENT = TestdataCreator.getLastnameFragment();
    String VALID_CUSTOMER_FIRSTNAME_FRAGMENT = TestdataCreator.getFirstname();
    String VALID_CUSTOMER_FIRSTNAME_LASTNAME_FRAGMENT_MIXED = TestdataCreator.getFirstnameLastnameFragementMixed();
    String NO_CUSTOMER_FOUND_NAME_FRAGMENT = TestdataCreator.getCustomerNameNotExistent();

    public CustomerControllerIT(@LocalServerPort int port) {
        restClient = RestClient.create("http://localhost:" + port);
    }

    private String loadJsonFromFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("requests/" + fileName);
        return Files.readString(resource.getFile().toPath());
    }

    @Test
    public void findCustomerById() {
        ResponseEntity<Customer> response = restClient.get().uri(BASE_PATH + "/" + VALID_CUSTOMER_ID).retrieve().toEntity(Customer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Customer customer = response.getBody();
        assertThat(customer.getId()).isEqualTo(VALID_CUSTOMER_ID);
    }

    @Test
    public void customerNotFoundException() {
        try {
            restClient.get().uri(BASE_PATH + "/" + INVALID_CUSTOMER_ID).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
            assertThat(e.getMessage()).contains("customer " + INVALID_CUSTOMER_ID + " not found");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void searchCustomersByName() {
        // One Customer found by firstname fragment
        ResponseEntity<CustomerInfo @NotNull []> response = restClient.get().uri(uriBuilder ->
                uriBuilder.path(BASE_PATH)
                        .queryParam("name", VALID_CUSTOMER_FIRSTNAME_FRAGMENT)
                        .build()
        ).retrieve().toEntity(CustomerInfo[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert response.getBody() != null;
        assertThat(response.getBody().length).isEqualTo(1);
        assertThat(response.getBody()[0].firstName()).isEqualTo("Anna");

        // Two Customers found by lastname fragment
        ResponseEntity<CustomerInfo @NotNull []> response2 = restClient.get().uri(uriBuilder ->
                uriBuilder.path(BASE_PATH)
                        .queryParam("name", VALID_CUSTOMER_LASTNAME_FRAGMENT)
                        .build()
        ).retrieve().toEntity(CustomerInfo[].class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert response2.getBody() != null;
        assertThat(response2.getBody().length).isEqualTo(2);

        // Two Customers found by firstname + lastname fragment mixed
        ResponseEntity<CustomerInfo @NotNull []> response3 = restClient.get().uri(uriBuilder ->
                uriBuilder.path(BASE_PATH)
                        .queryParam("name", VALID_CUSTOMER_FIRSTNAME_LASTNAME_FRAGMENT_MIXED)
                        .build()
        ).retrieve().toEntity(CustomerInfo[].class);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert response3.getBody() != null;
        assertThat(response3.getBody().length).isEqualTo(2);

        // No customer found does return OK with empty body
        ResponseEntity<CustomerInfo @NotNull []> response4 = restClient.get().uri(uriBuilder ->
                uriBuilder.path(BASE_PATH)
                        .queryParam("name", NO_CUSTOMER_FOUND_NAME_FRAGMENT)
                        .build()
        ).retrieve().toEntity(CustomerInfo[].class);
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response4.getBody()).isEmpty();
    }

    @Test
    public void registerCustomer() throws IOException {
        ResponseEntity<Customer> response = restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomer.json")).retrieve().toEntity(Customer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void registerCustomerWithExistingEmailFails() throws IOException {
        try {
            restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomerExistingEmail.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getMessage()).contains(customerService.EMAIL_ALREADY_USED_MESSAGE);
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void updateCustomer() throws IOException {
        ResponseEntity<Customer> response = restClient.put().uri(BASE_PATH + "/" + VALID_CUSTOMER_ID).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("updateCustomer.json")).retrieve().toEntity(Customer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateCustomerWithExistingEmailFails() throws IOException {
        try {
            restClient.put().uri(BASE_PATH + "/" + VALID_CUSTOMER_ID).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("updateCustomerExistingEmail.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getMessage()).contains(customerService.EMAIL_ALREADY_USED_MESSAGE);
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void updateCustomerWithInvalidCustomerIdFails() throws IOException {
        try {
            restClient.put().uri(BASE_PATH + "/" + INVALID_CUSTOMER_ID).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("updateCustomerInvalidId.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
            assertThat(e.getMessage()).contains("customer " + INVALID_CUSTOMER_ID + " not found");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void updateCustomerWithMismatchedIdFails() throws IOException {
        try {
            restClient.put().uri(BASE_PATH + "/" + 10001L).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("updateCustomerIdMismatch.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("ID in path and body do not match");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void registerCustomerWithBlankFirstNameFails() throws IOException {
        try {
            restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomerBlankFirstName.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("First name is required");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void registerCustomerWithBlankLastNameFails() throws IOException {
        try {
            restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomerBlankLastName.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("Last name is required");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void registerCustomerWithInvalidEmailFails() throws IOException {
        try {
            restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomerInvalidEmail.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("Email should be valid");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void registerCustomerWithNullAddressFails() {
        try {
            restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomerNullAddress.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("Address is required");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void registerCustomerWithNullCreditCardFails() {
        try {
            restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomerNullCreditCard.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("Credit card is required");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void registerCustomerWithFirstNameTooLongFails() {
        try {
            restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomerFirstNameTooLong.json")).retrieve().toEntity(Customer.class);
            fail(); // make test fail if no exception is thrown
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("First name must not exceed 50 characters");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void registerCustomerVerifyLocationHeader() throws IOException {
        ResponseEntity<Customer> response = restClient.post().uri(BASE_PATH).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("registerCustomer.json")).retrieve().toEntity(Customer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).contains("/customers/");

        System.out.println("Response: " + response.getBody());

        // Verify the body contains the created customer
        Customer createdCustomer = response.getBody();
        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getId()).isNotNull();
        assertThat(createdCustomer.getFirstName()).isEqualTo("Alice");
        assertThat(createdCustomer.getLastName()).isEqualTo("Smith");
        assertThat(createdCustomer.getEmail()).isEqualTo("alice@example.org");
    }

    @Test
    public void updateCustomerVerifyResponseBody() throws IOException {
        ResponseEntity<Customer> response = restClient.put().uri(BASE_PATH + "/" + VALID_CUSTOMER_ID).contentType(MediaType.APPLICATION_JSON).body(loadJsonFromFile("updateCustomer.json")).retrieve().toEntity(Customer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify the body contains the updated customer
        Customer updatedCustomer = response.getBody();
        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getId()).isEqualTo(VALID_CUSTOMER_ID);
        assertThat(updatedCustomer.getFirstName()).isEqualTo("Anna");
        assertThat(updatedCustomer.getLastName()).isEqualTo("Schmidt");
        assertThat(updatedCustomer.getEmail()).isEqualTo("anna.schmidt@example.com");
    }
}
