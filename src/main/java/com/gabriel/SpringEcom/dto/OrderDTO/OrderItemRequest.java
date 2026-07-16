package com.gabriel.SpringEcom.dto.OrderDTO;

public record OrderItemRequest(
        int productId,
        int quantity
) {}
