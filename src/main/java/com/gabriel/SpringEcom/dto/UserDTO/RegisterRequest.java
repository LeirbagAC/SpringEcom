package com.gabriel.SpringEcom.dto.UserDTO;

public record RegisterRequest(
        String username,
        String email,
        String password
) {}
