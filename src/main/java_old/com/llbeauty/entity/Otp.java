package com.llbeauty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used;

    @PrePersist
    public void prePersist() {
        this.used = false;
    }
}
