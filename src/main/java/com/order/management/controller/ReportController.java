package com.order.management.controller;

import com.order.management.dto.CustomerOrderCountDTO;
import com.order.management.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final OrderService orderService;

    @GetMapping("/orders-per-customer")
    public ResponseEntity<List<CustomerOrderCountDTO>> getOrderCountPerCustomer() {
        log.info("GET /api/reports/orders-per-customer");
        List<CustomerOrderCountDTO> report = orderService.getOrderCountPerCustomer();
        log.info("Returning {} rows in orders-per-customer report", report.size());
        return ResponseEntity.ok(report);
    }

    @GetMapping("/top-five-customers")
    public ResponseEntity<List<CustomerOrderCountDTO>> getTopFiveCustomers() {
        log.info("GET /api/reports/top-5-customers");
        List<CustomerOrderCountDTO> topOrders = orderService.getTopCustomers(5);
        log.info("Returning top {} customers by order count", topOrders.size());
        return ResponseEntity.ok(topOrders);
    }
}
