package com.gabriel.SpringEcom.controller.usersControllers;

import com.gabriel.SpringEcom.security.UserPrincipal;
import com.gabriel.SpringEcom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/user")
@RequiredArgsConstructor
public class userController {

    private final UserService userService;

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.deleteCurrentUser(userPrincipal.getUser());
        return ResponseEntity.noContent().build();
    }

}
