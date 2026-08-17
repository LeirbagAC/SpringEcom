package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.CartDTO.CartItemRequestDTO;
import com.gabriel.SpringEcom.dto.CartDTO.CartResponseDTO;
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
        if (request.quantity() <= 0) throw new RuntimeException("Quantidade deve ser maior que zero.");

        //Busca o carrinho do usuário, se não existe ele cria um.
        Cart cart = cartRepo.findByUser(loggedUser).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(loggedUser);
            return cartRepo.save(newCart);
        });

        Product product = productRepo.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if(!product.isActive()) throw new RuntimeException("Este produto precisa está ativo no catálogo.");

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

        Cart savedCart  = cartRepo.save(cart);
        return cartMapper.mapToCartResponseDTO(savedCart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepo.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário"));

        cart.getItems().clear();
        cartRepo.save(cart);
    }

    @Transactional
    public void removeItemFromCart(Long productId, User user) {
        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho"));

        cart.getItems().remove(item);
        cartRepo.save(cart);
    }
}
