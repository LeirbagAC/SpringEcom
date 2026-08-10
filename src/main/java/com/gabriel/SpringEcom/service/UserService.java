package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

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
}
