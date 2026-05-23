package com.llbeauty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String mobile;
    private String password;

    private Double walletBalance;

    private LocalDateTime createdAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.walletBalance == null) {
            this.walletBalance = 0.0;
        }
    }
}
