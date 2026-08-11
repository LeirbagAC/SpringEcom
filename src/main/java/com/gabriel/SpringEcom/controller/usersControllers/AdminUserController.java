package com.gabriel.SpringEcom.controller.usersControllers;

import com.gabriel.SpringEcom.model.enums.ChangeRoleDTO;
import com.gabriel.SpringEcom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PatchMapping("/{externalId}/role")
    public ResponseEntity<Void> changeUserRole(@PathVariable UUID externalId, @RequestBody ChangeRoleDTO request) {
        userService.changeRole(externalId, request.newRole());
        return ResponseEntity.noContent().build();
    }

}
