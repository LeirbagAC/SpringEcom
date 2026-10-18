package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponse;
import com.gabriel.SpringEcom.dto.UserDTO.ChangePasswordRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.RegisterRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileResponseDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileUpdateRequestDTO;
import com.gabriel.SpringEcom.exceptions.BusinessRuleException;
import com.gabriel.SpringEcom.exceptions.EmailAlreadyInUseException;
import com.gabriel.SpringEcom.exceptions.ForbiddenOperationException;
import com.gabriel.SpringEcom.exceptions.NotFoundException;
import com.gabriel.SpringEcom.mappers.OrderMapper;
import com.gabriel.SpringEcom.mappers.ProfileMapper;
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
        return profileMapper.mapToProfileResponse(getUserById(id));
    }

    @Transactional
    public User updateProfile(Long userId, UserProfileUpdateRequestDTO request) {
        User user = getUserById(userId);

        if (!user.getEmail().equals(request.email()) && userRepo.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyInUseException();
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        return user;
    }

    @Transactional
    public void deleteCurrentUser(User loggedUser) {
        deactivate(getUserById(loggedUser.getId()));
    }


    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDTO request) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) throw new BusinessRuleException("Senha atual incorreta.");
        if(request.oldPassword().equals(request.newPassword())) throw new BusinessRuleException("A nova senha não pode ser igual à senha atual.");

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    // --- ADMIN SERVICES

    @Transactional
    public void changeRole(UUID externalId, Role newRole) {
        User user = getUserByExternalId(externalId);
        if (user.getRole() == Role.ADMIN) throw new ForbiddenOperationException("Não é permitido alterar o role de um Administrador.");
        user.setRole(newRole);
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponseDTO> searchUsers(String keyword) {
        return profileMapper.toProfileResponseList(userRepo.searchByKeyword(keyword));

    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersByUser(UUID externalId) {
        if (!userRepo.existsByExternalId(externalId)) {
            throw new NotFoundException("Usuário", externalId);
        }
        return orderMapper.toOrderResponseList(orderRepo.findAllByUserExternalId(externalId));
    }

    @Transactional
    public UserProfileResponseDTO createUser(RegisterRequestDTO request) {
        if (userRepo.existsByEmail(request.email())) throw new EmailAlreadyInUseException();

        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode("SenhaPadrão123"));

        User savedUser =  userRepo.save(newUser);
        return profileMapper.mapToProfileResponse(savedUser);
    }

    @Transactional
    public void deleteUserById(UUID externalId) {
        User user = getUserByExternalId(externalId);

        if (user.getRole() == Role.ADMIN) throw new ForbiddenOperationException("Não é permitido deletar um Administrador.");

        user.setActive(false);
        user.getProducts().forEach(product -> product.setActive(false));
    }

    //Métodos auxiliares

    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário", id));
    }

    private User getUserByExternalId(UUID externalId) {
        return userRepo.findByExternalId(externalId)
                .orElseThrow(() -> new NotFoundException("Usuário", externalId));
    }

    private void deactivate(User user) {
        user.setActive(false);
        user.getProducts().forEach(product -> product.setActive(false));
    }
}
