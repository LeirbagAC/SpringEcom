package com.gabriel.SpringEcom.dto.OrderDTO;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productName,
        BigDecimal totalPrice,
        int quantity
) {
}
