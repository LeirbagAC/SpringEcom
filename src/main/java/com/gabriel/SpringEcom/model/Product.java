package com.gabriel.SpringEcom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private String category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date releaseDate;
    private boolean productAvailable;
    private int stockQuantity;
    //Para Imagens
    private String imageName;
    private String imageType;
    @Lob //Large data
    private byte[] imageData;

    public Product(int id) {
        this.id = id;
    }
}

/*
                                                        IMPORTANTE!!
O Perigo na Vida Real: Nunca se usa @Data junto com @Entity! O problema está no @EqualsAndHashCode e no @ToString que o @Data gera.
Eles vão tentar ler todos os atributos da classe. Se a classe tiver relacionamentos com outras tabelas (como um Cliente que tem uma lista de Pedidos),
o Lombok vai tentar ler os pedidos. Isso pode causar o infame erro de StackOverflow (loop infinito) ou forçar o banco de dados a fazer dezenas de consultas desnecessárias,
matando a performance da sua aplicação.*/