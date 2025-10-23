package com.uteexpress.repository;

import com.uteexpress.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);
    List<Order> findBySenderName(String senderName);
    @Query("SELECT o FROM Order o WHERE o.customer.username = :username")
    List<Order> findByCustomerUsername(String username);
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByCustomerIdAndStatus(Long customerId, Order.OrderStatus status);
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByStatus(Order.OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(Order.OrderStatus status);
}
