package com.gabriel.SpringEcom.dto.CartDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDTO(
        @NotNull
        Long productId,

        @NotNull
        @Positive
        Integer quantity
) {}
