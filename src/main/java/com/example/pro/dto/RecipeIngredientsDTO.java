package com.example.pro.dto;

import com.example.pro.entity.IngredientEntity;
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

}
