package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.mappers.OrderMapper;
import com.gabriel.SpringEcom.model.*;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponse;
import com.gabriel.SpringEcom.repo.CartRepo;
import com.gabriel.SpringEcom.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse placeOrder(User loggedUser) {
        Cart cart = cartRepo.findByUser(loggedUser)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        if(cart.getItems().isEmpty()) throw new RuntimeException("Não é possível finalizar a compra com o carrinho vazio.");

        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderId(orderId);
        order.setEmail(loggedUser.getEmail());
        order.setCustomName(loggedUser.getUsername());
        order.setStatus("Pedido Feito!");
        order.setOrderDate(LocalDate.now());
        order.setUser(loggedUser);

        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if(!product.isActive()) throw new RuntimeException("O produto não está ativo no catálogo: " + product.getName());
            if(product.getStockQuantity() < cartItem.getQuantity()) throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName());

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
//            productRepo.save(product); Novamente, isso é redundante por conta do Dirty Checking causado pelo Transactional

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .order(order)
                    .build();

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        Order savedOrder =  orderRepo.save(order);
        cart.getItems().clear();

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrderResponses() {
        List<Order> orders = orderRepo.findAll();
        return orderMapper.toOrderResponseList(orders);
    }
}
