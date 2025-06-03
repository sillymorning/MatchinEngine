package com.baraka.matching.service;

import com.baraka.matching.dto.Order;
import com.baraka.matching.dto.OrderDirection;
import com.baraka.matching.dto.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class OrderBookTest {

    private OrderBook orderBook;
    private Long orderIdCounter;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
        orderIdCounter = 0L;
    }

    @Test
    void shouldFullyMatchBuyOrderWithExistingSellOrder() {
        // Existing SELL order
        Order sellOrder = createOrder(100.0, 5.0, OrderDirection.SELL);
        orderBook.match(sellOrder); // Adds to sell book

        // Incoming BUY order that fully matches the SELL order
        Order buyOrder = createOrder(100.0, 5.0, OrderDirection.BUY);
        orderBook.match(buyOrder);

        // Verify sell order is fully filled and removed from book
        assertTrue(sellOrder.isFullyFilled(), "Sell order should be fully filled");
        assertEquals(0.0, sellOrder.getPendingAmount(), "Sell order pending amount should be 0");
        assertEquals(1, sellOrder.getTrades().size(), "Sell order should have 1 trade");

        // Verify buy order is fully filled
        assertTrue(buyOrder.isFullyFilled(), "Buy order should be fully filled");
        assertEquals(0.0, buyOrder.getPendingAmount(), "Buy order pending amount should be 0");
        assertEquals(1, buyOrder.getTrades().size(), "Buy order should have 1 trade");

        // Verify trade details
        Trade buyTrade = buyOrder.getTrades().get(0);
        assertEquals(sellOrder.getId(), buyTrade.getOrderId(), "Buy trade orderId should be sell order's ID");
        assertEquals(5.0, buyTrade.getAmount(), "Buy trade amount should be 5.0");
        assertEquals(100.0, buyTrade.getPrice(), "Buy trade price should be 100.0");

        Trade sellTrade = sellOrder.getTrades().get(0);
        assertEquals(buyOrder.getId(), sellTrade.getOrderId(), "Sell trade orderId should be buy order's ID");
        assertEquals(5.0, sellTrade.getAmount(), "Sell trade amount should be 5.0");
        assertEquals(100.0, sellTrade.getPrice(), "Sell trade price should be 100.0");
    }

    @Test
    void shouldMatchBuyOrderAgainstMultipleSellOrdersFIFO() {
        // Existing SELL orders (FIFO order)
        Order sellOrder1 = createOrder(100.0, 3.0, OrderDirection.SELL);
        orderBook.match(sellOrder1);
        Order sellOrder2 = createOrder(100.0, 5.0, OrderDirection.SELL);
        orderBook.match(sellOrder2);
        Order sellOrder3 = createOrder(101.0, 2.0, OrderDirection.SELL);
        orderBook.match(sellOrder3);

        // Incoming BUY order
        Order buyOrder = createOrder(101.0, 8.0, OrderDirection.BUY);
        orderBook.match(buyOrder);

        // Verify sellOrder1 (ID 0) is fully filled
        assertTrue(sellOrder1.isFullyFilled(), "Sell order 1 should be fully filled");
        assertEquals(0.0, sellOrder1.getPendingAmount());
        assertEquals(1, sellOrder1.getTrades().size());
        assertEquals(buyOrder.getId(), sellOrder1.getTrades().get(0).getOrderId());
        assertEquals(3.0, sellOrder1.getTrades().get(0).getAmount());
        assertEquals(100.0, sellOrder1.getTrades().get(0).getPrice());

        // Verify sellOrder2 (ID 1) is fully filled
        assertTrue(sellOrder2.isFullyFilled(), "Sell order 2 should be fully filled");
        assertEquals(0.0, sellOrder2.getPendingAmount(), "Sell order 2 pending amount should be 0"); // 8.0 - 3.0 = 5.0, then 5.0 - 5.0 = 0.0
        assertEquals(1, sellOrder2.getTrades().size());
        assertEquals(buyOrder.getId(), sellOrder2.getTrades().get(0).getOrderId());
        assertEquals(5.0, sellOrder2.getTrades().get(0).getAmount());
        assertEquals(100.0, sellOrder2.getTrades().get(0).getPrice());

        // Verify sellOrder3 (ID 2) is NOT matched
        assertFalse(sellOrder3.isFullyFilled(), "Sell order 3 should NOT be filled");
        assertEquals(2.0, sellOrder3.getPendingAmount());
        assertTrue(sellOrder3.getTrades().isEmpty());

        // Verify buyOrder (ID 3) is fully filled
        assertTrue(buyOrder.isFullyFilled(), "Buy order should be fully filled");
        assertEquals(0.0, buyOrder.getPendingAmount());
        assertEquals(2, buyOrder.getTrades().size(), "Buy order should have 2 trades");

        List<Trade> buyTrades = buyOrder.getTrades();
        assertEquals(sellOrder1.getId(), buyTrades.get(0).getOrderId());
        assertEquals(3.0, buyTrades.get(0).getAmount());
        assertEquals(100.0, buyTrades.get(0).getPrice());

        assertEquals(sellOrder2.getId(), buyTrades.get(1).getOrderId());
        assertEquals(5.0, buyTrades.get(1).getAmount());
        assertEquals(100.0, buyTrades.get(1).getPrice());

    }

    @Test
    void shouldMatchSellOrderAgainstMultipleBuyOrdersFIFO() {
        // Existing BUY orders (FIFO order)
        Order buyOrder1 = createOrder(100.0, 3.0, OrderDirection.BUY);
        orderBook.match(buyOrder1);
        Order buyOrder2 = createOrder(100.0, 5.0, OrderDirection.BUY);
        orderBook.match(buyOrder2);
        Order buyOrder3 = createOrder(99.0, 2.0, OrderDirection.BUY);
        orderBook.match(buyOrder3);

        // Incoming SELL order
        Order sellOrder = createOrder(99.0, 8.0, OrderDirection.SELL);
        orderBook.match(sellOrder);

        // Verify buyOrder1 (ID 0) is fully filled
        assertTrue(buyOrder1.isFullyFilled());
        assertEquals(0.0, buyOrder1.getPendingAmount());
        assertEquals(1, buyOrder1.getTrades().size());
        assertEquals(sellOrder.getId(), buyOrder1.getTrades().get(0).getOrderId());
        assertEquals(3.0, buyOrder1.getTrades().get(0).getAmount());
        assertEquals(100.0, buyOrder1.getTrades().get(0).getPrice());

        // Verify buyOrder2 (ID 1) is fully filled
        assertTrue(buyOrder2.isFullyFilled());
        assertEquals(0.0, buyOrder2.getPendingAmount());
        assertEquals(1, buyOrder2.getTrades().size());
        assertEquals(sellOrder.getId(), buyOrder2.getTrades().get(0).getOrderId());
        assertEquals(5.0, buyOrder2.getTrades().get(0).getAmount());
        assertEquals(100.0, buyOrder2.getTrades().get(0).getPrice());

        // Verify buyOrder3 (ID 2) is NOT matched
        assertFalse(buyOrder3.isFullyFilled());
        assertEquals(2.0, buyOrder3.getPendingAmount());
        assertTrue(buyOrder3.getTrades().isEmpty());

        // Verify sellOrder (ID 3) is fully filled
        assertTrue(sellOrder.isFullyFilled());
        assertEquals(0.0, sellOrder.getPendingAmount());
        assertEquals(2, sellOrder.getTrades().size());

        // Verify sellOrder trades
        List<Trade> sellTrades = sellOrder.getTrades();
        assertEquals(buyOrder1.getId(), sellTrades.get(0).getOrderId());
        assertEquals(3.0, sellTrades.get(0).getAmount());
        assertEquals(100.0, sellTrades.get(0).getPrice());

        assertEquals(buyOrder2.getId(), sellTrades.get(1).getOrderId());
        assertEquals(5.0, sellTrades.get(1).getAmount());
        assertEquals(100.0, sellTrades.get(1).getPrice());


    }

    @Test
    void shouldHandleNoMatchWhenPricesNotFavorable() {
        Order sellOrder = createOrder(105.0, 10.0, OrderDirection.SELL);
        orderBook.match(sellOrder); // Sell at 105

        Order buyOrder = createOrder(100.0, 5.0, OrderDirection.BUY);
        orderBook.match(buyOrder); // Buy at 100

        // No match should occur (buy price < sell price)
        assertEquals(10.0, sellOrder.getPendingAmount());
        assertEquals(5.0, buyOrder.getPendingAmount());
        assertTrue(sellOrder.getTrades().isEmpty());
        assertTrue(buyOrder.getTrades().isEmpty());

    }


    private Order createOrder(Double price, Double amount, OrderDirection direction) {
        return new Order(
                orderIdCounter++,
                LocalDateTime.now().toString(),
                "BTC",
                price,
                amount,
                direction,
                amount,
                new ArrayList<>()
        );
    }

}
