package com.kumar.backend.Request;

import com.kumar.backend.Utils.Enums.OrderType;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private OrderType orderType;
}
