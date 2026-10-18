package com.gabriel.SpringEcom.dto.UserDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserProfileUpdateRequestDTO(
        @NotBlank
        String username,

        @NotBlank
        @Email
        String email
) {}