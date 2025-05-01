package com.example.pro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private Long id;

    private Long ingredientId;

    private Long recipeId;

    private Long cartId;

    private Long quantity;

    private IngredientDTO ingredient;

    private RecipeDTO recipe;

}
