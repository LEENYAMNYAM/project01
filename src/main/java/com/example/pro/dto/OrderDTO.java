package com.example.pro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String orderNumber;
    private LocalDateTime orderDate;
    private int itemsAmount;
    private int shippingFee;
    private int totalAmount;
    private int usedPoints;
    private int earnedPoints;
    private String paymentMethod;
    private String shippingAddress;
    private String shippingAddressDetail;
    private String deliveryRequest;
    private List<RecipeIngredientsDTO> orderItems;
}
