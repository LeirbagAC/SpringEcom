package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.UserDTO.ChangePasswordRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileResponseDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileUpdateRequestDTO;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder  passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileResponseDTO getProfile(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
        return mapToProfileResponse(user);
    }

    @Transactional
    public User updateProfile(Long userId, UserProfileUpdateRequestDTO request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!user.getEmail().equals(request.email()) && userRepo.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso por outra conta.");
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        return user;
    }

    @Transactional
    public void deleteCurrentUser(User loggedUser) {
        User user = userRepo.findById(loggedUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + loggedUser.getId()));

        user.setActive(false);
        user.getProducts().forEach(product -> product.setActive(false));
    }

    @Transactional
    public void changeRole(Long id, Role newRole) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));

        user.setRole(newRole);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDTO request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) throw new RuntimeException("Senha atual incorreta.");
        if(request.oldPassword().equals(request.newPassword())) throw new RuntimeException("A nova senha não pode ser igual à senha atual.");

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    private UserProfileResponseDTO mapToProfileResponse(User user) {
        return new UserProfileResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }
}
