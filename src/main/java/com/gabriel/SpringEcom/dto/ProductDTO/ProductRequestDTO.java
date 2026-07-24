package com.gabriel.SpringEcom.dto.ProductDTO;

import java.math.BigDecimal;

public record ProductRequestDTO(
        String name,
        String description,
        String brand,
        BigDecimal price,
        String category,
        boolean productAvailable,
        int stockQuantity
) {}
