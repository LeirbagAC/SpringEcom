package com.gabriel.SpringEcom.dto.ProductDTO;

import com.gabriel.SpringEcom.dto.UserDTO.SellerSummaryDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean productAvailable,
        SellerSummaryDTO seller,
        List<ProductImageDTO> images
) {}
