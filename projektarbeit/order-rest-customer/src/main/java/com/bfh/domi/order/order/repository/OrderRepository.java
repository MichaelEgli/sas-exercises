package com.bfh.domi.order.order.repository;

import com.bfh.domi.order.order.dto.OrderStatistics;
import com.bfh.domi.order.order.dto.OrderInfo;
import com.bfh.domi.order.order.model.Order;
import com.bfh.domi.order.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Query 5: Finde die Bestellung (Order) mit einer bestimmten Nummer
    Optional<Order> findOrderById(Long orderId);

    // Query 6: Finde Informationen zu allen Bestellungen (OrderInfo) eines bestimmten Kunden
    // in einem bestimmten Zeitraum (Datum von/bis)
    List<OrderInfo> findAllByCustomerIdAndDateBetween(Long customerId, LocalDateTime start, LocalDateTime end);

    // Query 7: Schreibe eine Abfrage die den Bestellungstotalpreis, die Anzahl Positionen
    // sowie den Durchschnittsbetrag der Positions von allen Bestellungen gruppiert nach Jahr und Kunde zurückgibt
    @Query("SELECT EXTRACT(YEAR FROM o.date) AS year, " +
            "c.id AS customerId, " +
            "CONCAT(c.firstName, ' ', c.lastName) AS customerName, " +
            "SUM(DISTINCT o.amount) AS totalOrderPrice, " +
            "COUNT(oi.id) AS itemCount, " +
            "ROUND(AVG(oi.book.price * oi.quantity), 1) AS averageItemAmount " +
            "FROM Order o " +
            "JOIN o.customer c " +
            "JOIN o.orderItems oi " +
            "GROUP BY EXTRACT(YEAR FROM o.date), c.id, c.firstName, c.lastName " +
            "ORDER BY year DESC, customerName")
    List<OrderStatistics> getOrderStatisticsByYearAndCustomer();

    @Modifying
    @Query("UPDATE Order o SET o.status = :orderStatus WHERE o.id = :orderId")
    int updateOrderStatus(Long orderId, OrderStatus orderStatus);


}
