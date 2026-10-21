package com.gabriel.SpringEcom.dto.ProductDTO;

public record ProductImageResponseDTO(
        Long id,
        String imageName,
        String imageType,
        String imageUrl
) {}
