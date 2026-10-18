package com.gabriel.SpringEcom.dto.OrderDTO;

import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        Long productId,

        @NotNull
        Integer quantity
) {}
