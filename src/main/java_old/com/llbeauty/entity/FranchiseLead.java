package com.llbeauty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "franchise_leads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseLead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String mobile;
    private String email;
    private String city;
    private String budget;
    private String preferredLocation;
    private String franchiseType; // Kiosk, Studio, Lounge

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
