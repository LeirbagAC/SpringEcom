package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.CartDTO.CartItemRequestDTO;
import com.gabriel.SpringEcom.dto.CartDTO.CartResponseDTO;
import com.gabriel.SpringEcom.exceptions.InactiveProductException;
import com.gabriel.SpringEcom.exceptions.InsufficientStockException;
import com.gabriel.SpringEcom.exceptions.NotFoundException;
import com.gabriel.SpringEcom.mappers.CartMapper;
import com.gabriel.SpringEcom.model.Cart;
import com.gabriel.SpringEcom.model.CartItem;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.repo.CartRepo;
import com.gabriel.SpringEcom.repo.ProductRepo;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final CartMapper cartMapper;

    @Transactional(readOnly = true)
    public CartResponseDTO getCart(User loggedUser) {
        return cartRepo.findByUser(loggedUser)
                .map(cartMapper::mapToCartResponseDTO)
                .orElseGet(() -> new CartResponseDTO(
                        loggedUser.getUsername(),
                        loggedUser.getEmail(),
                        List.of(),
                        BigDecimal.ZERO
                ));
    }

    @Transactional
    public CartResponseDTO addProduct(CartItemRequestDTO request, User loggedUser) {

        Product product = productRepo.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Produto", request.productId()));

        if (!product.isActive()) {
            throw new InactiveProductException(product.getId());
        }

        Cart cart = cartRepo.findByUser(loggedUser).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(loggedUser);
            return cartRepo.save(newCart);
        });

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst(); //"Assim que o primeiro item passar pelo filtro, pare tudo e me entregue ele"

        // Quantidade total que o carrinho terá após a adição
        int quantityInCart = existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int newQuantity = quantityInCart + request.quantity();

        if (newQuantity > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    product.getName(), product.getStockQuantity(), newQuantity);
        }

        if (existingItemOpt.isPresent()) {
            existingItemOpt.get().setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity());
            cart.getItems().add(newItem);
        }

        return cartMapper.mapToCartResponseDTO(cartRepo.save(cart));
    }

    @Transactional
    public void clearCart(User user) {
        cartRepo.findByUser(user).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepo.save(cart);
        });
    }

    @Transactional
    public void removeItemFromCart(Long productId, User user) {
        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Item do carrinho", productId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item do carrinho", productId));

        cart.getItems().remove(item);
    }
}
