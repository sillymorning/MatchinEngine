package com.baraka.matching.service;

import com.baraka.matching.dto.Order;
import com.baraka.matching.dto.OrderRequest;
import com.baraka.matching.dto.OrderResponse;
import com.baraka.matching.exception.OrderNotFoundException;
import com.baraka.matching.repository.OrderBookRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderBookRepository orderBookRepository;
    private final AtomicLong orderIdCounter = new AtomicLong(0);
    private final Map<String, OrderBook> orderBooks = new ConcurrentHashMap<>();

    public OrderServiceImpl(OrderBookRepository orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }


    public OrderResponse placeOrder(OrderRequest request) {
        OrderBook currentAsset = getCurrentAsset(request);
        Order order = createOrder(request);
        currentAsset.match(order);
        orderBookRepository.save(order);
        return OrderResponse.fromOrder(order);
    }


    public OrderResponse getOrder(Long orderId) {
        Order order = orderBookRepository.getOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Invalid " + orderId));
        return OrderResponse.fromOrder(order);
    }

    private Order createOrder(OrderRequest request) {
        Long newOrderId = orderIdCounter.getAndIncrement();
        return new Order(
                newOrderId,
                Instant.now().toString(),
                request.asset(),
                request.price(),
                request.amount(),
                request.direction(),
                request.amount(),
                new ArrayList<>()
        );

    }


    private OrderBook getCurrentAsset(OrderRequest request) {
        return orderBooks.computeIfAbsent(
                request.asset(),
                k -> new OrderBook()
        );
    }
}
