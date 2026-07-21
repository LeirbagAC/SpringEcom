package com.gabriel.SpringEcom.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        boolean productAvailable,
        List<ProductImageDTO> images
) {}
