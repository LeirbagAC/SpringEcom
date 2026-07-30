package com.gabriel.SpringEcom.dto.CartDTO;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDTO(
        String userName,
        String email,
        List<CartItemResponseDTO> items,
        BigDecimal totalPrice
) {}
