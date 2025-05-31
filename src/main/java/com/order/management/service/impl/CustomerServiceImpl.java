package com.order.management.service.impl;

import com.order.management.dto.CustomerDTO;
import com.order.management.exception.ResourceNotFoundException;
import com.order.management.model.Customer;
import com.order.management.repository.CustomerRepository;
import com.order.management.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements business logic for Customer operations.
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerDTO addCustomer(CustomerDTO dto) {
        log.info("Attempting to add new customer: name={}, email={}, phone={}",
                 dto.getName(), dto.getEmail(), dto.getPhone());
        try {
            Customer customer = Customer.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .phone(dto.getPhone())
                    .build();
            Customer saved = customerRepository.save(customer);
            log.info("Successfully added customer with id={}", saved.getId());
            return toDTO(saved);
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to add customer due to integrity violation: {}", ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while adding customer: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        log.info("Updating customer with id={}", id);
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        try {
            Customer updated = customerRepository.save(existing);
            log.info("Successfully updated customer with id={}", updated.getId());
            return toDTO(updated);
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to update customer id={} due to integrity violation: {}", id,  ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while updating customer id={}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        log.debug("Fetching all customers from database");
        List<CustomerDTO> list = customerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        log.debug("Found {} customers", list.size());
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        log.debug("Fetching customer by id={}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        log.debug("Found customer: {}", customer);
        return toDTO(customer);
    }

    private CustomerDTO toDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }
}
