package com.example.pro.service;

import com.example.pro.dto.RecipeIngredientsDTO;

import java.util.List;

public interface RecipeIngredientsService {
    List<RecipeIngredientsDTO> getRecipeIngredientsbyRecipeId(Long recipeId);
}
