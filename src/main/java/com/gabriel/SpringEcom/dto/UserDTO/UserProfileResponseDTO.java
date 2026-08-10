package com.gabriel.SpringEcom.dto.UserDTO;

import com.gabriel.SpringEcom.model.enums.Role;

public record UserProfileResponseDTO(
        Long id,
        String username,
        String email,
        Role role,
        boolean active
) {}
