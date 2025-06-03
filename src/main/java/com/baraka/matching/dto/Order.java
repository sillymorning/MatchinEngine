package com.baraka.matching.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String timestamp;

    @JsonProperty("asset")
    private String asset;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("direction")
    private OrderDirection direction;

    @JsonProperty("pendingAmount")
    private Double pendingAmount;

    @JsonProperty("trades")
    private List<Trade> trades;

    public void addTrade(Trade trade) {
        this.trades.add(trade);
    }

    public boolean isFullyFilled() {
        return pendingAmount <= 0.0;
    }

}
