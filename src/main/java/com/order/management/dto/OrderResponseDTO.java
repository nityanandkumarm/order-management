package com.order.management.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private Long customerId;
    private Instant orderDate;
    private List<OrderItemResponseDTO> items;
    private BigDecimal totalAmount;
}
