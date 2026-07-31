package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.model.*;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderItemResponse;
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

        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrderResponses() {
        return orderRepo.findAll().stream()
                .map(this::mapToOrderResponse)
                .toList();

    }

    //Para converte para o formato do DTO
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProduct() == null) {
                    continue;
                }

                itemResponses.add(new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getTotalPrice(),
                        item.getQuantity()
                ));
            }
        }

        return new OrderResponse(
                order.getOrderId(),
                order.getCustomName(),
                order.getEmail(),
                order.getStatus(),
                order.getOrderDate(),
                itemResponses
        );

    }
}
