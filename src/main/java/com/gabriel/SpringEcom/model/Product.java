package com.gabriel.SpringEcom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private String category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date releaseDate;
    private boolean productAvailable;
    private int stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,  orphanRemoval = true)
    @JsonManagedReference //Sem isso e o JsonIgore do lado da imagem vai ficar um loop infinito
    private List<ProductImage> images = new ArrayList<>();
}