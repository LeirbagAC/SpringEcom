package com.gabriel.SpringEcom.dto.OrderDTO;

import java.util.List;

public record OrderRequest(
        String customerName,
        String email,
        List<OrderItemRequest> items
) {
}
