package com.gabriel.SpringEcom.controller;

import com.gabriel.SpringEcom.dto.CartDTO.CartItemRequestDTO;
import com.gabriel.SpringEcom.dto.CartDTO.CartResponseDTO;
import com.gabriel.SpringEcom.security.UserPrincipal;
import com.gabriel.SpringEcom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/carts")
    public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponseDTO response = cartService.getCart(userPrincipal.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addProductToCart(@RequestBody CartItemRequestDTO request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponseDTO updatedCart = cartService.addProduct(request, userPrincipal.getUser());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/item/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long productId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        cartService.removeItemFromCart(productId, userPrincipal.getUser());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        cartService.clearCart(userPrincipal.getUser());
        return ResponseEntity.noContent().build();
    }

}
