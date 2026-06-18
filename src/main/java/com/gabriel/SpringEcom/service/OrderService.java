package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.model.Order;
import com.gabriel.SpringEcom.model.OrderItem;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.dto.OrderItemRequest;
import com.gabriel.SpringEcom.model.dto.OrderItemResponse;
import com.gabriel.SpringEcom.model.dto.OrderRequest;
import com.gabriel.SpringEcom.model.dto.OrderResponse;
import com.gabriel.SpringEcom.repo.OrderRepo;
import com.gabriel.SpringEcom.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductRepo productRepo;

    public OrderResponse placeOrder(OrderRequest request) {

        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderId(orderId);
        order.setCustomName(request.customerName());
        order.setEmail(request.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());

        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest itemReq : request.items()) {

            Product product = productRepo.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());
            productRepo.save(product);

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
