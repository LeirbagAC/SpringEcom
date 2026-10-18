package com.gabriel.SpringEcom.dto.ProductDTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank
        @Size(max = 100)
        String name,

        String description,

        @NotBlank
        String brand,

        @NotNull
        @Positive
        BigDecimal price,

        @NotBlank
        String category,

        @NotNull
        @PositiveOrZero
        Integer stockQuantity
) {}
