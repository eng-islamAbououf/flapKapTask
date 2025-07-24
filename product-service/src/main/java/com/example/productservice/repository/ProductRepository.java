package com.example.productservice.repository;

import com.example.productservice.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Get all products created by a specific seller
    List<Product> findAllBySellerId(Long sellerId);
}