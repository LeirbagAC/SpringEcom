package com.gabriel.SpringEcom.mappers;

import com.gabriel.SpringEcom.dto.OrderDTO.OrderItemResponseDTO;
import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponseDTO;
import com.gabriel.SpringEcom.model.Order;
import com.gabriel.SpringEcom.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "customName", target = "customerName")
    @Mapping(source = "orderItems", target = "items")
    OrderResponseDTO toOrderResponse(Order order);

    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDTO toOrderItemResponse(OrderItem item);

    List<OrderResponseDTO> toOrderResponseList(List<Order> orders);

    default List<OrderItemResponseDTO> mapOrderItems(List<OrderItem> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .filter(item -> item.getProduct() != null)
                .map(this::toOrderItemResponse)
                .toList();
    }
}