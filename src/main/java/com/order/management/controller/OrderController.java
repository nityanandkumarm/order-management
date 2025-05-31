package com.order.management.controller;

import com.order.management.dto.OrderRequestDTO;
import com.order.management.dto.OrderResponseDTO;
import com.order.management.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @Valid @RequestBody OrderRequestDTO dto) {

        log.info("POST /api/orders - payload: {}", dto);
        OrderResponseDTO created = orderService.placeOrder(dto);
        log.info("POST /api/orders - created order id={}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCustomer(
            @PathVariable Long customerId) {

        log.debug("GET /api/orders/customer/{}", customerId);
        List<OrderResponseDTO> orders = orderService.getOrdersByCustomerId(customerId);
        log.debug("GET /api/orders/customer/{} - returning {} orders",
                  customerId, orders.size());
        return ResponseEntity.ok(orders);
    }
}
