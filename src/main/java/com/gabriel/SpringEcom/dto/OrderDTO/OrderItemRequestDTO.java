package com.gabriel.SpringEcom.dto.OrderDTO;

import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
        Long productId,

        @NotNull
        Integer quantity
) {}
