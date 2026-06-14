package com.llbeauty.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "beauticians")
public class Beautician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "contact")
    private String contact;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE or INACTIVE

    public Beautician() {}

    public Beautician(String name, String specialization, String contact, String status) {
        this.name = name;
        this.specialization = specialization;
        this.contact = contact;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
