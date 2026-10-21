package com.gabriel.SpringEcom.dto.ProductDTO;

import com.gabriel.SpringEcom.dto.UserDTO.SellerSummaryResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean productAvailable,
        SellerSummaryResponseDTO seller,
        List<ProductImageResponseDTO> images
) {}
