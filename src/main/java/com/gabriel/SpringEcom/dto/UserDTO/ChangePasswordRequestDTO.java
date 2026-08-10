package com.gabriel.SpringEcom.dto.UserDTO;

public record ChangePasswordRequestDTO(
        String oldPassword,
        String newPassword
) {}
