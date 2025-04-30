package com.example.pro.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientsDTO {
    private Long id;
    @NotEmpty
    private Long recipeId;
    @NotEmpty
    private IngredientDTO ingredient;
    @NotEmpty
    private Long quantity;
    @NotEmpty
    private Long cartId;

}
