package com.baraka.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("price")
    private Double price;
}
