package com.example.pro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDTO {
    private Long id;
    private String ingredientName;
    private String productId;
    private Long price;
    private String mallLink;
    private String mallName;
    private String imageUrl;
}
