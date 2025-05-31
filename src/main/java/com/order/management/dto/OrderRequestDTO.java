package com.order.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotEmpty(message = "At least one product must be included in the order")
    @Valid
    private List<OrderItemRequestDTO> items;
}
