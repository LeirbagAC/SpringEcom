package com.gabriel.SpringEcom.dto.UserDTO;

public record LoginRequest(
        String email,
        String password
) {}
