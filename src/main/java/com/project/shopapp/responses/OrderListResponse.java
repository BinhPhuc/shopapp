package com.project.shopapp.responses;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class OrderListResponse {
    List<OrderResponse> orderResponseList;
    int totalPages;
}
