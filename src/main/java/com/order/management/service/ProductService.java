package com.order.management.service;

import com.order.management.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO addProduct(ProductDTO dto);
    ProductDTO updateProduct(Long id, ProductDTO dto);
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    void deleteProduct(Long id);
}
