package com.gabriel.SpringEcom.model.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productName,
        BigDecimal totalPrice,
        int quantity
) {
}
