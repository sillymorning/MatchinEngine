package com.baraka.matching.controller;

import com.baraka.matching.dto.OrderDirection;
import com.baraka.matching.dto.OrderRequest;
import com.baraka.matching.dto.OrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPlaceOrder() throws Exception {
        OrderRequest request = new OrderRequest("BTC", 150.0, 10.0, OrderDirection.BUY);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.asset", is("BTC")))
                .andExpect(jsonPath("$.price", is(150.0)))
                .andExpect(jsonPath("$.amount", is(10.0)))
                .andExpect(jsonPath("$.direction", is("BUY")))
                .andExpect(jsonPath("$.pendingAmount", is(10.0)));
    }

    @Test
    void testGetOrderById() throws Exception {
        OrderRequest request = new OrderRequest("DODGE", 120.0, 5.0, OrderDirection.SELL);

        String response = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponse placedOrder = objectMapper.readValue(response, OrderResponse.class);

        mockMvc.perform(get("/orders/" + placedOrder.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(placedOrder.id().intValue())))
                .andExpect(jsonPath("$.asset", is("DODGE")))
                .andExpect(jsonPath("$.amount", is(5.0)))
                .andExpect(jsonPath("$.pendingAmount", is(5.0)))
                .andExpect(jsonPath("$.direction", is("SELL")));
    }

}
