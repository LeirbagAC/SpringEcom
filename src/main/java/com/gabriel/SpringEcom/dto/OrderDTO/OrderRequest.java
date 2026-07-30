package com.gabriel.SpringEcom.dto.OrderDTO;

import java.util.List;

public record OrderRequest(
        List<OrderItemRequest> items
) {
}
