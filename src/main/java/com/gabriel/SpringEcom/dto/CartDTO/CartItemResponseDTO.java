package com.gabriel.SpringEcom.dto.CartDTO;

import java.math.BigDecimal;

public record CartItemResponseDTO(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
//        ProductResponseDTO product //Comentei para deixar a response mais enxuta, mas se o front precisar da imagem da para adicionar a url aqui
) {}
