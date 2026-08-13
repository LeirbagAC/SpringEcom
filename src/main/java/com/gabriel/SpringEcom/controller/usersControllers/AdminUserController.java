package com.gabriel.SpringEcom.controller.usersControllers;

import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponse;
import com.gabriel.SpringEcom.dto.UserDTO.RegisterRequestDTO;
import com.gabriel.SpringEcom.dto.UserDTO.UserProfileResponseDTO;
import com.gabriel.SpringEcom.model.enums.ChangeRoleDTO;
import com.gabriel.SpringEcom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PatchMapping("/{externalId}/role")
    public ResponseEntity<Void> changeUserRole(@PathVariable UUID externalId, @RequestBody ChangeRoleDTO request) {
        userService.changeRole(externalId, request.newRole());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileResponseDTO>> searchUsers(@RequestParam String keyword) {
        List<UserProfileResponseDTO> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/orders/{externalId}")
    public ResponseEntity<List<OrderResponse>> getAllOrdersByUser(@PathVariable UUID externalId) {
        List<OrderResponse> orders = userService.getAllOrdersByUser(externalId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createUser(@RequestBody RegisterRequestDTO request) {
        UserProfileResponseDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID externalId) {
        userService.deleteUser(externalId);
        return ResponseEntity.noContent().build();
    }
}
