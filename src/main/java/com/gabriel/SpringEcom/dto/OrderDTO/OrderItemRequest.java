package com.gabriel.SpringEcom.dto.OrderDTO;

public record OrderItemRequest(
        Long productId,
        int quantity
) {}
