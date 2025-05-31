package com.order.management.service;

import com.order.management.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    CustomerDTO addCustomer(CustomerDTO dto);
    CustomerDTO updateCustomer(Long id, CustomerDTO dto);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(Long id);
}
