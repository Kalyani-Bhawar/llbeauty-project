package com.llbeauty.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "salon_services")
public class SalonService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;
    private Integer durationMinutes;
    private String imageUrl;

    public SalonService() {}

    public SalonService(Long id, String name, String description, Double price, Integer durationMinutes, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Builder Pattern matching the style of the project
    public static class SalonServiceBuilder {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private Integer durationMinutes;
        private String imageUrl;

        SalonServiceBuilder() {}

        public SalonServiceBuilder id(Long id) { this.id = id; return this; }
        public SalonServiceBuilder name(String name) { this.name = name; return this; }
        public SalonServiceBuilder description(String description) { this.description = description; return this; }
        public SalonServiceBuilder price(Double price) { this.price = price; return this; }
        public SalonServiceBuilder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public SalonServiceBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public SalonService build() {
            return new SalonService(id, name, description, price, durationMinutes, imageUrl);
        }
    }

    public static SalonServiceBuilder builder() {
        return new SalonServiceBuilder();
    }
}
