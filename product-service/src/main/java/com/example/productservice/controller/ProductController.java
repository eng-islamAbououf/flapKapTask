package com.example.productservice.controller;

import com.example.productservice.model.dto.ProductDTO;
import com.example.productservice.service.pub.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDTO> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO dto, HttpServletRequest request) {
        Long sellerId = extractSellerId(request);
        return ResponseEntity.ok(productService.create(dto, sellerId));
    }

    @PostMapping("/decrease")
    public ResponseEntity<Void> decreaseStock(
            @RequestParam Long productId,
            @RequestParam int amount
    ) {
        productService.decreaseStock(productId, amount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,
                                             @RequestBody ProductDTO dto,
                                             HttpServletRequest request) {
        Long sellerId = extractSellerId(request);
        return ResponseEntity.ok(productService.update(id, dto, sellerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long sellerId = extractSellerId(request);
        productService.delete(id, sellerId);
        return ResponseEntity.noContent().build();
    }

    private Long extractSellerId(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if (role == null || !role.equalsIgnoreCase("SELLER")) {
            throw new SecurityException("Only SELLER users can perform this operation.");
        }
        String userId = request.getHeader("X-User-Id");
        if (userId == null) {
            throw new SecurityException("Missing user ID header");
        }
        return Long.valueOf(userId);
    }
}
