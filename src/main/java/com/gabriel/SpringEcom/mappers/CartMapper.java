package com.gabriel.SpringEcom.mappers;

import com.gabriel.SpringEcom.dto.CartDTO.CartItemResponseDTO;
import com.gabriel.SpringEcom.dto.CartDTO.CartResponseDTO;
import com.gabriel.SpringEcom.model.Cart;
import com.gabriel.SpringEcom.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = {BigDecimal.class})
public interface CartMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(target = "totalPrice", expression = "java(calculateTotal(cart.getItems()))")
    CartResponseDTO mapToCartResponseDTO(Cart cart);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "unitPrice")
    @Mapping(target = "subtotal", expression = "java(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))")
    CartItemResponseDTO mapToItemDTO(CartItem item);

    default BigDecimal calculateTotal(List<CartItem> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
