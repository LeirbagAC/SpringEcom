package com.gabriel.SpringEcom.dto.UserDTO;

import com.gabriel.SpringEcom.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank @Size(min = 3, max = 30) String username,
        @NotBlank @Email String email,
        @NotNull Role role,
        @NotBlank @Size(min = 6) String password
) {}
