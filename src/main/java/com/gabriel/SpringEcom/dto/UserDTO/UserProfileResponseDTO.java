package com.gabriel.SpringEcom.dto.UserDTO;

import com.gabriel.SpringEcom.model.enums.Role;

import java.util.UUID;

public record UserProfileResponseDTO(
        UUID id,
        String username,
        String email,
        Role role,
        boolean active
) {}
