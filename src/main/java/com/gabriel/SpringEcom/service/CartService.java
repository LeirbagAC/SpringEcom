package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.CartDTO.CartItemRequestDTO;
import com.gabriel.SpringEcom.dto.CartDTO.CartItemResponseDTO;
import com.gabriel.SpringEcom.dto.CartDTO.CartResponseDTO;
import com.gabriel.SpringEcom.model.Cart;
import com.gabriel.SpringEcom.model.CartItem;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.repo.CartRepo;
import com.gabriel.SpringEcom.repo.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;
    private final ProductRepo productRepo;

    @Transactional
    public CartResponseDTO addProduct(CartItemRequestDTO request, User loggedUser) {

        //Busca o carrinho do usuário, se não existe ele cria um.
        Cart cart = cartRepo.findByUser(loggedUser).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(loggedUser);
            return cartRepo.save(newCart);
        });

        Product product = productRepo.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst(); //"Assim que o primeiro item passar pelo filtro, pare tudo e me entregue ele"

        if(existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.quantity();

            if(newQuantity > product.getStockQuantity() ) {
                throw new RuntimeException("Estoque insuficiente para a quantidade solicitada.");
            }
            existingItem.setQuantity(newQuantity);

        } else {

            if(request.quantity() > product.getStockQuantity()) {
                throw new RuntimeException("Estoque insuficiente.");
            }

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity());

            cart.getItems().add(newItem);
        }

        Cart savdCart  = cartRepo.save(cart);
        return mapToCartResponseDTO(savdCart);
    }

    private CartResponseDTO mapToCartResponseDTO(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(this::mapToItemDTO)
                .toList();

        BigDecimal totalPrice = items.stream()
                .map(CartItemResponseDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDTO(
                cart.getUser().getUsername(),
                cart.getUser().getEmail(),
                items,
                totalPrice
        );
    }

    private CartItemResponseDTO mapToItemDTO(CartItem item) {
        BigDecimal unitPrice = item.getProduct().getPrice();
        BigDecimal subTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));

        return new CartItemResponseDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                unitPrice,
                subTotal
        );
    }

}
