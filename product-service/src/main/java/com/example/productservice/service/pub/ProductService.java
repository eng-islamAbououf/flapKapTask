package com.example.productservice.service.pub;


import com.example.productservice.model.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    ProductDTO create(ProductDTO dto, Long sellerId);

    ProductDTO update(Long id, ProductDTO dto, Long sellerId);

    void delete(Long id, Long sellerId);

    List<ProductDTO> getAll();

    ProductDTO getById(Long id);

    // ProductService.java
    void decreaseStock(Long productId, int amount);

}