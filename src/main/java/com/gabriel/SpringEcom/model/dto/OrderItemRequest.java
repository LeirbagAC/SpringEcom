package com.gabriel.SpringEcom.model.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {}
