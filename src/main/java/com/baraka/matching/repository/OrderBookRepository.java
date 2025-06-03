package com.baraka.matching.repository;

import com.baraka.matching.dto.Order;

import java.util.Optional;

public interface OrderBookRepository {

     void save(Order order);
     Optional<Order> getOrderById(Long id);

}
