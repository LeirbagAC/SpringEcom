package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.exceptions.EmptyCartException;
import com.gabriel.SpringEcom.exceptions.InactiveProductException;
import com.gabriel.SpringEcom.exceptions.InsufficientStockException;
import com.gabriel.SpringEcom.exceptions.NotFoundException;
import com.gabriel.SpringEcom.mappers.OrderMapper;
import com.gabriel.SpringEcom.model.*;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponse;
import com.gabriel.SpringEcom.repo.CartRepo;
import com.gabriel.SpringEcom.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new NotFoundException("Carrinho", loggedUser.getExternalId()));

        if(cart.getItems().isEmpty()) throw new EmptyCartException();

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

            if(!product.isActive()) throw new InactiveProductException(product.getId());
            if(product.getStockQuantity() < cartItem.getQuantity()) throw new InsufficientStockException(product.getName(), product.getStockQuantity(), cartItem.getQuantity());

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
    public Page<OrderResponse> getAllOrderResponses(User loggedUser, Pageable pageable) {
        return orderRepo.findByUser(loggedUser, pageable)
                .map(orderMapper::toOrderResponse);
    }
}
