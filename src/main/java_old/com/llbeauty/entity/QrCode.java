package com.llbeauty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    private String qrData;
    private String status; // ACTIVE, USED

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
