package com.order.management.service.impl;

import com.order.management.dto.*;
import com.order.management.exception.InsufficientStockException;
import com.order.management.exception.ResourceNotFoundException;
import com.order.management.model.*;
import com.order.management.repository.*;
import com.order.management.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO dto) {
        log.info("Placing order for customerId={} with {} items",
                 dto.getCustomerId(), dto.getItems().size());

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id " + dto.getCustomerId()));

        Order order = Order.builder()
                .customer(customer)
                .build();

        for (OrderItemRequestDTO itemReq : dto.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id " + itemReq.getProductId()));

            int requestedQty = itemReq.getQuantity();
            if (product.getStock() < requestedQty) {
                String errMsg = String.format(
                        "Insufficient stock for productId=%d: requested=%d, available=%d",
                        product.getId(), requestedQty, product.getStock());
                log.warn(errMsg);
                throw new InsufficientStockException(errMsg);
            }

            product.setStock(product.getStock() - requestedQty);
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(requestedQty)
                    .unitPrice(product.getPrice())
                    .build();

            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with orderId={}", savedOrder.getId());

        return toResponseDTO(savedOrder);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByCustomerId(Long customerId) {
        log.debug("Fetching orders for customerId={}", customerId);

        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id " + customerId));

        List<Order> orders = orderRepository.findByCustomerId(customerId);
        log.debug("Found {} orders for customerId={}", orders.size(), customerId);

        return orders.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private OrderResponseDTO toResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderItemResponseDTO.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalAmount = itemDTOs.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderResponseDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .orderDate(order.getOrderDate())
                .items(itemDTOs)
                .totalAmount(totalAmount)
                .build();
    }

    @Override
    public List<CustomerOrderCountDTO> getOrderCountPerCustomer() {
        log.debug("Fetching total order count per customer");
        List<Object[]> rows = orderRepository.findOrderCountPerCustomer();
        return rows.stream()
                .map(row -> new CustomerOrderCountDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerOrderCountDTO> getTopCustomers(int topN) {
        log.debug("Fetching top {} customers by order count", topN);
        PageRequest pageReq = PageRequest.of(0, topN);
        List<Object[]> rows = orderRepository.findTopCustomers(pageReq);
        return rows.stream()
                .map(row -> new CustomerOrderCountDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()))
                .collect(Collectors.toList());
    }
}
