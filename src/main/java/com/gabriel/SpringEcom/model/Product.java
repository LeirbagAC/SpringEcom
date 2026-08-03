package com.gabriel.SpringEcom.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private String category;
    private LocalDate releaseDate;
    private boolean productAvailable;
    private int stockQuantity;
    private boolean active =  true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    //Esses callbacks só são executados quando a entidade é persistida pelo JPA/Hibernate, mas uma atualização direta por JPQL ou SQL
    @PrePersist
    @PreUpdate
    public void syncAvailability() {
        this.productAvailable = this.stockQuantity > 0;
    }
}