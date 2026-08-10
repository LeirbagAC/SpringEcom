package com.gabriel.SpringEcom.dto.UserDTO;

public record UserProfileUpdateResponseDTO(
        UserProfileResponseDTO profile,
        String newToken
) {}