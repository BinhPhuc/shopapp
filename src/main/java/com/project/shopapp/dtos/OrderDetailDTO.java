package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "order's id must be > 0!")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "product's id must be > 0")
    private Long productId;

    @Min(value = 1, message = "number of product must be > 0")
    private Long quantity;

    @Min(value = 0, message = "price must be >= 0")
    private Float price;

    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
}
