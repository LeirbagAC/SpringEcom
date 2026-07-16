package com.gabriel.SpringEcom.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productName,
        BigDecimal totalPrice,
        int quantity
) {
}
