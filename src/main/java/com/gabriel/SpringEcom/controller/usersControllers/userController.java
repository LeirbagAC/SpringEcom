package com.gabriel.SpringEcom.controller.usersControllers;

import com.gabriel.SpringEcom.dto.UserDTO.ChangePasswordRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileResponseDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileUpdateRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileUpdateResponseDTO;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.security.JwtService;
import com.gabriel.SpringEcom.security.UserPrincipal;
import com.gabriel.SpringEcom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController("/user/me")
@RequiredArgsConstructor
public class userController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<UserProfileResponseDTO> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponseDTO profile = userService.getProfile(userPrincipal.getUser().getId());
        return ResponseEntity.ok(profile);
    }

    //Não sei se essa é a forma correta para se fazer seguindo as boas práticas, mas o que eu quero é que quando o usuário atualizar o perfil,
    //ele receba um novo token JWT com as informações atualizadas para facilitar meus testes nessa simulação.
    @PutMapping
    public ResponseEntity<UserProfileUpdateResponseDTO> updateMyProfile(@RequestBody UserProfileUpdateRequestDTO request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User updatedUser = userService.updateProfile(userPrincipal.getUser().getId(), request);
        String newToken = jwtService.generateToken(new UserPrincipal(updatedUser));

        UserProfileResponseDTO profileResponse = new UserProfileResponseDTO(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                updatedUser.isActive()
        );

        return ResponseEntity.ok(new UserProfileUpdateResponseDTO(profileResponse, newToken));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequestDTO request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.changePassword(userPrincipal.getUser().getId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.deleteCurrentUser(userPrincipal.getUser());
        return ResponseEntity.noContent().build();
    }

}
