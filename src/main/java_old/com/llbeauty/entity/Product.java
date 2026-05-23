package com.llbeauty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category; // Perfumes, Skincare, Haircare, Spa/Detox, Accessories
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Double price;
    private String imageUrl;
    private Integer stock;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
