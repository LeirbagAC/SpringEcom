package com.gabriel.SpringEcom.controller.usersControllers;

import com.gabriel.SpringEcom.model.enums.ChangeRoleDTO;
import com.gabriel.SpringEcom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/admin/users")
@RequiredArgsConstructor
public class adminUserController {

    private final UserService userService;

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> changeUserRole(@PathVariable Long id, @RequestBody ChangeRoleDTO request) {
        userService.changeRole(id, request.newRole());
        return ResponseEntity.noContent().build();
    }

}
