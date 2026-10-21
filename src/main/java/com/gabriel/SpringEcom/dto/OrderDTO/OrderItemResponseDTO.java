package com.gabriel.SpringEcom.dto.OrderDTO;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        String productName,
        BigDecimal totalPrice,
        Integer quantity
) {
}
