package com.baraka.matching.service;

import com.baraka.matching.dto.Order;
import com.baraka.matching.dto.OrderDirection;
import com.baraka.matching.dto.OrderRequest;
import com.baraka.matching.dto.OrderResponse;
import com.baraka.matching.exception.OrderNotFoundException;
import com.baraka.matching.repository.OrderBookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderBookRepository orderBookRepository;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Test
    void testPlaceOrder_shouldReturnOrderResponseAndSaveOrder() {
        OrderRequest request = new OrderRequest("BTC", 150.0, 10.0, OrderDirection.BUY);

        OrderResponse response = orderServiceImpl.placeOrder(request);

        assertNotNull(response);
        assertEquals("BTC", response.asset());
        assertEquals(150.0, response.price());
        assertEquals(10.0, response.amount());
        assertEquals(OrderDirection.BUY, response.direction());
        assertEquals(10.0, response.pendingAmount());
        assertNotNull(response.id());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderBookRepository, times(1)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertEquals(response.id(), savedOrder.getId());
        assertEquals("BTC", savedOrder.getAsset());
        assertEquals(OrderDirection.BUY, savedOrder.getDirection());
    }

    @Test
    void shouldGetExistingOrder() {
        Order order = new Order(1L, "2024-01-01T00:00:00Z", "BTC", 150.0, 10.0, OrderDirection.SELL, 5.0, List.of());
        when(orderBookRepository.getOrderById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderServiceImpl.getOrder(1L);

        assertEquals(1L, response.id());
        assertEquals("BTC", response.asset());
        assertEquals(OrderDirection.SELL, response.direction());
        verify(orderBookRepository, times(1)).getOrderById(1L);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {
        when(orderBookRepository.getOrderById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderServiceImpl.getOrder(999L));
        verify(orderBookRepository, times(1)).getOrderById(999L);
    }
}
