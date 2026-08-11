package com.gabriel.SpringEcom.controller.usersControllers;

import com.gabriel.SpringEcom.dto.UserDTO.AuthResponseDTO;
import com.gabriel.SpringEcom.dto.UserDTO.LoginRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.RegisterRequestDTO;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class  AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request, Role.USER));
    }

    @PostMapping("/seller/register")
    public ResponseEntity<AuthResponseDTO> sellerRegister(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request, Role.SELLER));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

}