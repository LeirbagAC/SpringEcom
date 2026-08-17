package com.gabriel.SpringEcom.mappers;

import com.gabriel.SpringEcom.dto.OrderDTO.OrderItemResponse;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponse;
import com.gabriel.SpringEcom.model.Order;
import com.gabriel.SpringEcom.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "customName", target = "customerName")
    @Mapping(source = "orderItems", target = "items")
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toOrderItemResponse(OrderItem item);

    List<OrderResponse> toOrderResponseList(List<Order> orders);

    default List<OrderItemResponse> mapOrderItems(List<OrderItem> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .filter(item -> item.getProduct() != null)
                .map(this::toOrderItemResponse)
                .toList();
    }
}