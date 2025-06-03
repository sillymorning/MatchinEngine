package com.baraka.matching.service;

import com.baraka.matching.dto.OrderRequest;
import com.baraka.matching.dto.OrderResponse;

public interface OrderService {


    OrderResponse placeOrder(OrderRequest request);

    OrderResponse getOrder(Long orderId);
}
