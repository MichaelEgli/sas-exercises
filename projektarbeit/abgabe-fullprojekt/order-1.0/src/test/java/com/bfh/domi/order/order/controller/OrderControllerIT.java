package com.bfh.domi.order.order.controller;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.testdata.TestdataCreator;
import com.bfh.domi.order.order.dto.*;
import com.bfh.domi.order.order.exception.BookNotFoundException;
import com.bfh.domi.order.order.integration.catalog.CatalogClient;
import com.bfh.domi.order.order.integration.shipping.ShippingClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data-04-orders.sql")
class OrderControllerIT {

    @MockitoBean
    private CatalogClient catalogClient;
    @MockitoBean
    private ShippingClient shippingClient;

    private static final String BASE_PATH = "/orders";
    private final RestClient restClient;

    OrderControllerIT(@LocalServerPort int port) {
        restClient = RestClient.create("http://localhost:" + port);
    }

    Long CUSTOMER_ID = TestdataCreator.getCustomerId();
    Long ORDER_ID = TestdataCreator.getOrderId();

    private String loadJsonFromFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("requests/" + fileName);
        return Files.readString(resource.getFile().toPath());
    }

    @Test
    void placeOrder() throws BookNotFoundException, IOException {
        Mockito.when(catalogClient.findBook(any())).thenReturn(TestdataCreator.getNewBook());
        Mockito.doNothing().when(shippingClient).sendShippingOrder(any());

        String requestBody = loadJsonFromFile("placeOrder.json");

        ResponseEntity<SalesOrder> response = restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().customer().id()).isEqualTo(CUSTOMER_ID);
        assertThat(response.getBody().items()).hasSize(1);
    }

    @Test
    void searchOrders() {
        int YEAR = 2024;

        ResponseEntity<OrderInfo[]> response = restClient.get().uri(uriBuilder ->
                        uriBuilder.path(BASE_PATH)
                                .queryParam("customerId", CUSTOMER_ID)
                                .queryParam("year", YEAR)
                                .build())
                .retrieve()
                .toEntity(OrderInfo[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()[0].date().getYear()).isEqualTo(YEAR);
    }

    @Test
    void findOrder() {
        ResponseEntity<SalesOrder> response = restClient.get().uri(BASE_PATH + "/" + ORDER_ID)
                .retrieve()
                .toEntity(SalesOrder.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(ORDER_ID);
    }

    @Test
    void cancelOrder() {
        ResponseEntity<Void> response = restClient.patch().uri(BASE_PATH + "/" + ORDER_ID)
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void placeOrder_whenBookNotFound_returns422() throws BookNotFoundException, IOException {
        Mockito.when(catalogClient.findBook(any())).thenThrow(new BookNotFoundException("Book not found"));

        String requestBody = loadJsonFromFile("placeOrderBookNotFound.json");

        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("422");
    }

    @Test
    void placeOrder_whenCustomerNotFound_returns422() throws BookNotFoundException, IOException {
        Mockito.when(catalogClient.findBook(any())).thenReturn(TestdataCreator.getNewBookMinimal());

        String requestBody = loadJsonFromFile("placeOrderCustomerNotFound.json");
        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("422");
    }

    @Test
    void placeOrder_whenPaymentFails_returns422() throws BookNotFoundException, IOException {
        Mockito.when(catalogClient.findBook(any())).thenReturn(TestdataCreator.getNewBookMinimal());

        // Simulate payment failure in service layer
        String requestBody = loadJsonFromFile("placeOrderPaymentFails.json");
        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("422");
    }

    @Test
    void placeOrder_whenInvalidRequest_returns400() throws IOException {
        String requestBody = loadJsonFromFile("placeOrderInvalid.json");
        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("400");
    }

    @Test
    void searchOrders_whenInvalidYear_returns400() {
        assertThatThrownBy(() -> restClient.get().uri(uriBuilder ->
                        uriBuilder.path(BASE_PATH)
                                .queryParam("customerId", CUSTOMER_ID)
                                .queryParam("year", 1999) // Below minimum
                                .build())
                .retrieve()
                .toEntity(OrderInfo[].class))
                .hasMessageContaining("400");
    }

    @Test
    void findOrder_whenOrderNotFound_returns404() {
        Long INVALID_ORDER_ID = 999999L;
        assertThatThrownBy(() -> restClient.get().uri(BASE_PATH + "/" + INVALID_ORDER_ID)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("404");
    }

    @Test
    void cancelOrder_whenOrderNotFound_returns404() {
        Long INVALID_ORDER_ID = 999999L;
        assertThatThrownBy(() -> restClient.patch().uri(BASE_PATH + "/" + INVALID_ORDER_ID)
                .retrieve()
                .toBodilessEntity())
                .hasMessageContaining("404");
    }

    @Test
    @Sql("/test-data-50-orders.sql")
    void cancelOrder_whenOrderAlreadyShipped_returns409() {
        int SHIPPED_ORDER_ID = 100000; // Assuming this order is already shipped in test data
        assertThatThrownBy(() -> restClient.patch().uri(BASE_PATH + "/" + SHIPPED_ORDER_ID)
                .retrieve()
                .toBodilessEntity())
                .hasMessageContaining("409");
    }

    @Test
    void placeOrder_whenIsbnTooShort_returns400() throws IOException {
        String requestBody = loadJsonFromFile("placeOrderIsbnTooShort.json");
        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("400");
    }

    @Test
    void placeOrder_whenIsbnTooLong_returns400() throws IOException {
        String requestBody = loadJsonFromFile("placeOrderIsbnTooLong.json");
        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("400");
    }

    @Test
    void placeOrder_whenIsbnBlank_returns400() throws IOException {
        String requestBody = loadJsonFromFile("placeOrderIsbnBlank.json");
        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("400");
    }

    @Test
    void placeOrder_whenQuantityZero_returns400() throws IOException {
        String requestBody = loadJsonFromFile("placeOrderQuantityZero.json");
        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("400");
    }

    @Test
    void placeOrder_whenQuantityNegative_returns400() throws IOException {
        String requestBody = loadJsonFromFile("placeOrderQuantityNegative.json");
        assertThatThrownBy(() -> restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class))
                .hasMessageContaining("400");
    }

    @Test
    void placeOrder_withMultipleItems_success() throws BookNotFoundException, IOException {
        Mockito.when(catalogClient.findBook(TestdataCreator.getMultipleBooks1().getIsbn())).thenReturn(TestdataCreator.getMultipleBooks1());
        Mockito.when(catalogClient.findBook(TestdataCreator.getMultipleBooks2().getIsbn())).thenReturn(TestdataCreator.getMultipleBooks2());
        Mockito.doNothing().when(shippingClient).sendShippingOrder(any());

        String requestBody = loadJsonFromFile("placeOrderMultipleItems.json");

        ResponseEntity<SalesOrder> response = restClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(SalesOrder.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().customer().id()).isEqualTo(CUSTOMER_ID);
        assertThat(response.getBody().items()).hasSize(2);
    }

    @Test
    void searchOrders_whenNoResults_returnsEmptyArray() {
        Long NON_EXISTENT_CUSTOMER_ID = TestdataCreator.getCustomerIdNotExistent();
        int YEAR = 2024;

        ResponseEntity<OrderInfo[]> response = restClient.get().uri(uriBuilder ->
                        uriBuilder.path(BASE_PATH)
                                .queryParam("customerId", NON_EXISTENT_CUSTOMER_ID)
                                .queryParam("year", YEAR)
                                .build())
                .retrieve()
                .toEntity(OrderInfo[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void searchOrders_whenMissingCustomerId_returns400() {
        assertThatThrownBy(() -> restClient.get().uri(uriBuilder ->
                        uriBuilder.path(BASE_PATH)
                                .queryParam("year", 2024)
                                .build())
                .retrieve()
                .toEntity(OrderInfo[].class))
                .hasMessageContaining("400");
    }

    @Test
    void searchOrders_whenMissingYear_returns400() {
        assertThatThrownBy(() -> restClient.get().uri(uriBuilder ->
                        uriBuilder.path(BASE_PATH)
                                .queryParam("customerId", CUSTOMER_ID)
                                .build())
                .retrieve()
                .toEntity(OrderInfo[].class))
                .hasMessageContaining("400");
    }
}