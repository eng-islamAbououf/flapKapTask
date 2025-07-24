package com.example.productservice.service.imp;

import com.example.productservice.model.dto.ProductDTO;
import com.example.productservice.model.entity.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.pub.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public void decreaseStock(Long productId, int amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getAmountAvailable() < amount) {
            throw new RuntimeException("Not enough stock available");
        }

        product.setAmountAvailable(product.getAmountAvailable() - amount);
        productRepository.save(product);
    }

    @Override
    public ProductDTO create(ProductDTO dto, Long sellerId) {
        Product product = Product.builder()
                .productName(dto.getProductName())
                .cost(dto.getCost())
                .amountAvailable(dto.getAmountAvailable())
                .sellerId(sellerId)  // Always set from token
                .build();
        product = productRepository.save(product);
        return toDTO(product);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto, Long sellerId) {
        Product product = findOwnedProduct(id, sellerId);

        product.setProductName(dto.getProductName());
        product.setCost(dto.getCost());
        product.setAmountAvailable(dto.getAmountAvailable());

        return toDTO(productRepository.save(product));
    }

    @Override
    public void delete(Long id, Long sellerId) {
        Product product = findOwnedProduct(id, sellerId);
        productRepository.delete(product);
    }

    @Override
    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ProductDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return toDTO(product);
    }

    private Product findOwnedProduct(Long id, Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        if (!product.getSellerId().equals(sellerId)) {
            throw new SecurityException("Unauthorized access: not your product");
        }
        return product;
    }

    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .cost(product.getCost())
                .amountAvailable(product.getAmountAvailable())
                .sellerId(product.getSellerId())
                .build();
    }
}
