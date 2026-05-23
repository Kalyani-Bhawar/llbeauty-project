package com.llbeauty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String userName;
    private String userMobile;
    private String serviceName;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String status; // PENDING, CONFIRMED, CANCELLED

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}
