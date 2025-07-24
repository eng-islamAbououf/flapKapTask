package com.example.productservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int cost;  // Stored in cents (5, 10, 20, 50, 100)

    @Column(nullable = false)
    private int amountAvailable;

    @Column(nullable = false)
    private Long sellerId;
}