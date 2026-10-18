package com.gabriel.SpringEcom.dto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(
        @NotBlank
        String oldPassword,

        @NotBlank
        @Size(min = 6)
        String newPassword
) {}
