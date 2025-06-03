package com.baraka.matching.service;

import com.baraka.matching.dto.Order;
import com.baraka.matching.dto.OrderDirection;
import com.baraka.matching.dto.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

@Component
@Slf4j
public class OrderBook {

    private final ConcurrentSkipListMap<Double, Queue<Order>> buyOrders = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

    private final ConcurrentSkipListMap<Double, Queue<Order>> sellOrders = new ConcurrentSkipListMap<>();

    private final ReentrantLock lock = new ReentrantLock();


    public void match(Order order) {
        lock.lock();
        try {
            // Match BUY order against SELL orders
            if (order.getDirection() == OrderDirection.BUY) {
                matchOrderWithOppositeDirection(order, sellOrders,
                        (sellPrice) -> order.getPrice() >= sellPrice,
                        (currentPrice) -> currentPrice > order.getPrice()
                );
            } else {
                // Match SELL order against BUY orders
                matchOrderWithOppositeDirection(order, buyOrders,
                        (buyPrice) -> order.getPrice() <= buyPrice,
                        (currentPrice) -> currentPrice < order.getPrice()
                );
            }

            // If the new order is not fully filled, add it to its respective book
            if (!order.isFullyFilled()) {
                ConcurrentSkipListMap<Double, Queue<Order>> bookToAdd =
                        (order.getDirection() == OrderDirection.BUY) ? buyOrders : sellOrders;
                bookToAdd.computeIfAbsent(order.getPrice(), k -> new ConcurrentLinkedQueue<>()).add(order);
            } else {
                log.info("Order {} fully filled after matching.", order.getId());
            }
        } finally {
            lock.unlock();
        }

    }

    private void matchOrderWithOppositeDirection(
            Order incomingOrder,
            ConcurrentSkipListMap<Double, Queue<Order>> oppositeBook,
            Predicate<Double> matchPriceCondition,
            Predicate<Double> stopIterationCondition) {


        Iterator<Map.Entry<Double, Queue<Order>>> iterator = oppositeBook.entrySet().iterator();

        while (iterator.hasNext() && incomingOrder.getPendingAmount() > 0) {
            Map.Entry<Double, Queue<Order>> entry = iterator.next();
            Double bookPrice = entry.getKey();
            Queue<Order> ordersAtPrice = entry.getValue();

            if (!matchPriceCondition.test(bookPrice)) {
                if (stopIterationCondition.test(bookPrice)) {
                    break;
                }
                continue;
            }

            while (!ordersAtPrice.isEmpty() && incomingOrder.getPendingAmount() > 0) {
                Order counterOrder = ordersAtPrice.peek();

                if (counterOrder == null) {
                    ordersAtPrice.poll();
                    continue;
                }

                Double tradedAmount = Math.min(incomingOrder.getPendingAmount(), counterOrder.getPendingAmount());

                if (tradedAmount > 0) {
                    // Create trades
                    Trade incomingOrderTrade = new Trade(counterOrder.getId(), tradedAmount, bookPrice);
                    Trade counterOrderTrade = new Trade(incomingOrder.getId(), tradedAmount, bookPrice);

                    // Update pending amounts
                    incomingOrder.setPendingAmount(incomingOrder.getPendingAmount() - tradedAmount);
                    counterOrder.setPendingAmount(counterOrder.getPendingAmount() - tradedAmount);

                    // Add trades to both orders
                    incomingOrder.addTrade(incomingOrderTrade);
                    counterOrder.addTrade(counterOrderTrade);

                    log.debug("Matched order {} ({}) with order {} ({}) for {} at price {}",
                            incomingOrder.getId(), incomingOrder.getDirection(),
                            counterOrder.getId(), counterOrder.getDirection(),
                            tradedAmount, bookPrice);

                    if (counterOrder.isFullyFilled()) {
                        ordersAtPrice.poll();
                    }
                } else {
                    ordersAtPrice.poll();
                }
            }

            if (ordersAtPrice.isEmpty()) {
                iterator.remove();
            }
        }
    }
}
