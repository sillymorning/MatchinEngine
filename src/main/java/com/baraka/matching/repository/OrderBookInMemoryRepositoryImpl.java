package com.baraka.matching.repository;

import com.baraka.matching.dto.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderBookInMemoryRepositoryImpl implements OrderBookRepository{

    private final ConcurrentHashMap<Long, Order> allOrders = new ConcurrentHashMap<>();

    public void save(Order order) {
        allOrders.put(order.getId(), order);
    }

    public Optional<Order> getOrderById(Long id){
        return Optional.ofNullable(allOrders.get(id));
    }


}
