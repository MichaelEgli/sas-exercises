package com.bfh.domi.order.common.repository;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.testdata.TestdataCreator;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.repository.CustomerRepository;
import com.bfh.domi.order.order.model.Order;
import com.bfh.domi.order.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QueriesTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Query 1: Finde den Kunden (Customer) mit einer bestimmten E-Mail-Adresse
    @Test
    @Sql("/test-data-04-orders.sql")
    void q1_findByEmail_shouldReturnCustomer_whenEmailExists() {
        Optional<Customer> result = customerRepository.findByEmail(TestdataCreator.getEmail());
        assertThat(result.isPresent());
        assertThat(result.get().getEmail()).isEqualTo(TestdataCreator.getEmail());
        assertThat(result.get().getFirstName()).isEqualTo(TestdataCreator.getFirstname());
    }

    @Test
    @Sql("/test-data-04-orders.sql")
    void q1_findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<Customer> result = customerRepository.findByEmail(TestdataCreator.getEmailNonExistent());
        assertFalse(result.isPresent());
    }

    // Query 2: Finde Informationen (CustomerInfo) zu allen Kunden, deren Vor- oder Nachname
    // einen bestimmten Namen enthält. Gross-/Kleinschreibung soll ignoriert werden.
    @Test
    @Sql("/test-data-04-orders.sql")
    void q2_findAllByFirstNameAndLastNameFragment_shouldReturnCustomers_whenNameMatches() {
        var results = customerRepository.findAllByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(TestdataCreator.getFirstnameFragmentEmpty(), TestdataCreator.getLastnameFragment());
        assertThat(results).hasSize(2);
        assertThat(results).extracting("firstName").containsExactlyInAnyOrder("Max", "Susi");
    }

    @Test
    @Sql("/test-data-04-orders.sql")
    void q2_findAllByFirstNameAndLastNameFragment_shouldReturnEmpty_whenNoNameMatches() {
        var results = customerRepository.findAllByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(TestdataCreator.getFirstnameFragmentNonExistent(), TestdataCreator.getLastnameFragmentNotMatching());
        assertThat(results).isEmpty();
    }

    // Query 5: Finde die Bestellung (Order) mit einer bestimmten Nummer
    @Test
    @Sql("/test-data-04-orders.sql")
    void q5_findOrderById_shouldReturnOrder_whenOrderIdExists() {
        Optional<Order> result = orderRepository.findOrderById(TestdataCreator.getOrderId());
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(TestdataCreator.getOrderId());
        assertThat(result.get().getCustomer().getFirstName()).isEqualTo("Anna");
    }

    @Test
    @Sql("/test-data-04-orders.sql")
    void q5_findOrderById_shouldReturnEmpty_whenOrderIdDoesNotExist() {
        Optional<Order> result = orderRepository.findOrderById(TestdataCreator.getOrderIdNotExistent());
        assertThat(result.isPresent()).isFalse();
    }

    // Query 6: Finde Informationen zu allen Bestellungen (OrderInfo) eines bestimmten Kunden
    // in einem bestimmten Zeitraum (Datum von/bis)
    @Test
    @Sql("/test-data-04-orders.sql")
    void q6_findAllByCustomerIdAndDateBetween_shouldReturnOrders_whenCustomerIdAndDateRangeMatch() {
        LocalDateTime START_DATE = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime END_DATE = LocalDateTime.of(2024, 12, 31, 23, 59);

        var results = orderRepository.findAllByCustomerIdAndDateBetween(TestdataCreator.getCustomerId(), START_DATE, END_DATE);
        assertThat(results.size()).isEqualTo(2);
    }

    // Query 7: Schreibe eine Abfrage die den Bestellungstotalpreis, die Anzahl Positionen
    // sowie den Durchschnittsbetrag der Positions von allen Bestellungen gruppiert nach Jahr und Kunde zurückgibt
    @Test
    @Sql("/test-data-50-orders.sql")
    void q7_findOrderStatisticsGroupedByYearAndCustomer_shouldReturnStatistics() {
        var results = orderRepository.getOrderStatisticsByYearAndCustomer();
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(30);
        // Verify years are in descending order
        assertThat(results.getFirst().getYear()).isEqualTo(2025);
        assertThat(results.getLast().getYear()).isEqualTo(2023);

        // Find specific customer statistics for detailed verification
        // Customer 10000 (Anna Schmidt) in 2023
        var anna2023 = results.stream()
                .filter(s -> s.getCustomerId().equals(10000L) && s.getYear().equals(2023))
                .findFirst()
                .orElseThrow();
        assertThat(anna2023.getCustomerName()).isEqualTo("Anna Schmidt");
        assertThat(anna2023.getTotalOrderPrice()).isEqualByComparingTo("195.00");
        assertThat(anna2023.getItemCount()).isEqualTo(3L);
        assertThat(anna2023.getAverageItemAmount()).isEqualTo(81.7);

        // Customer 10050 (Max Muller) in 2023
        var max2023 = results.stream()
                .filter(s -> s.getCustomerId().equals(10050L) && s.getYear().equals(2023))
                .findFirst()
                .orElseThrow();
        assertThat(max2023.getCustomerName()).isEqualTo("Max Muller");
        assertThat(max2023.getTotalOrderPrice()).isEqualByComparingTo("325.00");
        assertThat(max2023.getItemCount()).isEqualTo(4L);
        assertThat(max2023.getAverageItemAmount()).isEqualTo(81.2);
    }
}
