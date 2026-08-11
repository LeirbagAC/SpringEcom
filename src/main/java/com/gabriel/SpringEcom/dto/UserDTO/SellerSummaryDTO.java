package com.gabriel.SpringEcom.dto.UserDTO;

import java.util.UUID;

public record SellerSummaryDTO(
        UUID id,
        String name,
        String email
) {}