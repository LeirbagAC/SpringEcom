package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.UserDTO.UserProfileResponseDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileUpdateRequestDTO;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

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
