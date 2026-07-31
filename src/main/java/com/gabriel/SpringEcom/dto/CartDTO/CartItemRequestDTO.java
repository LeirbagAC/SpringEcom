package com.gabriel.SpringEcom.dto.CartDTO;

public record CartItemRequestDTO(
        Long productId,
        int quantity
) {}
