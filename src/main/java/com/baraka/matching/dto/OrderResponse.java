package com.baraka.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record OrderResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("timestamp") String timestamp,
        @JsonProperty("asset") String asset,
        @JsonProperty("price") Double price,
        @JsonProperty("amount") Double amount,
        @JsonProperty("direction") OrderDirection direction,
        @JsonProperty("pendingAmount") Double pendingAmount,
        @JsonProperty("trades") List<Trade> trades
) {
    public static OrderResponse fromOrder(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTimestamp(),
                order.getAsset(),
                order.getPrice(),
                order.getAmount(),
                order.getDirection(),
                order.getPendingAmount(),
                order.getTrades()
        );
    }
}