package com.baraka.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest (
    @JsonProperty("asset")
    @NotBlank(message = "Asset cannot be blank")
    String asset,

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be non-negative")
    @JsonProperty("price") Double price,

    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount must be non-negative")
    @JsonProperty("amount") Double amount,

    @JsonProperty("direction")
    @NotNull(message = "Direction cannot be null")
    OrderDirection direction
    ){}
