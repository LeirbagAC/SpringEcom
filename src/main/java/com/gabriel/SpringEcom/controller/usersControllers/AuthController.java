package com.gabriel.SpringEcom.controller.usersControllers;

import com.gabriel.SpringEcom.dto.UserDTO.AuthResponse;
import com.gabriel.SpringEcom.dto.UserDTO.LoginRequest;
import com.gabriel.SpringEcom.dto.UserDTO.RegisterRequest;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.repo.UserRepo;
import com.gabriel.SpringEcom.security.JwtService;
import com.gabriel.SpringEcom.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class  AuthController {

    private final UserRepo userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        String token = jwtService.generateToken(new UserPrincipal(user));
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/seller/register")
    public ResponseEntity<AuthResponse> sellerRegister(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.SELLER);
        userRepository.save(user);

        String token = jwtService.generateToken(new UserPrincipal(user));
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(userPrincipal);

        return ResponseEntity.ok(new AuthResponse(token));
    }

}