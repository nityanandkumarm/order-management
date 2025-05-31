package com.order.management.service;

import com.order.management.dto.CustomerOrderCountDTO;
import com.order.management.dto.OrderRequestDTO;
import com.order.management.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO placeOrder(OrderRequestDTO dto);

    List<OrderResponseDTO> getOrdersByCustomerId(Long customerId);

    List<CustomerOrderCountDTO> getOrderCountPerCustomer();

    List<CustomerOrderCountDTO> getTopCustomers(int topN);
}
