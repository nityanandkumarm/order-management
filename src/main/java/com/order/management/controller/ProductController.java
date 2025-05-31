package com.order.management.controller;

import com.order.management.dto.ProductDTO;
import com.order.management.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO dto) {
        log.info("POST /api/products - payload: {}", dto);
        ProductDTO created = productService.addProduct(dto);
        log.info("POST /api/products - created product id={}", created.getId());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {
        log.info("PUT /api/products/{} - payload: {}", id, dto);
        ProductDTO updated = productService.updateProduct(id, dto);
        log.info("PUT /api/products/{} - success", id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.debug("GET /api/products");
        List<ProductDTO> list = productService.getAllProducts();
        log.debug("GET /api/products - returning {} products", list.size());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        log.debug("GET /api/products/{}", id);
        ProductDTO dto = productService.getProductById(id);
        log.debug("GET /api/products/{} - found: {}", id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{}", id);
        productService.deleteProduct(id);
        log.info("DELETE /api/products/{} - success", id);
        return ResponseEntity.noContent().build();
    }
}
