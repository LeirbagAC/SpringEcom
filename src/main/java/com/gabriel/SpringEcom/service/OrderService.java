package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductRequestDTO;
import com.gabriel.SpringEcom.model.Order;
import com.gabriel.SpringEcom.model.OrderItem;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderItemRequest;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderItemResponse;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderRequest;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponse;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.repo.OrderRepo;
import com.gabriel.SpringEcom.repo.ProductRepo;
import com.gabriel.SpringEcom.repo.UserRepo;
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
    private final ProductRepo productRepo;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request, User loggedUser) {
        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderId(orderId);
        order.setEmail(loggedUser.getEmail());
        order.setCustomName(loggedUser.getUsername());
        order.setStatus("Pedido Feito!");
        order.setOrderDate(LocalDate.now());

        order.setUser(loggedUser);

        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest itemReq : request.items()) {

            Product product = productRepo.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if(product.getStockQuantity() < itemReq.quantity()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());
//            productRepo.save(product); Novamente, isso é redundante por conta do Dirty Checking causado pelo Transactional

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())))
                    .order(order)
                    .build();

            orderItems.add(orderItem);

        }

        order.setOrderItems(orderItems);
        Order savedOrder =  orderRepo.save(order);

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
