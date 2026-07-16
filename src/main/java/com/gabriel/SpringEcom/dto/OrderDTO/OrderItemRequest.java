package com.gabriel.SpringEcom.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {}
