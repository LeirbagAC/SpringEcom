package com.gabriel.SpringEcom.controller;

import com.gabriel.SpringEcom.dto.CartDTO.CartItemRequestDTO;
import com.gabriel.SpringEcom.dto.CartDTO.CartResponseDTO;
import com.gabriel.SpringEcom.security.UserPrincipal;
import com.gabriel.SpringEcom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add/")
    public ResponseEntity<CartResponseDTO> addProductToCart(@RequestBody CartItemRequestDTO request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponseDTO updatedCart = cartService.addProduct(request, userPrincipal.getUser());

        return ResponseEntity.ok(updatedCart);
    }

}
