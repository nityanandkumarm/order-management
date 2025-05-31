package com.order.management.repository;

import com.order.management.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    @Query("""
        SELECT o.customer.id AS custId, o.customer.name AS custName, COUNT(o) AS cnt
         FROM Order o
         GROUP BY o.customer.id, o.customer.name
         ORDER BY o.customer.id
        """)
    List<Object[]> findOrderCountPerCustomer();

    @Query("""
        SELECT o.customer.id AS custId, o.customer.name AS custName, COUNT(o) AS cnt
          FROM Order o
         GROUP BY o.customer.id, o.customer.name
         ORDER BY cnt DESC
        """)
    List<Object[]> findTopCustomers(Pageable pageable);
}
