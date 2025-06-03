package com.baraka.matching.controller;

import com.baraka.matching.dto.OrderRequest;
import com.baraka.matching.dto.OrderResponse;
import com.baraka.matching.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeAnOrder(@RequestBody final OrderRequest request) {
        OrderResponse order = orderService.placeOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable final Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

}
