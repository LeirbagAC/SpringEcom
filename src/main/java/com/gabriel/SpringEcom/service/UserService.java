package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponse;
import com.gabriel.SpringEcom.dto.UserDTO.ChangePasswordRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.RegisterRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileResponseDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileUpdateRequestDTO;
import com.gabriel.SpringEcom.mappers.OrderMapper;
import com.gabriel.SpringEcom.mappers.ProfileMapper;
import com.gabriel.SpringEcom.model.Order;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.repo.OrderRepo;
import com.gabriel.SpringEcom.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder  passwordEncoder;
    private  final OrderRepo orderRepo;
    private  final OrderMapper orderMapper;
    private  final ProfileMapper profileMapper;

    @Transactional(readOnly = true)
    public UserProfileResponseDTO getProfile(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return profileMapper.mapToProfileResponse(user);
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
    public void changePassword(Long userId, ChangePasswordRequestDTO request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) throw new RuntimeException("Senha atual incorreta.");
        if(request.oldPassword().equals(request.newPassword())) throw new RuntimeException("A nova senha não pode ser igual à senha atual.");

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    // --- ADMIN SERVICES

    @Transactional
    public void changeRole(UUID externalId, Role newRole) {
        User user = userRepo.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.getRole() == Role.ADMIN) throw new RuntimeException("Não é permitido alterar o role de um Administrador.");

        user.setRole(newRole);
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponseDTO> searchUsers(String keyword) {
        List<User> users = userRepo.searchByKeyword(keyword);
        return profileMapper.toProfileResponseList(users);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersByUser(UUID externalId) {
        User user = userRepo.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        List<Order> orders = orderRepo.findAllByUserExternalId(externalId);

        return orderMapper.toOrderResponseList(orders);
    }

    @Transactional
    public UserProfileResponseDTO createUser(RegisterRequestDTO request) {
        if (userRepo.existsByEmail(request.email())) throw new RuntimeException("Este e-mail já está em uso por outra conta.");

        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode("SenhaPadrão123"));

        User savedUser =  userRepo.save(newUser);
        return profileMapper.mapToProfileResponse(savedUser);
    }

    @Transactional
    public void deleteUser(UUID externalId) {
        User user = userRepo.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.getRole() == Role.ADMIN) throw new RuntimeException("Não é permitido deletar um Administrador.");

        user.setActive(false);
        user.getProducts().forEach(product -> product.setActive(false));
    }
}
