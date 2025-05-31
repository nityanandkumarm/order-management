package com.order.management.service.impl;

import com.order.management.dto.ProductDTO;
import com.order.management.exception.ResourceNotFoundException;
import com.order.management.model.Product;
import com.order.management.repository.ProductRepository;
import com.order.management.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements product‐related business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductDTO addProduct(ProductDTO dto) {
        log.info("Attempting to add new product: name={}, price={}, stock={}",
                 dto.getName(), dto.getPrice(), dto.getStock());
        try {
            Product product = Product.builder()
                    .name(dto.getName())
                    .price(dto.getPrice())
                    .stock(dto.getStock())
                    .build();

            Product saved = productRepository.save(product);
            log.info("Successfully added product with id={}", saved.getId());
            return toDTO(saved);
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while adding product: {}",
                      ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while adding product: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        log.info("Updating product with id={}", id);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());

        try {
            Product updated = productRepository.save(existing);
            log.info("Successfully updated product with id={}", updated.getId());
            return toDTO(updated);
        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while updating product id={}: {}",
                      id, ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while updating product id={}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ProductDTO> getAllProducts() {
        log.debug("Fetching all products from repository");
        List<ProductDTO> list = productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        log.debug("Found {} products", list.size());
        return list;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ProductDTO getProductById(Long id) {
        log.debug("Fetching product by id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
        log.debug("Found product: {}", product);
        return toDTO(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id={}", id);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
        productRepository.delete(existing);
        log.info("Deleted product with id={}", id);
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }
}
