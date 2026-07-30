package com.gabriel.SpringEcom.dto.ProductDTO;

import com.gabriel.SpringEcom.dto.UserDTO.UserResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean productAvailable,
        UserResponseDTO seller,
        List<ProductImageDTO> images
) {}
