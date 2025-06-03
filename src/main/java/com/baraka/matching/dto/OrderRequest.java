package com.baraka.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderRequest (
    @JsonProperty("asset") String asset,
    @JsonProperty("price") Double price,
    @JsonProperty("amount") Double amount,
    @JsonProperty("direction") OrderDirection direction
    ){}
