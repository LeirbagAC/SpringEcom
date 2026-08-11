package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.UserDTO.AuthResponseDTO;
import com.gabriel.SpringEcom.dto.UserDTO.LoginRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.RegisterRequestDTO;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.repo.UserRepo;
import com.gabriel.SpringEcom.security.JwtService;
import com.gabriel.SpringEcom.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request, Role role) {
        if(userRepo.existsByEmail(request.email())) throw new RuntimeException("Email já cadastrado no sistema.");

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setActive(true);

        userRepo.save(user);

        String token = jwtService.generateToken(new UserPrincipal(user));
        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(userPrincipal);
        return new AuthResponseDTO(token);
    }

}
