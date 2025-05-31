package com.order.management.controller;

import com.order.management.dto.CustomerDTO;
import com.order.management.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  Exposes /api/customers endpoints. Uses SLF4J to log requests and responses.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDTO> addCustomer(@Valid @RequestBody CustomerDTO dto) {
        log.info("POST /api/customers - payload: {}", dto);
        CustomerDTO created = customerService.addCustomer(dto);
        log.info("POST /api/customers - created customer: id={}", created.getId());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO dto) {
        log.info("PUT /api/customers/{} - payload: {}", id, dto);
        CustomerDTO updated = customerService.updateCustomer(id, dto);
        log.info("PUT /api/customers/{} - success", id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAll() {
        log.debug("GET /api/customers");
        List<CustomerDTO> all = customerService.getAllCustomers();
        log.debug("GET /api/customers - returning {} customers", all.size());
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        log.debug("GET /api/customers/{}", id);
        CustomerDTO dto = customerService.getCustomerById(id);
        log.debug("GET /api/customers/{} - found: {}", id, dto);
        return ResponseEntity.ok(dto);
    }
}
